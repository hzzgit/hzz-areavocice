package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

//@Entity
//@Table(name="newAlarm",catalog="gps_hisdata")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "gps_hisData.alarm", camelToUnderline = false)
public class Alarm extends TenantEntity {
    /**
     * 自动处理
     */
    public static int PROCESSED_AUTO = 13;
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "id")
    private long entityId;
    /*
     * 车辆Id
     */
    private long vehicleId;
    /**
     * adas报警唯一编号，系统生成的32字节的UUID，确保可以跟踪到详细的adas报警信息
     */
    private String adasAlarmNo;
    private String plateNo;
    private String simNo;
    //报警类型
    private String alarmType;
    //报警描述
    private String descr;
    //报警来源
    private String alarmSource;
    //报警时间、改为插入服务器时间
    private Date alarmTime;
    //车速
    private double speed;
    //报警地点
    private String location;
    private double latitude;
    private double longitude;
    /**
     * 纳入评价 1纳入 0 不纳入考评
     */
    private int appraisal;
    //对应终端消息的流水号
    private int ackSn;
    //处理标志
    private int processed;
    //处理时间
    private Date processedTime;
    //处理用户Id
    private long processedUserId;
    //报警的库表名称，用于ibatis插入时设置的动态库表名称
//    @Transient
    @DbColumn(ignore = true)
    private String tableName;
//    @Transient
    @DbColumn(ignore = true)
    private long depId;
    private String processedUserName;
    public Alarm() {
        setCreateDate(new Date());
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    public Date getProcessedTime() {
        return processedTime;
    }

    public void setProcessedTime(Date processedTime) {
        this.processedTime = processedTime;
    }

    public long getProcessedUserId() {
        return processedUserId;
    }

    public void setProcessedUserId(long processedUserId) {
        this.processedUserId = processedUserId;
    }

    public String getProcessedUserName() {
        return processedUserName;
    }

    public void setProcessedUserName(String processedUserName) {
        this.processedUserName = processedUserName;
    }

    public int getAckSn() {
        return ackSn;
    }

    public void setAckSn(int ackSn) {
        this.ackSn = ackSn;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    public long getDepId() {
        return depId;
    }

    public void setDepId(long depId) {
        this.depId = depId;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getAdasAlarmNo() {
        return adasAlarmNo;
    }

    public void setAdasAlarmNo(String adasAlarmNo) {
        this.adasAlarmNo = adasAlarmNo;
    }

    public int getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(int appraisal) {
        this.appraisal = appraisal;
    }

}
