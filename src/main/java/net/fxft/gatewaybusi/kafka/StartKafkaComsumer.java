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
import net.fxft.gateway.kafka.UnitConfigManager;
import net.fxft.gateway.kafka.devicemsg.IFromDeviceMsgProcessor;
import net.fxft.gateway.protocol.DeviceMsg;
import net.fxft.gateway.protocol.DeviceMsgBuilder;
import net.fxft.gateway.protocol.TransferMsg;
import net.fxft.gateway.protocol.TransferMsgBuilder;
import net.fxft.gateway.protocol.gps.LocationMsg;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.gateway.util.SimNoUtil;
import net.fxft.gatewaybusi.GatewayBusiApplicationStart;
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
public class StartKafkaComsumer implements IFromDeviceMsgProcessor {

    private static final Logger log = LoggerFactory.getLogger(StartKafkaComsumer.class);
    @Autowired
    private IMessageProcessService messageProcessService;

    public static  boolean ispausepool=false;

    @Override
    public boolean isPausePoll() {
        return ispausepool;
    }

    @Autowired
    private RealDataService realDataService;


    private static long MaxAfterMillis = 20 * 3600 * 1000;

    private GPSRealData getGPS(String simNo, JT_0200 jvi) {
        Date dt = DateUtil.stringToDatetime(jvi.getTime(),
                "yyyy-MM-dd HH:mm:ss");
        if (dt == null) {
            log.error(simNo + ","
                    + "定位包无效的日期:" + jvi.getTime());
            return null;
        }
        if (dt.getTime() - System.currentTimeMillis() > MaxAfterMillis) {
            log.error("定位数据日期无效，收到未来的时间！" + simNo + ",定位包信息:" +
                    jvi.toString());
            return null;
        }

        double latitude = 0.000001 * jvi.getLatitude();
        double longitude = 0.000001 * jvi.getLongitude();
        double speed = jvi.getSpeed() / 10.0;
        GPSRealData rd = new GPSRealData();
        rd.setSimNo(simNo);
        rd.setSendTime(dt);
        if (latitude > 0 && longitude > 0) {
            // 保证是有效坐标
            rd.setLatitude(latitude);
            rd.setLongitude(longitude);
        }
        VehicleData vehicleData = realDataService.getVehicleData(simNo);
        if (vehicleData != null) {
            rd.setVehicleId(vehicleData.getEntityId());
        }
        rd.setVelocity(speed);
        String staStatus = Integer.toBinaryString(jvi.getStatus());
        staStatus = StringUtil.leftPad(staStatus, 32, '0');
        rd.setStatus(staStatus);
        rd.setOnlineDate(dt);
        rd.setOnline(true);
        return rd;
    }


    private GPSRealData getGPS(String simNo, LocationMsg jvi) {
        Date dt = TimeUtils.timeStamptotime(jvi.getTime());
        if (dt == null) {
            log.error(simNo + ","
                    + "定位包无效的日期:" + jvi.getTime());
            return null;
        }
        if (dt.getTime() - System.currentTimeMillis() > MaxAfterMillis) {
            log.error("定位数据日期无效，收到未来的时间！" + simNo + ",定位包信息:" +
                    jvi.toString());
            return null;
        }

        double latitude = jvi.getLatitude();
        double longitude = jvi.getLongitude();
        double speed = jvi.getSpeed();
        GPSRealData rd = new GPSRealData();
        rd.setSimNo(simNo);
        rd.setSendTime(dt);
        if (latitude > 0 && longitude > 0) {
            // 保证是有效坐标
            rd.setLatitude(latitude);
            rd.setLongitude(longitude);
        }
        VehicleData vehicleData = realDataService.getVehicleData(simNo);
        if (vehicleData != null) {
            rd.setVehicleId(vehicleData.getEntityId());
        }
        rd.setVelocity(speed);
        String staStatus = Integer.toBinaryString(jvi.getStatus());
        staStatus = StringUtil.leftPad(staStatus, 32, '0');
        rd.setStatus(staStatus);
        rd.setOnlineDate(dt);
        rd.setOnline(true);
        return rd;
    }


    @Override
    public void pocessMsg(String s, DeviceMsg deviceMsg, ConsumerRecord<String, byte[]> consumerRecord) {

        try {
            DeviceMsg dm = deviceMsg;
            if (dm.getMsgType() == 0x0200) {
                if (dm.getMsgBody() instanceof JT_0200) {
                    JT_0200 jt_0200 = (JT_0200) dm.getMsgBody();
                    GPSRealData rd = getGPS(dm.getSimNo(), jt_0200);
                    messageProcessService.processMsg(rd);
//                        autoVoiceService.autoVoiceMain(rd);
//                        areaAlarmService.addAreaqueue(rd);
                } else if (dm.getMsgBody() instanceof LocationMsg) {
                    LocationMsg lm = (LocationMsg) dm.getMsgBody();
                    GPSRealData rd = getGPS(dm.getSimNo(), lm);
                    messageProcessService.processMsg(rd);
                }
            } else if (dm.getMsgType() == 0x0704) {
                if (dm.getMsgBody() instanceof JT_0704) {
                    JT_0704 jt0704 = (JT_0704) dm.getMsgBody();
                    List<JT_0200> positionReportList = jt0704.getPositionReportList();
                    if (ConverterUtils.isList(positionReportList)) {
                        for (JT_0200 jt_0200 : positionReportList) {
                            GPSRealData rd = getGPS(dm.getSimNo(), jt_0200);
                            messageProcessService.processMsg(rd);
                        }
                    }

                }
            }

        } catch (Exception e) {
            log.error("kafka接收fromDeviceMsg处理出错！" , e);
        }
    }

    @Override
    public String getConsumerGroupId(UnitConfigManager unitConfigManager) {
        return unitConfigManager.getUnitConfig(GatewayBusiApplicationStart.class).getConsumerGroupId();
    }

    @Override
    public int getThreadPoolSize() {
        return 10;
    }

    @Override
    public void beforeShutdown() {

    }
}