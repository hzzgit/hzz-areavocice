package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.entity.SystemConfig;

/**
 * 报警设置服务，获取系统中某一个或所有的报警设置
 * @author admin
 *
 */
public interface ISystemConfigService extends IBaseService<Long, SystemConfig> {
	
	
	public SystemConfig getSystemConfig();
	

}
