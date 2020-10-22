package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO;

import lombok.Data;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 10:53
 */
@Data
public class AreaOrderDO {

    private long orderid;

    private long areaid;

    /*停车解除的位移大小，单位米,超过这个位移才能位移达到要求触发*/
    private int cfgparkdisplacedistance;

    /*停车解除的位移时长,单位秒，超过这个时长且位移达到要求才触发*/
    private int cfgparkdisplacetime;

    /*停车时长，单位秒,只有达到这个停车时长才开始触发进入关键点停车报警*/
    private int cfgparktime;

    private long userid;


}
