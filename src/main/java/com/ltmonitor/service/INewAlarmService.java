package com.ltmonitor.service;

import com.ltmonitor.entity.Alarm;
import com.ltmonitor.entity.GPSRealData;

/**
 * 最新报警入库服务
 * @author admin
 *
 */
public interface INewAlarmService  {

//	public abstract void start();
//
//	public abstract void stopService();

	void enQueue(Alarm newAlarm, String remark);

	/**
	 * 插入最新的报警到数据库
	 * @param alarmType
	 * @param alarmSource
	 * @param rd
	 */
	Alarm insertAlarm(String alarmSource, String alarmType,
					  GPSRealData rd, String remark);


}