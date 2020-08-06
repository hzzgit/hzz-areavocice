package net.fxft.ascsareavoice.ltmonitor.entity;

import java.io.Serializable;

/**
 * 基于saas的多租户模型
 * 多租户架构中，每个实体类都会有一个租户ID，用来进行数据隔离，区分不同公司或者不同租户的数据。主要是用作数据权限之用。
 *
 * @author DELL
 */
//@MappedSuperclass
public abstract class TenantEntity implements IEntity, Serializable {
    /**
     * 创建日期
     */
//	@Column(name = "createDate")
    protected java.util.Date createDate = new java.util.Date();
    /**
     * 多租户架构中，每个实体类都会有一个所属企业ID，用来进行数据隔离，区分不同公司或者不同租户的数据。主要是用作数据权限之用。
     */
//	@Column(name = "companyId", nullable = true, columnDefinition="INT default 0")
    private long companyId;
    /**
     * 备注
     */
//	@Column(name = "remark")
    private String remark;
    /**
     * 假删除标记
     */
//	@Column(name = "deleted", columnDefinition = "bit DEFAULT 0 ")
    private boolean deleted;
    /**
     * 数据的创建者姓名
     */
//	@Column(name = "owner")
    private String owner;


    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.util.Date value) {
        createDate = value;
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String value) {
        remark = value;
    }

//	public  boolean getDeleted() {
//		return deleted;
//	}

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean value) {
        deleted = value;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String value) {
        owner = value;
    }


    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

}