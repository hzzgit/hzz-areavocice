package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

/**
 * 终端信息
 *
 * @author admin
 */
//@Entity
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Table(name = "Terminal")
@DbTable(value = "Terminal", camelToUnderline = false)
public class Terminal extends TenantEntity {
    private static final long serialVersionUID = -480253912021549418L;
    public static String STATE_CREATE = "0";//开户，正常
    public static String STATE_SUSPEND = "1";//停用
    public static String STATE_UNINSTALL = "2";//已拆卸
    public static String STATE_OFFLINE = "3";//脱网

//	public Boolean getBind() {
//		return bind;
//	}

    @DbId
    @DbColumn(columnName = "termId")
    private long entityId;
    //出厂号
    private String devNo = "";
    //设备编号
    private String termNo = "";
    private String verSoftware;
    private String verHardware;
    private String verProtocol;
    //设备厂商
    private String producer = "";
    private Date makeTime = new Date();
    //生产批次
    private String makeNo = "";
    //状态
    private String state;
    private Date installTime = new Date();
    //联系人
    private String contacts = "";
    /**
     * 联系电话
     */
    private String contactTelephone;
    /**
     * 安装单位
     */
    private String installCompany = "";
    //终端型号
    private String termType;
    //是否已经绑定
    private Boolean bind = false;

    private Date updateTime = new Date();
    //流水号
    private String seqNo;

    private int depId;

    private String imei;
    private String simNo;

    public Terminal() {
        state = STATE_CREATE;

    }

    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "termId", unique = true, nullable = false)
    public long getEntityId() {
        return this.entityId;
    }

    public void setEntityId(long id) {
        this.entityId = id;
    }

    //	@Column(name = "devNo",  length = 20)
    public String getDevNo() {
        return this.devNo;
    }

    public void setDevNo(String devNo) {
        this.devNo = devNo;
    }

    //	@Column(name = "termNo", nullable = false, length = 20)
    public String getTermNo() {
        return this.termNo;
    }

    public void setTermNo(String termNo) {
        this.termNo = termNo;
    }

    public String getVerSoftware() {
        return this.verSoftware;
    }

    public void setVerSoftware(String verSoftware) {
        this.verSoftware = verSoftware;
    }

    public String getVerHardware() {
        return this.verHardware;
    }

    public void setVerHardware(String verHardware) {
        this.verHardware = verHardware;
    }

    public String getVerProtocol() {
        return this.verProtocol;
    }

    public void setVerProtocol(String verProtocol) {
        this.verProtocol = verProtocol;
    }

    public String getProducer() {
        return this.producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Date getMakeTime() {
        return this.makeTime;
    }

    public void setMakeTime(Date makeTime) {
        this.makeTime = makeTime;
    }

    public String getMakeNo() {
        return this.makeNo;
    }

    public void setMakeNo(String makeNo) {
        this.makeNo = makeNo;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }


    public Date getInstallTime() {
        return this.installTime;
    }

    public void setInstallTime(Date installTime) {
        this.installTime = installTime;
    }

    public String getTermType() {
        return this.termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public Boolean isBind() {
        return bind;
    }

    public void setBind(Boolean bind) {
        this.bind = bind;
    }

    public int getDepId() {
        return depId;
    }

    public void setDepId(int depId) {
        this.depId = depId;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getInstallCompany() {
        return installCompany;
    }

    public void setInstallCompany(String installCompany) {
        this.installCompany = installCompany;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
}