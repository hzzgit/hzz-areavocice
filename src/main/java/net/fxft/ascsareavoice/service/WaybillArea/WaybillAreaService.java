package net.fxft.ascsareavoice.service.WaybillArea;

import com.ltmonitor.entity.Alarm;
import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.service.INewAlarmService;
import com.ltmonitor.service.MapFixService;
import com.ltmonitor.util.Constants;
import com.ltmonitor.util.ConverterUtils;
import com.ltmonitor.util.TimeUtils;
import com.ltmonitor.vo.PointLatLng;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.kafka.KafkaMessageSender;
import net.fxft.ascsareavoice.vo.WaybillAreaMainVo;
import net.fxft.ascsareavoice.vo.WaybillAreaPointVo;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.alarm.AreaAlarmEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ：hzz
 * @description：运单围栏处理类
 * @date ：2020/8/5 15:26
 */
@Service
@Slf4j
public class WaybillAreaService {

    /**
     * 是否开启运单围栏报警
     */
    @Value("${isWaybillArea:false}")
    private boolean isWaybillArea;

    //运单围栏的点位圆圈半径
    @Value("${WaybillArearadius:200}")
    private int WaybillArearadius;
    /**
     * 处理运单围栏的独立队列
     */
    private ConcurrentLinkedQueue<GPSRealData> AreaQueue = new ConcurrentLinkedQueue();


    /**
     * 用来判断进出围栏的情况，如果已经进入围栏则不再报警，直到出去之后
     */
    private ConcurrentMap<String, Boolean> CrossMap = new ConcurrentHashMap<>();


    @Autowired
    private WaybillAreaCache waybillAreaCache;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private INewAlarmService newAlarmService;


    public Boolean getcrosstype(String key) {
        if (CrossMap.containsKey(key)) {
            return CrossMap.get(key);
        }
        return false;
    }

    public void setCrossMap(ConcurrentMap<String, Boolean> crossMap) {
        CrossMap = crossMap;
    }

    /**
     * 定时写入缓存，初始化读取缓存
     */
    @PostConstruct
    private void checkfilecache() {
        CrossMap = WaybillAreaautoCache.loadCache();
        new Thread(() -> {
            while (true) {
                WaybillAreaautoCache.saveCache(CrossMap);
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 加入队列
     *
     * @param rd
     */
    public void addAreaqueue(GPSRealData rd) {
        if (isWaybillArea) {
            if (rd.getSendTime() == null || rd.getLongitude() <= 0 || rd.getLatitude() <= 0 ||
                    TimeUtils.isdifferminute(rd.getSendTime(), new Date(), 1200)) {
                return;
            }
            if (!waybillAreaCache.isWaybillArea(rd.getSimNo())) {
                return;
            }
            AreaQueue.add(rd);

        }
    }


    @PostConstruct
    private void processorAreaAlarm() {
        if (isWaybillArea) {
            new Thread(() -> {
                while (true) {
                    try {

                        int qs = AreaQueue.size();
                        if (qs > 100) {
                            log.debug("处理运单围栏报警队列" + AreaQueue + "排队等待应答数量:" + qs);
                        }
                        GPSRealData rd = AreaQueue.poll();
                        int k = 0;
                        while (rd != null) {
                            if (waybillAreaCache.isWaybillArea(rd.getSimNo())) {
                                log.debug("开始判断运单围栏报警,simNo" + rd.getSimNo());
                                try {
                                    analyze(rd);
                                } catch (Exception e) {
                                    log.error("计算运单围栏异常,rd：" + rd.toString(), e);
                                }
                            }
                            rd = AreaQueue.poll();
                        }
                    } catch (Exception ex) {
                        log.error("围栏报警队列处理线程异常", ex);
                    }

                    if (AreaQueue.size() == 0) {
                        try {//每隔五秒检测一次队列处理线程
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }).start();

        }
    }


    /**
     * 开始处理计算运单围栏
     *
     * @param rd
     */
    private void analyze(GPSRealData rd) throws Exception {
        String simNo = rd.getSimNo();
        WaybillAreaMainVo waybillAreaMainVo = waybillAreaCache.searchbysimNo(simNo);
        long orderid = waybillAreaMainVo.getId();//订单的主键


        Date startTime = waybillAreaMainVo.getStartTime();//开始时间
        Date endTime = waybillAreaMainVo.getEndTime();//结束时间
        long userid = waybillAreaMainVo.getUserid();//用户id
        String name = waybillAreaMainVo.getName();//订单名称
        int bytime = waybillAreaMainVo.getBytime();//是否根据时间
        if (bytime == 1) {//判断是否根据时间进行执行订单
            if (!TimeUtils.isEffectiveDate(rd.getSendTime(), startTime, endTime)) {//如果不在时间范围内
                return;
            }
        }
        List<WaybillAreaPointVo> waybillAreaPointVos = waybillAreaMainVo.getWaybillAreaPointVos();
        if (ConverterUtils.isList(waybillAreaPointVos)) {
            for (WaybillAreaPointVo waybillAreaPointVo : waybillAreaPointVos) {
                Long pointid = waybillAreaPointVo.getId();
                double latitude = ConverterUtils.toDouble(waybillAreaPointVo.getLatitude());
                double longitude = ConverterUtils.toDouble(waybillAreaPointVo.getLongitude());
                String maptype = waybillAreaPointVo.getMaptype();//地图类型 gps:天地图坐标，baidu:百度坐标，google:谷歌地图
                Long pointtype = waybillAreaPointVo.getPointtype();//点位类型,1，开始点，2，途经点，3，结束点

                PointLatLng mp = new PointLatLng();
                //根据区域的地图类型，将GPS终端的坐标转成对应的地图类型，进行比对判断
                if (Constants.MAP_BAIDU.equals(maptype)) {
                    mp = MapFixService.fix(rd.getLatitude(), rd.getLongitude(), Constants.MAP_BAIDU);
                } else if (Constants.MAP_GPS.equals(maptype)) {
                    mp = new PointLatLng(rd.getLongitude(), rd.getLatitude());
                } else {
                    mp = MapFixService.fix(rd.getLatitude(), rd.getLongitude(),
                            Constants.MAP_GOOGLE);
                }

                PointLatLng pl = new PointLatLng(longitude, latitude);
                Boolean inArea = MapFixService.IsInCircle(mp, pl, WaybillArearadius);//判断是否进入了点位的半径圆

                boolean isalarm = false;//是否触发报警

                boolean beforecrosstype = false;//当前点位是否在某个

                String crosskey = rd.getSimNo() + "-" + pointid;


                if (CrossMap.containsKey(crosskey)) {//存在之前的是否在围栏里，默认是不在
                    beforecrosstype = CrossMap.get(crosskey);
                }
                if (inArea != beforecrosstype) {//当前是否在围栏里和上一个点进行比较
                    isalarm = true;//触发了报警
                }
                CrossMap.put(crosskey, inArea);

                if (isalarm) {//如果触发了报警
                    String alarmsource = WaybillAreaEnum.进入运单围栏报警.getAlarmSource();
                    String alarmType = WaybillAreaEnum.进入运单围栏报警.getAlarmType();
                    if (!inArea) {
                        alarmsource = WaybillAreaEnum.离开运单围栏报警.getAlarmSource();
                        alarmType = WaybillAreaEnum.离开运单围栏报警.getAlarmType();
                    }
                    //用户id+订单id+点位id+点位类型
                    String descr = userid + ";" + orderid + ";" + pointid + ";" + pointtype + ";";
                    insertAlarm(alarmsource, alarmType, rd, name, descr);
                }

                log.debug("当前车辆的运单围栏处理情况为，simno=" + rd.getSimNo() + ",sendTime=" + rd.getSendTime() + "" +
                        "运单名称为=" + name + ",之前是否在围栏内=" + CrossMap.get(crosskey) + ",现在是否在围栏内=" + inArea);


            }
        }
    }


    /**
     * 发送运单围栏报警
     *
     * @param alarmSource
     * @param alarmType
     * @param rd
     * @param areaName
     */
    private void insertAlarm(String alarmSource, String alarmType,
                             GPSRealData rd, String areaName, String descr) {
        rd.setLocation("运单围栏:" + areaName);
        Alarm alarm = this.newAlarmService.insertAlarm(alarmSource, alarmType, rd, "AreaAlarmService");
        AreaAlarmEvent areaAlarmEvent = new AreaAlarmEvent();
        BeanUtils.copyProperties(alarm, areaAlarmEvent);
        areaAlarmEvent.setDescr(descr);
        EventMsg em = new EventMsg();
        em.setEventBody(areaAlarmEvent);
        em.loadDefaultDevMsgAttr();
        kafkaMessageSender.sendAreaAlarmEventMsg(em, rd.getSimNo());
    }

}
