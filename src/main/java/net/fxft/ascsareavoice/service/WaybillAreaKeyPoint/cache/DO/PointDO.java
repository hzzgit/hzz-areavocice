package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO;

import lombok.Data;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:01
 */
@Data
public class PointDO {
    private double latitude;

    private double longitude;

    private String maptype;

    private long pointid;

    private int cfgradius;


    public PointDO(double latitude, double longitude, String maptype, long pointid, int cfgradius) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.maptype = maptype;
        this.pointid = pointid;
        this.cfgradius = cfgradius;
    }
}
