package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

/**
 * 电子运单
 *
 * @author DELL
 */

@DbTable(value = "EWayBill", camelToUnderline = false)
public class EWayBill extends TenantEntity {

    @DbId
    @DbColumn(columnName = "billId")
    private long entityId;

    private String plateNo;

    private long vehicleId;
    //运单内容
    private String eContent;

    private int plateColor;

    public EWayBill() {
        createDate = new Date();
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String geteContent() {
        return eContent;
    }

    public void seteContent(String eContent) {
        this.eContent = eContent;
    }

    public int getPlateColor() {
        return plateColor;
    }

    public void setPlateColor(int plateColor) {
        this.plateColor = plateColor;
    }

}
