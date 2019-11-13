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

    @DbId
    private int id;
    private long vehicleId;
    // 设备终端状态
    private String status;
    //在线时间
    private Date onlineDate;
    // 车牌号
    private String plateNo;
    // 车终端卡号
    private String simNo;
    // 发送时间
    private Date sendTime;
    // 经度
    private double longitude;
    // 纬度
    private double latitude;
    // 速度
    private double velocity;
    /**
     * GPS设备在线状态, false代表不在线
     */
    private boolean online;
    // 地理位置的文字描述,如省,市，县，路的详细描述
    private String location;
    //终端发送的定位包的终端流水号
    private int responseSn;

    public GPSRealData() {
        status = "00000000000000000000000000000000";

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getResponseSn() {
        return responseSn;
    }

    public void setResponseSn(int responseSn) {
        this.responseSn = responseSn;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }


    public Date getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(Date onlineDate) {
        this.onlineDate = onlineDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String value) {
        location = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String value) {
        plateNo = value;
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


}