package com.ltmonitor.util;

import java.util.Date;

public class Constants {

	/**
	 * 百度地图围栏
	 */
	public static String MAP_BAIDU = "baidu";
	/**
	 * 谷歌地图围栏,凡是基于火星坐标系的地图，都是此类型
	 */
	public static String MAP_GOOGLE = "google";
	//wgs 84坐标系，gps终端设备的原生坐标
	public static String MAP_GPS = "gps";
	/**
	 * 显示状态类型 为 刹车转向状态
	 */
	public static String STATE_TYPE_BARKER = "braker";
	/**
	 * 显示状态类型为ACC状态
	 */
	public static String STATE_TYPE_ACC = "acc";
	
	public static String ALARM_TABLE_BY_MONTH = "yyyyMM";
	

	public static String ALARM_TABLE_BY_DAY = "yyyyMMdd";
	
	public static String ALARM_TABLE_FORMAT = "yyyyMM";
	
	public static String getAlarmTableName()
	{
		return getAlarmTableName(new Date());
	}
	
	public static String getAlarmTableName(Date d)
	{

		String tableName = "NewAlarm"
				+ DateUtil.toStringByFormat(d, ALARM_TABLE_FORMAT);
		return tableName;
	}
	
	/**
	 * 将分钟转换成时，分，秒的描述方式，如90分钟，转换成1小时30分
	 * 用于统计时间的描述
	 * @param minutes
	 * @return
	 */
	public static String getIntervalDescr(double minutes) {
		if (minutes <= 0)
			return "0";
		StringBuilder descr = new StringBuilder();

		boolean d = false;
		if (minutes >= 1440) {
			descr.append(((int) minutes) / 1440 + "天");
			minutes -= ((int) minutes / 1440) * 1440;
			d = true;
		}
		if (minutes >= 60) {
			descr.append(((int) minutes) / 60 + "小时");
			minutes -= ((int) minutes / 60) * 60;
		}
		if (minutes >= 1) {
			descr.append((int) minutes + "分");
			minutes -= (int) minutes;
		}
		//如果有天，则时间描述过长，就去掉秒的描述
		if(d == false) {
			int seconds = (int) (minutes * 60);
			if (seconds > 0) {
				descr.append(seconds + "秒");
			}
		}

		return descr.toString();
	}


	public static String formatFileSize(String fieldValue)
	{
		int index = fieldValue.indexOf(".");
		if(index >0)
		{
			//去掉小数点部分
			fieldValue = fieldValue.substring(0,index);
		}
		if(fieldValue.length() > 3)
		{
			StringBuilder sb = new StringBuilder();
			String strRemain = fieldValue ;
			int remainLength = strRemain.length();
			while(remainLength > 3)
			{
				sb.insert(0,strRemain.substring(remainLength - 3)).insert(0,",");
				strRemain = strRemain.substring(0, remainLength - 3);
				remainLength = strRemain.length();
			}
			if(remainLength > 0)
				sb.insert(0,strRemain);
			fieldValue = sb.toString();
		}
		return fieldValue;
	}

}
