package net.fxft.ascsareavoice.vo;

import lombok.Data;

/**
 * @author ：hzz
 * @description：运单对应点位类
 * @date ：2020/8/5 14:43
 */
@Data
public class WaybillAreaPointVo {

    /** simNo **/
    private String simNo;
    /**  主键  */
    private String  id;
    /**  点位类型,1，开始点，2，途经点，3，结束点  */
    private Long  pointtype;
    /**  经度  */
    private String  longitude;
    /**  纬度  */
    private String  latitude;
    /**  地图类型 gps:天地图坐标，baidu:百度坐标，google:谷歌地图  */
    private String  maptype;
    /**  和orderareamanage表主键绑定  */
    private String  orderid;
    /**  设置点位的有效半径  */
    private Integer validradius;
}
