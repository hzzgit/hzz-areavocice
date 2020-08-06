package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.Alarm;
import net.fxft.ascsareavoice.ltmonitor.entity.AlarmRecord;
import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.service.INewAlarmService;
import com.ltmonitor.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * 报警入库服务
 *
 * @author v5-552
 */

@Service("newAlarmService")
public class NewAlarmService implements INewAlarmService {

    private static Logger log = LoggerFactory.getLogger(NewAlarmService.class);


    public NewAlarmService() {
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
            //this.enQueue(ar, remark);//2019版就不插入到alarm表
            return ar;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


}
