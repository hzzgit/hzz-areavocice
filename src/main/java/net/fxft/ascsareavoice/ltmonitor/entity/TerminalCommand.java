package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;


/**
 * 终端命令
 *
 * @author admin
 */
//@Entity
//@Table(name="TerminalCommand")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "TerminalCommand", camelToUnderline = false)
public class TerminalCommand extends TenantEntity {
    /**
     * 指令等待服务器发送
     */
    public static final String STATUS_NEW = "New";
    /**
     * 服务器已经发送，等待终端应答
     */
    public static final String STATUS_PROCESSING = "Processing";

    public static final String STATUS_WRITESUCCESS = "WriteSuccess";

    public static final String STATUS_WRITEFAILED = "WriteSuccess";

    /**
     * 指令格式不正确,无效格式，服务器无法下发
     */
    public static final String STATUS_INVALID = "Invalid";
    /**
     * 终端应答-执行成功
     */
    public static final String STATUS_SUCCESS = "Success";
    /**
     * 只对拍照指令有效，在拍照指令，终端应答成功后，开始上传照片，拍照命令的最终终端不是命令执行成功，而是照片上传成功
     */
    public static final String STATUS_UPLOADED = "Uploaded";
    /**
     * 终端应答：执行失败
     */
    public static final String STATUS_FAILED = "Failed";
    /**
     * 终端不在线
     */
    public static final String STATUS_OFFLINE = "Offline";
    /**
     * 终端应答：设备不支持该指令
     */
    public static final String STATUS_NOT_SUPPORT = "NotSupport";

    //owner属性值:政府平台，terminal,用户登录名 三种属性
    public static final String FROM_GOV = "政府平台";
    public static final String FROM_TERMINAL = "terminal";
    //本地平台
    public static final String FROM_LOCAL_PLATFORM = "local_platform";
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "cmdId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "cmdId")
    private long entityId;
    /**
     * 车辆Id
     */
    private long vehicleId;
    //	@Column(name = "simNo")
    private String simNo;
    // 车牌号
//	@Column(name = "plateNo")
    private String plateNo;
    // 类别 命令的大类别
//	@Column(name = "cmdType")
    private int cmdType;


    // 终端ID号
    /**
     * 子命令类型，如苏标中，大类型是透传0x8900,0x0900, 子命令类型是嵌套的苏标指令
     */
    private int subCmdType;
    // 命令数据中的命令字或标志位
//    @Column(name = "cmd")
    private String cmd;
    // 数据，多个参数的时候，使用;隔开
//	@Column(name = "cmdData")
    private String cmdData;
    // 命令状态
//	@Column(name = "status")
    private String status;
    // 命令下发的流水号
    private long SN;
    // 命令的发送者
//	@Column(name = "userId")
    private long userId;
    private Date updateDate;

    public TerminalCommand() {
        setCreateDate(new Date());
        setStatus(STATUS_NEW);
    }

    @Override
    public long getEntityId() {
        return entityId;
    }

    @Override
    public void setEntityId(long value) {
        entityId = value;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String value) {
        simNo = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String value) {
        plateNo = value;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int value) {
        cmdType = value;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String value) {
        cmd = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }

    // 区域设置时，关联的区域数据(Enclosure类)
    // public int EnclosureId { getGpsRealData; set; }

    //	@Column(name = "SN")
    public long getSN() {
        return SN;
    }

    public void setSN(long value) {
        SN = value;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long value) {
        userId = value;
    }

    //命令执行时间
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCmdData() {
        return cmdData;
    }

    public void setCmdData(String cmdData) {
        this.cmdData = cmdData;
    }

    public long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(long vehicleId) {
        this.vehicleId = vehicleId;
    }

    /**
     * 子命令类型，如苏标中，大类型是透传0x8900,0x0900, 子命令类型是嵌套的苏标指令
     */
    public int getSubCmdType() {
        return subCmdType;
    }

    public void setSubCmdType(int subCmdType) {
        this.subCmdType = subCmdType;
    }
}