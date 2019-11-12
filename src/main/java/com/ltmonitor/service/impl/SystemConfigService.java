package com.ltmonitor.service.impl;

import com.ltmonitor.entity.SystemConfig;
import com.ltmonitor.service.ISystemConfigService;
import org.springframework.stereotype.Service;

/**
 * 系统参数配置服务
 *
 * @author admin
 */

@Service("systemConfigService")
public class SystemConfigService extends BaseService<Long, SystemConfig> implements ISystemConfigService {

    public SystemConfigService() {
        super(SystemConfig.class);
    }

    @Override
    public SystemConfig getSystemConfig() {
        return find("select * from SystemConfig");
    }


}
