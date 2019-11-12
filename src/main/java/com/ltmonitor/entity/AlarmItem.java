package com.ltmonitor.entity;

import com.ltmonitor.entity.AlarmRecord;

import java.util.Date;

/**
 * 报警数据
 *
 */
public class AlarmItem {
	private int alarmId;
	private String simNo;
	private Date alarmTime;

	private String alarmKey;
	
	private String alarmType;
	
	private String alarmSource;

	private String status;
	
	private double latitude;
	
	private double longitude;
	
	private double velocity;
	
	private String location;
	
	private String plateNo;
	/**
	 * 报警时所在的区域、线路、关键点等
	 */
	private MapArea mapArea;

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	public String getAlarmSource() {
		return alarmSource;
	}

	public void setAlarmSource(String alarmSource) {
		this.alarmSource = alarmSource;
	}



	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}


	public AlarmItem(GPSRealData rd, String alarmType, String alarmSource) {
		this.setLatitude(rd.getLatitude());
		this.setLongitude(rd.getLongitude());
		this.setLocation(rd.getLocation());
		this.setPlateNo(rd.getPlateNo());
		this.alarmTime = rd.getSendTime();
		this.alarmType = alarmType;
		this.alarmSource = alarmSource;
		this.velocity = rd.getVelocity();
		this.status = AlarmRecord.STATUS_NEW;
		this.simNo = rd.getSimNo();
	}
	public AlarmItem(GPSRealData rd, String alarmType, String alarmSource, MapArea area) {
		this.setLatitude(rd.getLatitude());
		this.setLongitude(rd.getLongitude());
		this.setLocation(rd.getLocation());
		this.setPlateNo(rd.getPlateNo());
		this.alarmTime = rd.getSendTime();
		this.alarmType = alarmType;
		this.alarmSource = alarmSource;
		this.velocity = rd.getVelocity();
		this.status = AlarmRecord.STATUS_NEW;
		this.mapArea = area;
		this.simNo = rd.getSimNo();
	}






	public String getAlarmKey() {
		return alarmKey;
	}

	public void setAlarmKey(String alarmKey) {
		this.alarmKey = alarmKey;
	}

	public Date getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(Date alarmTime) {
		this.alarmTime = alarmTime;
	}

	public int getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSimNo() {
		return simNo;
	}

	public void setSimNo(String simNo) {
		this.simNo = simNo;
	}

	/**
	 * 区域报警或线路报警、关键点报警的时候，所在的区域\线路
	 */
	public MapArea getMapArea() {
		return mapArea;
	}

	public void setMapArea(MapArea mapArea) {
		this.mapArea = mapArea;
	}
}