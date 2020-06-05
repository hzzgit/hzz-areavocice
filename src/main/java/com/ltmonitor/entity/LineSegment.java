package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;
import java.util.Date;

//**********************************************************************
// 线路中的每个拐点                                                              
//**********************************************************************

//@Entity
//@Table(name="LineSegment")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "LineSegment")
public class LineSegment extends TenantEntity implements Serializable {
    public static String InDriver = "进区域报警给驾驶员";
    public static String InPlatform = "进区域报警给平台";
    public static String OutDriver = "出区域报警给驾驶员";
    public static String OutPlatform = "出区域报警给平台 ";
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "segId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "segId")
    private long entityId;
    private String name;
    /**
     * 线段所属的线路id
     */
    private long routeId;
    //拐点ID
    private int pointId;
    //经度
    private double latitude1;
    //纬度
    private double longitude1;
    private double latitude2;
    private double longitude2;
    //是否分段限速站点
    private boolean station;
    //路段宽度
    private int lineWidth;
    //路段属性,各个选项使用;分割
    private String alarmType;
    //路段行驶时间限制
    private int maxTimeLimit;
    //路段最低行驶时间限制
    private int minTimeLimit;
    //超速限制
    private double maxSpeed;
    //超速持续时间
    private int overSpeedTime;
    //是否根据道路行驶时间
    private int byTime;
    //是否根据限速值
    private int limitSpeed;

//    public LineSegment() {
//        setCreateDate(new Date());
////        this.lineWidth = 100;
////        this.limitSpeed = true;
////        this.byTime = true;
////        this.maxSpeed = 100;
////        this.overSpeedTime = 10;
////        this.maxTimeLimit = 600;
////        this.minTimeLimit = 10;
//    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public long getRouteId() {
        return routeId;
    }

    public void setRouteId(long value) {
        routeId = value;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int value) {
        pointId = value;
    }

    public double getLatitude1() {
        return latitude1;
    }

    public void setLatitude1(double value) {
        latitude1 = value;
    }

    public double getLongitude1() {
        return longitude1;
    }

    public void setLongitude1(double value) {
        longitude1 = value;
    }

    public boolean isStation() {
        return station;
    }

    public void setStation(boolean value) {
        station = value;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int value) {
        lineWidth = value;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String value) {
        alarmType = value;
    }

    public int getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public void setMaxTimeLimit(int value) {
        maxTimeLimit = value;
    }

    public int getMinTimeLimit() {
        return minTimeLimit;
    }

    public void setMinTimeLimit(int value) {
        minTimeLimit = value;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double value) {
        maxSpeed = value;
    }

    public int getOverSpeedTime() {
        return overSpeedTime;
    }

    public void setOverSpeedTime(int value) {
        overSpeedTime = value;
    }

    public boolean getByTime() {
        return byTime==1?true:false;
    }

    public void setByTime(int value) {
        byTime = value;
    }

    public boolean getLimitSpeed() {
        return limitSpeed==1?true:false;
    }

    public void setLimitSpeed(int value) {
        limitSpeed = value;
    }

    //创建区域属性
    public byte CreateAreaAttr() {
        String byteStr = "";
        /**
         byteStr += (byte)(getByTime() ? 1 : 0); //1：根据时间
         byteStr += (byte)(getLimitSpeed() ? 1 : 0); //限速
         byteStr += 0;
         byteStr += 0;

         while(byteStr.length() < 7)
         {
         byteStr += '0';
         }
         byteStr += (byte)(isStation() ? 1 : 0); //1：是否有报站点
         */

        byteStr += (isStation() ? 1 : 0); //1：是否有报站点
        byteStr += "00000";
        byteStr += (getLimitSpeed() ? 1 : 0); //限速
        byteStr += (getByTime() ? 1 : 0); //1：根据时间
        byte t = Byte.parseByte(byteStr, 2);
        return t;
    }

    public double getLatitude2() {
        return latitude2;
    }

    public void setLatitude2(double latitude2) {
        this.latitude2 = latitude2;
    }

    public double getLongitude2() {
        return longitude2;
    }

    public void setLongitude2(double longitude2) {
        this.longitude2 = longitude2;
    }


}