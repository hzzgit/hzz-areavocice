package net.fxft.gatewaybusi.dataprocess;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.entity.VehicleData;
import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.JT_0201;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.jt808.protocol.positionAdditional.PostitionAdditional_InOutAreaAlarmAdditional;
import com.ltmonitor.jt808.protocol.positionAdditional.PostitionAdditional_OverSpeedAlarmAdditional;
import com.ltmonitor.jt808.protocol.positionAdditional.PostitionAdditional_RouteDriveTimeAlarmAdditional;
import com.ltmonitor.jt808.protocol.positionAdditional.PostitionAdditional_UnusualDriveBehaviour;
import com.ltmonitor.util.DateUtil;
import net.fxft.gateway.util.SimNoUtil;
import net.fxft.gatewaybusi.service.AutoVoice.IAutoVoiceService;
import net.fxft.gatewaybusi.service.IRealDataService;
import net.fxft.gatewaybusi.service.MapArea.AreaAlarmService;
import net.fxft.gatewaybusi.service.impl.RealDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Process0200_RealData{

    private static final Logger log = LoggerFactory.getLogger(Process0200_RealData.class);

    private static long MaxAfterMillis = 20*3600*1000;

    @Autowired
    private RealDataService realDataService;

    //语音播报服务层
    @Autowired
    private IAutoVoiceService autoVoiceService;

    //注入围栏报警的类
    @Autowired
    private AreaAlarmService areaAlarmService;

    public void processData(String simNo, T808Message message) throws Exception {
        int headerType = message.getHeader().getMessageType();
        JT_0200 jvi = null;
        if (headerType == 0x0200) {
            jvi = (JT_0200) message.getMessageContents();
        } else if (headerType == 0x0201) {
            JT_0201 jt = (JT_0201) message.getMessageContents();
            jvi = jt.getPositionReport();
        }
        if (jvi == null) {
            return;
        }
        Date dt = DateUtil.stringToDatetime(jvi.getTime(),
                "yyyy-MM-dd HH:mm:ss");
        if (dt == null) {
            log.error(message.getSimNo() + "," + message.getPlateNo()
                    + "定位包无效的日期:" + jvi.getTime());
            return; // 对于无效日期的包，是否丢弃?
        }
        if (dt.getTime() - System.currentTimeMillis() > MaxAfterMillis) {
            log.error("定位数据日期无效，收到未来的时间！" + message.getSimNo() + ",定位包信息:" +
                    jvi.toString());
            return;
        }

        double latitude = 0.000001 * jvi.getLatitude();
        double longitude =  0.000001 * jvi.getLongitude();
        double speed = jvi.getSpeed() / 10.0;
        GPSRealData rd = new GPSRealData();
        rd.setResponseSn((int) (message.getHeader().getMessageSerialNo()));
        simNo = SimNoUtil.toSimNo12(simNo);
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
        rd.setStatus(jvi.getStrStatus());
        rd.setOnlineDate(dt);
        rd.setOnline(true);
        autoVoiceService.autoVoiceMain(rd);
        areaAlarmService.addAreaqueue(rd);

    }



}
