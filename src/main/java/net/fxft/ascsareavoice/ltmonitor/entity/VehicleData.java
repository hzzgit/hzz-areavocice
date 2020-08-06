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

    public VehicleData() {
        setCreateDate(new Date());
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

    public void setPlateNo(String value) {
        plateNo = value;
    }


    public String getSimNo() {
        simNo = SimNoUtil.toSimNo12(simNo);
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = SimNoUtil.toSimNo12(simNo);
    }

}