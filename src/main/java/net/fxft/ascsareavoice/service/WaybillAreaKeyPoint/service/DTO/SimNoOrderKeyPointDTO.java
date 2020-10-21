package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.service.DTO;

import lombok.Data;

import java.util.Date;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 15:16
 */
@Data
public class SimNoOrderKeyPointDTO {

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
    /**
     * 用来记录解除停车状态的次数
     */
    private int removeParkingCount;



}
