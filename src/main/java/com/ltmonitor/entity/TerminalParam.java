package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

/**
 * 终端配置参数 -记录终端的参数配置
 * 参考808协议文档中的终端参数
 *
 * @author admin
 */
//@Entity
//@Table(name="TerminalParam")
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "TerminalParam", camelToUnderline = false)
public class TerminalParam extends TenantEntity {
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "paramId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "paramId")
    private long entityId;
    //终端Sim ID号
    private String simNo;
    //车牌号
    private String plateNo;
    //命令下发的Id
    private long commandId;
    //参数编码
    private String code;
    //命令
    private String value;
    //字段类型
    private String fieldType;
    //状态
    private String status;
    //命令下发的流水号
    private int sN;
    //参数更新日期
    private java.util.Date updateDate = new java.util.Date(0);

    public TerminalParam() {
        setCreateDate(new java.util.Date());
        setUpdateDate(new java.util.Date());
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

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        code = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String _value) {
        this.value = _value;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String value) {
        fieldType = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String value) {
        status = value;
    }

    public int getSN() {
        return sN;
    }

    public void setSN(int value) {
        sN = value;
    }

    public java.util.Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(java.util.Date value) {
        updateDate = value;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String simNo) {
        this.simNo = simNo;
    }

    public long getCommandId() {
        return commandId;
    }

    public void setCommandId(long commandId) {
        this.commandId = commandId;
    }
}