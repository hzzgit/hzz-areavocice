package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:01
 */
@Data
public class AreaDO {

    private String name;

    private List<PointDO> pointDOS=new ArrayList<>();

}
