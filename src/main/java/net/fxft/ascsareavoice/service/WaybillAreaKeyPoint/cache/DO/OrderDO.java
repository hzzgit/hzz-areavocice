package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:48
 */
@Data
public class OrderDO {

    private Long orderId;
    private Long userId;
    private List<OrderAreaDO> orderAreaDOS=new ArrayList<>();
}
