package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO;

import lombok.Data;

/**
 * @author ：hzz
 * @description：区域点位缓存
 * @date ：2020/10/21 10:48
 */
@Data
public class AreaPointDO {

    private Long areaId;
    private String name;

    private Integer cfgradius;

    private Double latitude;

    private Double longitude;

    private String maptype;

    private Long pointid;
}
