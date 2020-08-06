package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.SystemConfig;
import net.fxft.ascsareavoice.ltmonitor.service.ISystemConfigService;
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
