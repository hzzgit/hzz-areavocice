package net.fxft.ascsareavoice.po.autovoice;

import java.util.Date;
import java.util.List;

//自动播报的配置内容类
public class AutoVoicePO {

    private String plateNo;

    private String simNo;

    private Long vehicleId;

    private Long depId;

    private int isuse;

    private Date startTime;
    private Date endTime;

    private List<AutoVoiceConfigPO> autoVoiceConfigPOS;


    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
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

    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
    }

    public int getIsuse() {
        return isuse;
    }

    public void setIsuse(int isuse) {
        this.isuse = isuse;
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

    public List<AutoVoiceConfigPO> getAutoVoiceConfigPOS() {
        return autoVoiceConfigPOS;
    }

    public void setAutoVoiceConfigPOS(List<AutoVoiceConfigPO> autoVoiceConfigPOS) {
        this.autoVoiceConfigPOS = autoVoiceConfigPOS;
    }
}
