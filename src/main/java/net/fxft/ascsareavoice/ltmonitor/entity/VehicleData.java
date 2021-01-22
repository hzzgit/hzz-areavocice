package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;
import net.fxft.gateway.util.SimNoUtil;

import java.util.Date;


/**
 * 车辆基本静态信息
 *
 * @author admin
 */
@DbTable(value = "vehicle", camelToUnderline = false)
public class VehicleData extends TenantEntity {


    @DbId
    @DbColumn(columnName = "vehicleId")
    private long entityId;

    private String plateNo;
    //GPS手机卡号
    private String simNo;

    /**
     * 视频通道数
     */
    private int videoChannelNum;

    private String videoChannelNames;

    public VehicleData() {
        setCreateDate(new Date());
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(long value) {
        entityId = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String value) {
        plateNo = value;
    }


    public int getVideoChannelNum() {
        return videoChannelNum;
    }

    public void setVideoChannelNum(int videoChannelNum) {
        this.videoChannelNum = videoChannelNum;
    }

    public String getVideoChannelNames() {
        return videoChannelNames;
    }

    public void setVideoChannelNames(String videoChannelNames) {
        this.videoChannelNames = videoChannelNames;
    }

    public String getSimNo() {
        simNo = SimNoUtil.toSimNo12(simNo);
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = SimNoUtil.toSimNo12(simNo);
    }

}