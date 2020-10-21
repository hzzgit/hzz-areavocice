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

    private Long areaid;

    private Integer cfgparkdisplacedistance;

    private Integer cfgparkdisplacetime;

    private Integer cfgparktime;

    public OrderAreaDO(Long areaid, Integer cfgparkdisplacedistance, Integer cfgparkdisplacetime, Integer cfgparktime) {
        this.areaid = areaid;
        this.cfgparkdisplacedistance = cfgparkdisplacedistance;
        this.cfgparkdisplacetime = cfgparkdisplacetime;
        this.cfgparktime = cfgparktime;
    }
}
