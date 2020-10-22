package net.fxft.ascsareavoice.service.GpsFliter;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;
import net.fxft.common.jdbc.DbColumn;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class GpsInfo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

 
    /**  纬度  */
    private Double latitude;
    /**  经度  */
    private Double longitude;
    /**  发送时间  */
    private String sendTime;
    /**  SIM号  */
    private String simNo;
	/**  车辆ID */
    private Long vehicleId;
    /**  点位类型  (0.正常点,1.可疑点)*/
    private Integer pointType;
    /** 系统记录时间，用于比较过去时间和未来时间的标准，前后二十小时  */
    private String sysRecordTime;
    /**  有效性 */
    private boolean valid;
    /**  速度 */
    private double velocity;

    /*方向*/
    private int direction;

    /*海拔*/
    private double altitude;

    private String status;

    /** 累计未验证点个数 */
    @DbColumn(ignore = true)
    private AtomicInteger addUnvalidCount = new AtomicInteger(0);

    public AtomicInteger getAddUnvalidCount() {
        return addUnvalidCount;
    }

    public void setAddUnvalidCount(AtomicInteger addUnvalidCount) {
        this.addUnvalidCount = addUnvalidCount;
    }


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Integer getPointType() {
        return pointType;
    }

    public void setPointType(Integer pointType) {
        this.pointType = pointType;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getSysRecordTime() {
        return sysRecordTime;
    }

    public void setSysRecordTime(String sysRecordTime) {
        this.sysRecordTime = sysRecordTime;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public static  GpsInfo getInstance(GPSRealData rd){
        GpsInfo gpsInfo=new GpsInfo();
        BeanUtils.copyProperties(rd,gpsInfo);
        gpsInfo.setSysRecordTime(TimeUtils.dateTodetailStr(new Date()));
        gpsInfo.setSendTime(TimeUtils.dateTodetailStr(rd.getSendTime()));
       // gpsInfo.setSysRecordTime();
        return gpsInfo;
    }



    public  GPSRealData gpsinfotoGpsRealData(){
        GPSRealData rd=new GPSRealData();
        BeanUtils.copyProperties(this,rd);
        rd.setSendTime(TimeUtils.todatetime(sendTime));
        rd.setOnlineDate(TimeUtils.todatetime(sendTime));
        rd.setOnline(true);
        return rd;
    }


    @Override
    public String toString() {
        return "GpsInfo{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", sendTime='" + sendTime + '\'' +
                ", simNo='" + simNo + '\'' +
                ", vehicleId=" + vehicleId +
                ", pointType=" + pointType +
                ", sysRecordTime='" + sysRecordTime + '\'' +
                ", velocity=" + velocity +
                ", direction=" + direction +
                ", altitude=" + altitude +
                ", addUnvalidCount=" + addUnvalidCount +
                '}';
    }
}
