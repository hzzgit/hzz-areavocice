package net.fxft.ascsareavoice.ltmonitor.entity;


import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;
import java.util.Date;

/**
 * 行车记录仪返回的数据结果
 *
 * @author Administrator
 */
//@Entity
//@Table(name = "VehicleRecorder")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "VehicleRecorder", camelToUnderline = false)
public class VehicleRecorder implements Serializable {

    private Date createDate;
    private long vehicleId;
    private int cmd;
    private String driverLicense;
    private String cmdData;
    /**
     * 数据开始时间
     */
    private Date startTime;
    private Date endTime;
    private double speed;
    private byte signalState; // 信号开关 8位， 代表 8个信号量
    // 下发的命令Id,可以根据此Id查询结果
    private long commandId;
    // 数据块 中，每个数据的计时 序号
    private int sortId;
    private double latitude;
    private double longitude;
    private double altitude;
    //    @OneToMany(cascade = CascadeType.ALL, mappedBy = "recorder")
//    @OrderBy("Id")
//    private List<SpeedRecorder> speedList = new ArrayList<SpeedRecorder>();
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "RecorderId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "RecorderId")
    private int entityId;

    public VehicleRecorder() {
        setCreateDate(new Date());
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int value) {
        entityId = value;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getCmdData() {
        return cmdData;
    }

    public void setCmdData(String cmdData) {
        this.cmdData = cmdData;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getCommandId() {
        return commandId;
    }

    public void setCommandId(long commandId) {
        this.commandId = commandId;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public byte getSignalState() {
        return signalState;
    }

    public void setSignal(byte signal) {
        this.signalState = signal;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

//    public List<SpeedRecorder> getSpeedList() {
//        return speedList;
//    }
//
//    public void setSpeedList(List<SpeedRecorder> speedList) {
//        this.speedList = speedList;
//    }

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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

}
