package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.service.DTO;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 15:16
 */
@Data
public class SimNoOrderKeyPointDTO implements Serializable {

    /*记录第一次进入停车状态的纬度及上次经纬度*/
    private Double latitude;
    /*记录第一次进入停车状态的经度及上次经纬度*/
    private Double longitude;
    /*停车开始时间*/
    private Date parkBeginTime;



    /*进入关键点停车触发乐报警*/
    private boolean InAlarm;



    /*用来记录是否当前在停车中*/
    private boolean isparkNow;

    /*用来记录是否触发了停车超时*/
    private boolean isparkTimeOut;

//    /*用来记录进入关键点停车报警的每个围栏也就是进入这个围栏只能报一次*/
//    private boolean InAlarmOnce;
//
//    /*用来记录进入关键点停车超时报警的每个围栏唯一性也就是进入这个围栏只能报一次*/
//    private boolean isparkTimeOutOnce;
    /**
     * 用来记录解除停车状态的次数
     */
    private int removeParkingCount;


    @Override
    public String toString() {
        return "SimNoOrderKeyPointDTO{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", parkBeginTime=" + parkBeginTime +
                ", InAlarm=" + InAlarm +
                ", isparkNow=" + isparkNow +
                ", isparkTimeOut=" + isparkTimeOut +
                ", removeParkingCount=" + removeParkingCount +
                '}';
    }
}
