package net.fxft.gatewaybusi.kafka;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.entity.VehicleData;
import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.JT_0704;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.util.ConverterUtils;
import com.ltmonitor.util.DateUtil;
import com.ltmonitor.util.StringUtil;
import com.ltmonitor.util.TimeUtils;
import net.fxft.cloud.metrics.Tps;
import net.fxft.common.tpool.BlockedThreadPoolExecutor;
import net.fxft.common.util.ByteUtil;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.protocol.DeviceMsg;
import net.fxft.gateway.protocol.DeviceMsgBuilder;
import net.fxft.gateway.protocol.TransferMsg;
import net.fxft.gateway.protocol.TransferMsgBuilder;
import net.fxft.gateway.protocol.gps.LocationMsg;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.gateway.util.SimNoUtil;
import net.fxft.gatewaybusi.IShutdownHook;
import net.fxft.gatewaybusi.service.AutoVoice.IAutoVoiceService;
import net.fxft.gatewaybusi.service.IMessageProcessService;
import net.fxft.gatewaybusi.service.MapArea.AreaAlarmService;
import net.fxft.gatewaybusi.service.impl.RealDataService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Component
public class StartKafkaComsumer implements IShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(StartKafkaComsumer.class);
    @Autowired
    private IMessageProcessService messageProcessService;

    @Autowired
    private ConsumerFactory kafkaConsumerFactory;

    private String fromDeviceMsgTopic;

    private BlockedThreadPoolExecutor execPool = null;

    @Value("${kafkaComsumer.threadPool:10}")
    private int threadCount;

    private Tps processTps = new Tps();

    private LongAdder totalReceiveCount = new LongAdder();

    private volatile boolean stopped = false;
    private Thread kafkaThread;

    //语音播报服务层
    @Autowired
    private IAutoVoiceService autoVoiceService;

    //注入围栏报警的类
    @Autowired
    private AreaAlarmService areaAlarmService;

    @Autowired
    private RealDataService realDataService;


    public void startListener() throws Exception {
        fromDeviceMsgTopic = UnitConfig.instance.getFromDeviceMsgTopic();
        String consumerGroupId = UnitConfig.instance.getConsumerGroupId();
        log.info("初始化KafkaComsumer！fromDeviceMsgTopic=" + fromDeviceMsgTopic +
                "; threadPoolCount=" + threadCount + "; consumerGroupId" + consumerGroupId);
        execPool = new BlockedThreadPoolExecutor(threadCount);
        startKafkaListener();
    }

    @PreDestroy
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void shutdownHook() {
        log.info("---begin stop StartKafkaComsumer---");
        stopped = true;
        try {
            kafkaThread.join(50_000);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        log.info("---end stop StartKafkaComsumer---");
    }


    private void startKafkaListener() {
        kafkaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String consumerGroupId = UnitConfig.instance.getConsumerGroupId();
                Consumer<String, byte[]> cos = kafkaConsumerFactory.createConsumer(consumerGroupId, "busi");
                cos.subscribe(Arrays.asList(UnitConfig.instance.getHighFromDeviceMsgTopic(), fromDeviceMsgTopic));
                while (true) {
                    try {
                        ConsumerRecords<String, byte[]> records = cos.poll(1000);
                        for (ConsumerRecord<String, byte[]> record : records) {
                            receive(record.value());
                        }
                    } catch (Exception e) {
                        log.error("kafka poll出错！", e);
                    }
                    if (stopped) {
                        try {
                            log.info("---开始停止KafkaListener！");
                            long l1 = System.currentTimeMillis();
                            execPool.shutdown();
                            try {
                                execPool.awaitTermination(50, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                log.error("", e);
                            }
                            long l2 = System.currentTimeMillis();
                            log.info("---KafkaListener已停止！耗时=" + (l2 - l1));
                        } finally {
                            break;
                        }
                    }
                }
            }
        }, "kafka_poll");
        kafkaThread.start();
    }


    private void receive(byte[] message) {
        execPool.submit(() -> {
            totalReceiveCount.increment();
            pocess(message);
            processTps.inc();
        });
    }

    private void pocess(byte[] message) {

        try {
            TransferMsg transferMsg = TransferMsgBuilder.decodeFromBytes(message);
            if (transferMsg.isDecodable()) {
                DeviceMsg dm = (DeviceMsg) transferMsg.getMsgObject();
                if(dm.getMsgType() == 0x0200 ){
                    if(dm.getMsgBody() instanceof JT_0200){
                        JT_0200 jt_0200 = (JT_0200) dm.getMsgBody();
                        GPSRealData rd = getGPS(dm.getSimNo(),jt_0200);
                        messageProcessService.processMsg(rd);
//                        autoVoiceService.autoVoiceMain(rd);
//                        areaAlarmService.addAreaqueue(rd);
                    }else if(dm.getMsgBody() instanceof LocationMsg){
                        LocationMsg lm = (LocationMsg) dm.getMsgBody();
                        GPSRealData rd = getGPS(dm.getSimNo(),lm);
                        messageProcessService.processMsg(rd);
                    }
                }else if(dm.getMsgType() == 0x0704){
                    if(dm.getMsgBody() instanceof JT_0704){
                        JT_0704 jt0704 = (JT_0704) dm.getMsgBody();
                        List<JT_0200> positionReportList = jt0704.getPositionReportList();
                        if(ConverterUtils.isList(positionReportList)){
                            for (JT_0200 jt_0200 : positionReportList) {
                                GPSRealData rd = getGPS(dm.getSimNo(),jt_0200);
                                messageProcessService.processMsg(rd);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            log.error("kafka接收fromDeviceMsg处理出错！bytes=" + ByteUtil.byteToHexStr(message), e);
        }

    }

    private static long MaxAfterMillis = 20*3600*1000;
    private GPSRealData getGPS(String simNo,JT_0200 jvi){
        Date dt = DateUtil.stringToDatetime(jvi.getTime(),
                "yyyy-MM-dd HH:mm:ss");
        if (dt == null) {
            log.error(simNo+ ","
                    + "定位包无效的日期:" + jvi.getTime());
          return  null;
        }
        if (dt.getTime() - System.currentTimeMillis() > MaxAfterMillis) {
            log.error("定位数据日期无效，收到未来的时间！" +simNo    + ",定位包信息:" +
                    jvi.toString());
            return  null;
        }

        double latitude = 0.000001 * jvi.getLatitude();
        double longitude =  0.000001 * jvi.getLongitude();
        double speed = jvi.getSpeed() / 10.0;
        GPSRealData rd = new GPSRealData();
        rd.setSimNo(simNo);
        rd.setSendTime(dt);
        if (latitude > 0 && longitude > 0) {
            // 保证是有效坐标
            rd.setLatitude(latitude);
            rd.setLongitude(longitude);
        }
        VehicleData vehicleData= realDataService.getVehicleData(simNo);
        if(vehicleData!=null){
            rd.setVehicleId(vehicleData.getEntityId());
        }
        rd.setVelocity(speed);
        String staStatus = Integer.toBinaryString(jvi.getStatus());
        staStatus = StringUtil.leftPad(staStatus, 32, '0');
        rd.setStatus(staStatus);
        rd.setOnlineDate(dt);
        rd.setOnline(true);
        return  rd;
    }


    private GPSRealData getGPS(String simNo,LocationMsg jvi){
        Date dt= TimeUtils.timeStamptotime(jvi.getTime());
        if (dt == null) {
            log.error(simNo+ ","
                    + "定位包无效的日期:" + jvi.getTime());
            return  null;
        }
        if (dt.getTime() - System.currentTimeMillis() > MaxAfterMillis) {
            log.error("定位数据日期无效，收到未来的时间！" +simNo    + ",定位包信息:" +
                    jvi.toString());
            return  null;
        }

        double latitude = jvi.getLatitude();
        double longitude =   jvi.getLongitude();
        double speed = jvi.getSpeed();
        GPSRealData rd = new GPSRealData();
        rd.setSimNo(simNo);
        rd.setSendTime(dt);
        if (latitude > 0 && longitude > 0) {
            // 保证是有效坐标
            rd.setLatitude(latitude);
            rd.setLongitude(longitude);
        }
        VehicleData vehicleData= realDataService.getVehicleData(simNo);
        if(vehicleData!=null){
            rd.setVehicleId(vehicleData.getEntityId());
        }
        rd.setVelocity(speed);
        String staStatus = Integer.toBinaryString(jvi.getStatus());
        staStatus = StringUtil.leftPad(staStatus, 32, '0');
        rd.setStatus(staStatus);
        rd.setOnlineDate(dt);
        rd.setOnline(true);
        return  rd;
    }


}