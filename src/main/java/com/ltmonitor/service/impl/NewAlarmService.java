package com.ltmonitor.service.impl;

import com.ltmonitor.entity.Alarm;
import com.ltmonitor.entity.AlarmRecord;
import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.service.IAlarmConfigService;
import com.ltmonitor.service.ILocationService;
import com.ltmonitor.service.INewAlarmService;
import com.ltmonitor.util.StringUtil;
import net.fxft.cloud.streamdata.StreamDataBufferProcessor;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.util.BasicUtil;
import net.fxft.gatewaybusi.service.IRealDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.LongAdder;

/**
 * 报警入库服务
 *
 * @author v5-552
 */

@Service("newAlarmService")
public class NewAlarmService implements INewAlarmService {

    private static Logger log = LoggerFactory.getLogger(NewAlarmService.class);


    private ConcurrentLinkedQueue<Alarm> dataQueue = new ConcurrentLinkedQueue<Alarm>();

    private volatile boolean continueProcess = true;

    private Thread processAlarmThread;
    @Autowired
    private ILocationService locationService;
    @Autowired
    private IAlarmConfigService alarmConfigService;

    @Autowired
    private IRealDataService realDataService;

//    @Resource(name = "jmsTemplate")
//    private JmsTemplate jmsTemplate;
//
//    // 队列名gzframe.demo
//    @Resource(name = "alarmQueueDestination")
//    private Destination alarmQueueDestination;


    private LongAdder alarmCount = new LongAdder();

    private LongAdder insertCount = new LongAdder();

    private StreamDataBufferProcessor<Alarm> processor;

    @Autowired
    private JdbcUtil jdbc;

    public NewAlarmService() {
    }


    @PostConstruct
    public void start() {
        processor = new StreamDataBufferProcessor<>("InsertAlarm", 3, 2000, 3, 2000);
        processor.addSerialProcess(alarmList -> {
            jdbc.insertList(alarmList)
                    .setLog(log, "告警批量入库")
                    .insertColumn(ColumnSet.all().minus("deleted"))
                    .executeBatch(true);
            insertCount.add(alarmList.size());
        });
        processAlarmThread = new Thread(new Runnable() {
            public void run() {
                processRealDataThreadFunc();
            }
        }, "NewAlarmService");
        processAlarmThread.start();
        log.info("NewAlarmService处理线程已启动！");
        new Thread(()->{
            while (true) {
                try {
                    log.debug("当前报警处理队列数量为"+processor.getExecPoolQueueSize()+"条");
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @PreDestroy
    public void stopService() {
        log.info("---begin stop NewAlarmService---");
        continueProcess = false;
        try {
            processAlarmThread.join(50000);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        // processRealDataThread.stop();
        log.info("---end stop NewAlarmService---");
    }

    public void enQueue(Alarm newAlarm, String remark) {
        alarmCount.increment();
        if (newAlarm.getRemark() == null) {
            newAlarm.setRemark(remark);
        }
        if (BasicUtil.isEmpty(newAlarm.getLocation())) {
            if (newAlarm.getLatitude() > 0 && newAlarm.getLongitude() > 0) {
                String location = locationService.getLocation(newAlarm.getLatitude(), newAlarm.getLongitude(), "告警入库前！simNo=" + newAlarm.getSimNo());
                log.debug("insertAlarm查询location! location=" + location + "; simNO=" + newAlarm.getSimNo());
                newAlarm.setLocation(location);
            }
        }

        processor.addData(newAlarm);

        if (dataQueue.size() > 200) {
            //这个仅用于告警推送前端
            log.error("报警队列中数量过多:" + dataQueue.size());
            this.dataQueue.clear();
        }
        dataQueue.add(newAlarm);
    }

    private void processRealDataThreadFunc() {
        int count = 0;
        while (continueProcess) {
            try {
                Alarm tm = dataQueue.poll();
                if (tm != null) {
                    final List<Alarm> msgList = new ArrayList<Alarm>();
                    while (tm != null) {
                        msgList.add(tm);
                        if (msgList.size() > 1000)
                            break;
                        tm = dataQueue.poll();

                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (count % 300 == 0) {

                count = 0;
            }

            if (dataQueue.size() == 0) {
                try {
                    if (continueProcess) {
                        Thread.sleep(100L);
                    }
                } catch (InterruptedException e1) {
                }
            }
        }
    }

    /**
     * 获取长度为32个字符的UUID字符串
     *
     * @return
     */
    public static String getUUID32() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        return uuid;
    }


    public Alarm insertAlarm(String alarmSource, String alarmType, GPSRealData rd, String remark) {
        try {
            Alarm ar = new Alarm();
            ar.setVehicleId(rd.getVehicleId());
            ar.setSimNo(rd.getSimNo());
            ar.setPlateNo(rd.getPlateNo());
            if (StringUtil.isNullOrEmpty(ar.getAdasAlarmNo())) {
                ar.setAdasAlarmNo(getUUID32());
            }

            Date alarmTime = rd.getSendTime();
            if (alarmTime == null || AlarmRecord.TYPE_ONLINE.equals(alarmType) || AlarmRecord.TYPE_OFFLINE.equals(alarmType))
                alarmTime = rd.getOnlineDate();
            ar.setAlarmTime(new Date());
            ar.setCreateDate(alarmTime);
//            ar.setAckSn(rd.getResponseSn());
            ar.setLatitude(rd.getLatitude());
            ar.setLongitude(rd.getLongitude());
            ar.setSpeed(rd.getVelocity());
            ar.setAlarmType(alarmType);
            ar.setAlarmSource(alarmSource);
            ar.setLocation(rd.getLocation());
//            String alarmTalbeName = Constants.getAlarmTableName();
//            ar.setTableName(alarmTalbeName);
            this.enQueue(ar, remark);
            return  ar;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return  null;
        }
    }



}
