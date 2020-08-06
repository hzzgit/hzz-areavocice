package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;
import java.util.Date;

/**
 * 驾驶员插卡拔卡记录
 *
 * @author DELL
 */

//@Entity
//@Table(name = "driverCardRecord")
//@org.hibernate.annotations.Proxy(lazy = false)
@DbTable(value = "driverCardRecord", camelToUnderline = false)
public class DriverCardRecord implements Serializable {

    //	@javax.persistence.Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "Id", unique = true, nullable = false)
    @DbId
    private long Id;
    private long vehicleId;//所属车辆
    //1 插入 2 拔出
    private int cardState;
    //插卡或拔卡时间 在状态为1时有效 YYMMDDHHmmss
    private Date operTime;
    //读卡结果，0成功 其他失败
    private int readResult;
    /// 驾驶员姓名
    private String driverName;
    /// 从业资格证编码 长度20
    private String certificationCode;
    /// 发证机构名称
    private String agencyName;
    /// 证件有效期 YYYYMMDD
    private String validateDate;
    private Date createDate;

    public DriverCardRecord() {
        createDate = new Date();
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public int getCardState() {
        return cardState;
    }

    public void setCardState(int cardState) {
        this.cardState = cardState;
    }

    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }

    public int getReadResult() {
        return readResult;
    }

    public void setReadResult(int readResult) {
        this.readResult = readResult;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCertificationCode() {
        return certificationCode;
    }

    public void setCertificationCode(String certificationCode) {
        this.certificationCode = certificationCode;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getValidateDate() {
        return validateDate;
    }

    public void setValidateDate(String validateDate) {
        this.validateDate = validateDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }
}
