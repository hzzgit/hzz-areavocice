package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO;

import lombok.Data;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:01
 */
@Data
public class PointDO {
    private Double latitude;

    private Double longitude;

    private String maptype;

    private Long pointid;

    public PointDO(Double latitude, Double longitude, String maptype,Long pointid) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maptype = maptype;
        this.pointid=pointid;
    }

}
