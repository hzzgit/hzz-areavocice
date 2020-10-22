package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO;

import lombok.Data;

import java.util.Date;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:16
 */
@Data
public class OrderAreaDO{

    private long areaid;

    /*解除停车的直线位移*/
    private int cfgparkdisplacedistance;

    /*超时停车的时长*/
    private int cfgparkdisplacetime;

    private int cfgparktime;

    public OrderAreaDO(long areaid, int cfgparkdisplacedistance, int cfgparkdisplacetime, int cfgparktime) {
        this.areaid = areaid;
        if(cfgparkdisplacedistance==0){
            cfgparkdisplacedistance=50;
        }
        this.cfgparkdisplacedistance = cfgparkdisplacedistance;
        this.cfgparkdisplacetime = cfgparkdisplacetime;
        this.cfgparktime = cfgparktime;
    }
}
