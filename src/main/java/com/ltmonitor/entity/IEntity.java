package com.ltmonitor.entity;

public interface IEntity {
    /**
     * 实体ID
     *
     * @return
     */
    long getEntityId();

    void setEntityId(long value);

    /**
     * 所属企业Id
     *
     * @return
     */
    long getCompanyId();

    void setCompanyId(long companyId);

}