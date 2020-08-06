package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

//@Entity
//@Table(name="driverInfo")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "driverInfo", camelToUnderline = false)
public class DriverInfo extends TenantEntity {
    private static final long serialVersionUID = -4955293596092558178L;
    //公司编号
    private String companyNo;
    /**
     * 驾驶车辆
     */
    private Long vehicleId;
    //驾驶员编号
    private String driverCode = "";
    //司机姓名
    private String driverName;
    //押运员
    private String monitor;
    //性别
    private String sex;
    //是否主驾驶
    private boolean mainDriver = true;
    /**
     * 从业资格证，驾驶证
     */
    private String driverLicence = "";
    /**
     * 身份证
     */
    private String identityCard = "";
    //地址
    private String address = "";
    /**
     * 联系电话
     */
    private String telephone = "";
    //手机
    private String mobilePhone = "";

    //出生日期
    private Date birthday = new Date();
    //驾驶车辆类型
    private String drivingType = "";
    //年审
    private Date examineYear = new Date();
    //驾龄
    private Short harnessesAge = 0;
    /**
     * 发证机构
     */
    private String licenseAgency;
    //发证时间
    private Date certificationDate = new Date();
    //过期时间
    private Date invalidDate = new Date();

    /**
     * 监管机构
     */
    private String monitorOrg = "";
    //监督电话
    private String monitorPhone = "";


    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "driverId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "driverId")
    private long entityId;

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    //	@Column(name = "driverCode", unique = true, nullable = false, length = 8)
    public String getDriverCode() {
        return this.driverCode;
    }

    public void setDriverCode(String driverCode) {
        this.driverCode = driverCode;
    }

    //	@Column(name = "driverName", nullable = false, length = 32)
    public String getDriverName() {
        return this.driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    //	@Column(name = "Sex", nullable = false, length = 8)
    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    //	@Column(name = "driverLicence", nullable = false, length = 20)
    public String getDriverLicence() {
        return this.driverLicence;
    }

    public void setDriverLicence(String driverLicence) {
        this.driverLicence = driverLicence;
    }

    //	@Column(name = "IdentityCard", nullable = false, length = 20)
    public String getIdentityCard() {
        return this.identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
    }

    //	@Column(name = "Address", nullable = false, length = 64)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //	@Column(name = "Telephone", nullable = false, length = 32)
    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    //	@Column(name = "mobilePhone", nullable = false, length = 32)
    public String getMobilePhone() {
        return this.mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    //	@Column(name = "Birthday", length = 23)
    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    //	@Column(name = "DrivingType", nullable = false, length = 8)
    public String getDrivingType() {
        return this.drivingType;
    }

    public void setDrivingType(String drivingType) {
        this.drivingType = drivingType;
    }

    //	@Column(name = "ExamineYear", length = 23)
    public Date getExamineYear() {
        return this.examineYear;
    }

    public void setExamineYear(Date examineYear) {
        this.examineYear = examineYear;
    }

    /**
     * 驾龄
     *
     * @return
     */
//	@Column(name = "HarnessesAge", nullable = false)
    public Short getHarnessesAge() {
        return this.harnessesAge;
    }

    public void setHarnessesAge(Short harnessesAge) {
        this.harnessesAge = harnessesAge;
    }


    /**
     * 驾照发证机构
     *
     * @return
     */
    public String getLicenseAgency() {
        return this.licenseAgency;
    }

    public void setLicenseAgency(String licenseAgency) {
        this.licenseAgency = licenseAgency;
    }

    public String getMonitorOrg() {
        return this.monitorOrg;
    }

    public void setMonitorOrg(String monitorOrg) {
        this.monitorOrg = monitorOrg;
    }

    public String getMonitorPhone() {
        return this.monitorPhone;
    }

    public void setMonitorPhone(String monitorPhone) {
        this.monitorPhone = monitorPhone;
    }


    public Date getCertificationDate() {
        return this.certificationDate;
    }

    public void setCertificationDate(Date certificationDate) {
        this.certificationDate = certificationDate;
    }

    public Date getInvalidDate() {
        return this.invalidDate;
    }

    public void setInvalidDate(Date invalidDate) {
        this.invalidDate = invalidDate;
    }


    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCompanyNo() {
        return companyNo;
    }

    public void setCompanyNo(String companyNo) {
        this.companyNo = companyNo;
    }

    public boolean isMainDriver() {
        return mainDriver;
    }

    public void setMainDriver(boolean mainDriver) {
        this.mainDriver = mainDriver;
    }

    public String getMonitor() {
        return monitor;
    }

    public void setMonitor(String monitor) {
        this.monitor = monitor;
    }
}
