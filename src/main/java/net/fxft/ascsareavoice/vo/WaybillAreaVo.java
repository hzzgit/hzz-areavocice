package net.fxft.ascsareavoice.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author ：hzz
 * @description：车辆订单围栏类
 * @date ：2020/8/5 14:38
 */
@Data
public class WaybillAreaVo {
    private long id;
    private Date startTime,endTime;
    private String SimNo;
    private long userid;
    private int bytime;
    private String name;
}
