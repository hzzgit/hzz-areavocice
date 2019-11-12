package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;
import net.fxft.gateway.util.SimNoUtil;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * GPS实时数据
 * 每个车一条实时记录，用于保存当前最新的gps定位数据
 *
 * @author admin
 */

//@Entity
//@Table(name = "GPSRealData")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "GPSRealData", camelToUnderline = false)
public class GPSRealData implements Serializable {

    // 唯一库表ID，没有实际意义
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", unique = true, nullable = false)
    @DbId
    private int id;
    private long vehicleId;
    // 设备终端状态
    private String status;
    // 部门
    private long depId;
    //刹车转向等状态
    private int signalState;
    //在线时间
    private Date onlineDate;
    //终端发送的定位包的终端流水号
//    @Transient
    @DbColumn(ignore = true)
    private int responseSn;
    // 电路状态,也就是报警状态
    private String alarmState;
    //最近一次停车时间
    private Date parkingTime;
    //疲劳驾驶报警时间
    private Date tiredAlarmTime;
    // 车牌号
    private String plateNo;
    // 终端围栏报警的区域ID
    private long areaId;
    // 围栏报警类型 0：进； 1：出
    private int areaAlarm;
    //围栏报警的区域类型
    private int areaType;
    //超速时，所在的区域id
    private int overSpeedAreaId;
    private int overSpeedAreaType;
    //路段行驶时间过长报警时，所在的路段id
    private int routeSegmentId;
    //行驶时间
    private int runTimeOnRoute;
    //0：行驶时间不足，1：过长
    private int routeAlarmType;
    // 车终端卡号
    private String simNo;
    // 地理位置的文字描述,如省,市，县，路的详细描述
    private String location;
    // 发送时间
    private Date sendTime;
    // 表示这条数据的更新时间
    private Date updateDate;
    // 经度
    private double longitude;
    // 纬度
    private double latitude;
    // 速度
    private double velocity;
    // 方向
    private int direction;
    // 海拔
    private double altitude;
    // 行驶记录仪速度
    private double recordVelocity;
    // 当前油量
    private double gas;
    // 行驶里程总量
    private double mileage;
    /**
     * 昨天里程，当天行驶里程 = mileage - yesterdayMileage;
     */
    private double lastDayMileage;
    // GPS的定位状态，false代表没有定位,被屏蔽或找不到卫星
    private boolean valid;
    /**
     * GPS设备在线状态, false代表不在线
     */
    private boolean online;
    /**
     * 视频相关报警
     */
    private int videoAlarm;
    /**
     * 视频信号丢失报警状态
     */
    private int videoLossAlarmStatus;
    /**
     * 视频遮挡报警状态
     */
    private int videoCoverAlarmStatus;
    /**
     * 视频存储器故障状态
     */
    private short videoStorageFaultAlarmStatus;
    /**
     * 异常驾驶行为类型
     */
    private short unusualDriveBeaviour;
    /**
     * 疲劳驾驶程度
     */
    private int tiredLevel;
    /**
     * 平台路线偏离报警
     */
    private String offsetRouteAlarm;
    /**
     * 平台分段限速报警
     */
    private String overSpeedAlarm;
    /**
     * 平台地图区域报警
     */
    private String mapAreaAlarm;
    /**
     * 平台未按规定时间到达关键点报警
     */
    private String arriveKeyPlaceAlarm;
    /**
     * 平台未按规定时间离开关键点报警
     */
    private String leaveKeyPlaceAlarm;
    public GPSRealData() {
        alarmState = "00000000000000000000000000000000";
        status = "00000000000000000000000000000000";

        //setSendTime(new java.util.Date());
        this.updateDate = new Date();


    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public double getRecordVelocity() {
        return recordVelocity;
    }

    public void setRecordVelocity(double value) {
        recordVelocity = value;
    }

    public double getMileage() {
        return mileage;
    }

    public void setMileage(double value) {
        mileage = value;
    }

    public double getGas() {
        return gas;
    }

    public void setGas(double gas) {
        this.gas = gas;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }


    public long getDepId() {
        return depId;
    }

    public void setDepId(long depId) {
        this.depId = depId;
    }

    public long getAreaId() {
        return areaId;
    }

    public void setAreaId(long areaId) {
        this.areaId = areaId;
    }

    public int getAreaAlarm() {
        return areaAlarm;
    }

    public void setAreaAlarm(int areaAlarm) {
        this.areaAlarm = areaAlarm;
    }

    public int getResponseSn() {
        return responseSn;
    }

    public void setResponseSn(int responseSn) {
        this.responseSn = responseSn;
    }

    public int getAreaType() {
        return areaType;
    }

    public void setAreaType(int areaType) {
        this.areaType = areaType;
    }

    public int getSignalState() {
        return signalState;
    }

    public void setSignalState(int signal) {
        this.signalState = signal;
    }

    public Date getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(Date onlineDate) {
        this.onlineDate = onlineDate;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }


    public String getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(String value) {
        alarmState = value;
    }

    public int getOverSpeedAreaId() {
        return overSpeedAreaId;
    }

    public void setOverSpeedAreaId(int overSpeedAreaId) {
        this.overSpeedAreaId = overSpeedAreaId;
    }

    public int getOverSpeedAreaType() {
        return overSpeedAreaType;
    }

    public void setOverSpeedAreaType(int overSpeedAreaType) {
        this.overSpeedAreaType = overSpeedAreaType;
    }

    public int getRouteSegmentId() {
        return routeSegmentId;
    }

    public void setRouteSegmentId(int routeSegmentId) {
        this.routeSegmentId = routeSegmentId;
    }

    public int getRunTimeOnRoute() {
        return runTimeOnRoute;
    }

    public void setRunTimeOnRoute(int runTimeOnRoute) {
        this.runTimeOnRoute = runTimeOnRoute;
    }

    public int getRouteAlarmType() {
        return routeAlarmType;
    }

    public void setRouteAlarmType(int routeAlarmType) {
        this.routeAlarmType = routeAlarmType;
    }

    public Date getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(Date parkingTime) {
        this.parkingTime = parkingTime;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date value) {
        updateDate = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String value) {
        plateNo = value;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String value) {
        location = value;
    }


    public String getSimNo() {
        simNo = SimNoUtil.toSimNo12(simNo);
        return simNo;
    }

    public void setSimNo(String value) {
        simNo = SimNoUtil.toSimNo12(value);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double value) {
        latitude = value;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int value) {
        direction = value;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double value) {
        velocity = value;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double value) {
        longitude = value;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date value) {
        sendTime = value;
    }

    public Date getTiredAlarmTime() {
        return tiredAlarmTime;
    }

    public void setTiredAlarmTime(Date tiredAlarmTime) {
        this.tiredAlarmTime = tiredAlarmTime;
    }

    public double getLastDayMileage() {
        return lastDayMileage;
    }

    public void setLastDayMileage(double lastDayMileage) {
        this.lastDayMileage = lastDayMileage;
    }

    /**
     * 路线偏离报警
     */
    public String getOffsetRouteAlarm() {
        return offsetRouteAlarm;
    }

    public void setOffsetRouteAlarm(String offsetRouteAlarm) {
        this.offsetRouteAlarm = offsetRouteAlarm;
    }

    /**
     * 分段限速报警
     */
    public String getOverSpeedAlarm() {
        return overSpeedAlarm;
    }

    public void setOverSpeedAlarm(String overSpeedAlarm) {
        this.overSpeedAlarm = overSpeedAlarm;
    }

    /**
     * 平台的地图区域报警
     */
    public String getMapAreaAlarm() {
        return mapAreaAlarm;
    }

    public void setMapAreaAlarm(String mapAreaAlarm) {
        this.mapAreaAlarm = mapAreaAlarm;
    }


    /**
     * 平台未按规定时间到达关键点报警
     */
    public String getArriveKeyPlaceAlarm() {
        return arriveKeyPlaceAlarm;
    }

    public void setArriveKeyPlaceAlarm(String arriveKeyPlaceAlarm) {
        this.arriveKeyPlaceAlarm = arriveKeyPlaceAlarm;
    }

    /**
     * 平台未按规定时间离开关键点报警
     */
    public String getLeaveKeyPlaceAlarm() {
        return leaveKeyPlaceAlarm;
    }

    public void setLeaveKeyPlaceAlarm(String leaveKeyPlaceAlarm) {
        this.leaveKeyPlaceAlarm = leaveKeyPlaceAlarm;
    }

    public int getVideoAlarm() {
        return videoAlarm;
    }

    public void setVideoAlarm(int videoAlarm) {
        this.videoAlarm = videoAlarm;
    }

    /**
     * 视频信号丢失报警状态
     */
    public int getVideoLossAlarmStatus() {
        return videoLossAlarmStatus;
    }

    public void setVideoLossAlarmStatus(int videoLossAlarmStatus) {
        this.videoLossAlarmStatus = videoLossAlarmStatus;
    }

    /**
     * 视频遮挡报警状态
     */
    public int getVideoCoverAlarmStatus() {
        return videoCoverAlarmStatus;
    }

    public void setVideoCoverAlarmStatus(int videoCoverAlarmStatus) {
        this.videoCoverAlarmStatus = videoCoverAlarmStatus;
    }

    /**
     * 视频存储器故障报警状态
     */
    public short getVideoStorageFaultAlarmStatus() {
        return videoStorageFaultAlarmStatus;
    }

    public void setVideoStorageFaultAlarmStatus(short videoStorageFaultAlarmStatus) {
        this.videoStorageFaultAlarmStatus = videoStorageFaultAlarmStatus;
    }

    /**
     * 异常驾驶行为
     */
    public short getUnusualDriveBeaviour() {
        return unusualDriveBeaviour;
    }

    public void setUnusualDriveBeaviour(short unusualDriveBeaviour) {
        this.unusualDriveBeaviour = unusualDriveBeaviour;
    }

    /**
     * 疲劳驾驶程度
     */
    public int getTiredLevel() {
        return tiredLevel;
    }

    public void setTiredLevel(int tiredLevel) {
        this.tiredLevel = tiredLevel;
    }
}