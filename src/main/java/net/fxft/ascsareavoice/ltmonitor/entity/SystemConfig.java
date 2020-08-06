package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;

/**
 * 系统配置参数，用户可以在界面上灵活定制地图、刷新间隔等各种参数
 *
 * @author DELL
 */
//@Entity
//@Table(name = "SystemConfig")
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
//@org.hibernate.annotations.Proxy(lazy = false)
@DbTable(value = "SystemConfig", camelToUnderline = false)
public class SystemConfig extends TenantEntity implements Serializable {
    private static final long serialVersionUID = -7442032908999437416L;

    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "id")
    private long entityId;
    /**
     * 是否检测验证码，yes检测，no不检测
     */
    private String checkValidateCode;
    //平台标题
    private String systemTitle;
    //四维地图的key
    private String smartKey;
    //百度地图的key
    private String baiduKey;
    //百度后台调用web服务的key，用于坐标和位置转换
    private String baiduWebServiceKey;
    /**
     * 高德地图的js key
     */
    private String amapKey;
    /**
     * 高德地图的web服务 API的key
     */
    private String amapWebServiceKey;
    //初始化地图坐标
    private double initLat;
    private double initLng;
    private Integer initZoomLevel;
    //系统默认地图类型, 目前支持baidu or smart两种类型
    private String mapType;
    /**
     * 前端实时数据刷新时间间隔
     */
    private int refreshInterval;
    /**
     * 报警刷新的时间间隔
     */
    private int alarmInterval;
    /**
     * 显示状态类型
     */
    private String displayStateType;
    /**
     * 当报警的时候，显示车辆在地图上 yes no
     */
    private String showVehicleOnMap;
    /**
     * 超时停车报警中，对最长停车时间的定义，单位秒，如果停车超过此时间，就报警；
     */
    private int maxParkingTime;
    /**
     * 车辆地图地标类型
     */
    private String vehicleIconType;
    /**
     * 系统设定的车辆终端最长离线天数，超过后将进行报警，提示给前台
     */
    private int maxOfflineDays;
    /**
     * 界面布局，是否显示视频
     */
    private String layoutOption;
    /**
     * 地图上的车辆文字标签中是否显示部门名称
     */
//	@Column(name = "showDepNameOnMap", columnDefinition = "bit DEFAULT 0 ")
    private boolean showDepNameOnMap;
    private String logo;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public double getInitLat() {
        return this.initLat;
    }

    public void setInitLat(double initLat) {
        this.initLat = initLat;
    }

    public double getInitLng() {
        return this.initLng;
    }

    public void setInitLng(double initLng) {
        this.initLng = initLng;
    }

    public Integer getInitZoomLevel() {
        return this.initZoomLevel;
    }

    public void setInitZoomLevel(Integer initZoomLevel) {
        this.initZoomLevel = initZoomLevel;
    }


    public String getSystemTitle() {
        return this.systemTitle;
    }

    public void setSystemTitle(String systemTitle) {
        this.systemTitle = systemTitle;
    }


    public String getMapType() {
        return this.mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }


    public String getSmartKey() {
        return this.smartKey;
    }

    public void setSmartKey(String smartKey) {
        this.smartKey = smartKey;
    }


    public int getMaxOfflineDays() {
        return maxOfflineDays;
    }

    public void setMaxOfflineDays(int maxOfflineDays) {
        this.maxOfflineDays = maxOfflineDays;
    }


    public String getDisplayStateType() {
        return displayStateType;
    }

    public void setDisplayStateType(String displayStateType) {
        this.displayStateType = displayStateType;
    }

    public String getCheckValidateCode() {
        return checkValidateCode;
    }

    public void setCheckValidateCode(String checkValidateCode) {
        this.checkValidateCode = checkValidateCode;
    }

    public String getBaiduKey() {
        return baiduKey;
    }

    public void setBaiduKey(String baiduKey) {
        this.baiduKey = baiduKey;
    }

    public int getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public int getAlarmInterval() {
        return alarmInterval;
    }

    public void setAlarmInterval(int alarmInterval) {
        this.alarmInterval = alarmInterval;
    }

    public String getShowVehicleOnMap() {
        return showVehicleOnMap;
    }

    public void setShowVehicleOnMap(String showVehicleOnMap) {
        this.showVehicleOnMap = showVehicleOnMap;
    }

    public int getMaxParkingTime() {
        return maxParkingTime;
    }

    public void setMaxParkingTime(int maxParkingTime) {
        this.maxParkingTime = maxParkingTime;
    }

    public String getAmapWebServiceKey() {
        return amapWebServiceKey;
    }

    public void setAmapWebServiceKey(String amapWebServiceKey) {
        this.amapWebServiceKey = amapWebServiceKey;
    }

    public String getAmapKey() {
        return amapKey;
    }

    public void setAmapKey(String amapKey) {
        this.amapKey = amapKey;
    }

    public boolean isShowDepNameOnMap() {
        return showDepNameOnMap;
    }

    public void setShowDepNameOnMap(boolean showDepNameOnMap) {
        this.showDepNameOnMap = showDepNameOnMap;
    }

    public String getVehicleIconType() {
        return vehicleIconType;
    }

    public void setVehicleIconType(String vehicleIconType) {
        this.vehicleIconType = vehicleIconType;
    }

    public String getBaiduWebServiceKey() {
        return baiduWebServiceKey;
    }

    public void setBaiduWebServiceKey(String baiduWebServiceKey) {
        this.baiduWebServiceKey = baiduWebServiceKey;
    }


    /**
     * 界面布局方案
     */
    public String getLayoutOption() {
        return layoutOption;
    }

    public void setLayoutOption(String layoutOption) {
        this.layoutOption = layoutOption;
    }

}
