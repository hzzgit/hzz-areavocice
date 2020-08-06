package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.entity.AlarmConfig;

/**
 * 报警设置服务，获取系统中某一个或所有的报警设置
 * @author admin
 *
 */
public interface IAlarmConfigService extends IBaseService<Long, AlarmConfig> {
	/**
	 * 根据报警类型和报警来源获取报警设置
	 * @param alarmType 报警类型, 参见AlarmRecord类中的常量定义
	 * @param alarmSource 报警来源, 参见AlarmRecord类中的常量定义
	 * @return
	 */
	AlarmConfig getAlarmConfig(String alarmType, String alarmSource);

	/**
	 * 根据报警类型和报警来源获取报警设置
	 * @param alarmType 报警类型, 参见AlarmRecord类中的常量定义
	 * @return
	 */
//	AlarmConfig getAlarmConfig(long depId,String alarmType);

	/**
	 * 获得默认的报警配置
	 * @param alarmType
	 * @param alarmSource
	 * @return
	 */
//	AlarmConfig getDefaultAlarmConfig(String alarmType, String alarmSource);
	/**
	 * 某一报警类型是否启用
	 * @param alarmType　报警类型
	 * @param alarmSource　报警来源
	 * @return
	 */

	boolean isAlarmEnabled(String alarmType, String alarmSource);

	String getAlarmVideoChannels(String alarmType, String alarmSource);

	String getAlarmPhotoChannels(String alarmType, String alarmSource);

	boolean isStatisticAlarm(String alarmType, String alarmSource);

//	boolean isAlarmEnabledByVehicleId(String alarmType, String alarmSource, long vehicleId);
	/**
	 * 获取某一组织机构下的报警配置
	 * @param depId
	 * @return
	 */
//	List<AlarmConfig> getAlarmConfigByDepId(long depId);
	/**
	 * 删除某一部门下的报警配置
	 * @param depId
	 */
//	void deleteAlarmConfigByDepId(long depId);

//	List<AlarmConfig> getTerminalAlarmConfig();
}
