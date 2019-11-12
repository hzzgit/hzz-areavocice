package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

//停车报警记录表

@DbTable(value = "AlarmRecord", camelToUnderline = false)
public class AlarmRecord extends TenantEntity {

	public static String TURN_ON = "1"; // 报警开
	public static String TURN_OFF = "0"; // 报警关闭
	// public static String TYPE_WARN = "Warn"; //报警记录
	public static String TYPE_STATE = "State"; // 状态记录
	public static String TYPE_PARKING = "Parking"; // 停车记录
	public static String TYPE_ONLINE = "GpsOnline"; // 在线记录
	public static String TYPE_OFFLINE = "GpsOffline"; // 离线记录
	public static String TYPE_OFFSET_ROUTE = "OffsetRoute"; // 路线偏移
	public static String TYPE_ON_ROUTE = "OnRoute"; // 路线偏移
	public static String TYPE_OVER_SPEED_ON_ROUTE = "OverSpeedOnRoute"; // 分段限速
	public static String TYPE_ARRIVE_NOT_ON_TIME = "ArriveNotOnTime"; // 规定时间到达
	public static String TYPE_LEAVE_NOT_ON_TIME = "LeaveNotOnTime"; // 规定时间离开
	
	public static String TYPE_OVER_SPEED = "1"; //超速报警
	public static String TYPE_TIRED = "2"; //疲劳驾驶报警

	public static String TYPE_ACC_OFF = "AccOff";//acc熄火报警；

	public static String TYPE_ACC_ON = "AccOn";//acc点火工作报警；


	public static String TYPE_VIDEO_LOSS = "32";//视频丢失报警；
	public static String TYPE_VIDEO_COVER = "33";//视频遮挡报警；

	public static String TYPE_VIDEO_DISK_FAULT = "34";//视频存储器故障报警；
	public static String TYPE_UNUSUAL_DRIVE_BEHAVIOUR = "37";//异常驾驶行为报警；


	public static String TYPE_DISK_FULL = "DiskFull";


	public static String TYPE_NIGHT_DRIVING = "NightDriving";
	public static String TYPE_CROSS_BORDER = "CrossBorder"; // 围栏报警
	public static String TYPE_CROSS_BORDER_TERM = "CrossBorderByTerm"; // 围栏报警

	public static String TYPE_IN_AREA = "InArea"; // 进入区域
	public static String TYPE_IN_AREA_TERM = "InAreaByTerm"; // 进入区域
	public static String STATUS_NEW = "New"; // 开始状态
	public static String STATUS_OLD = "Old"; // 结束状态

	public static String ALARM_FROM_PLATFORM = "platform_alarm";// 平台报警;
	public static String ALARM_FROM_TERM = "terminal_alarm";// 终端报警;
	public static String ALARM_FROM_GOV = "gov_alarm";// 政府平台报警;
	public static String STATE_FROM_TERM = "terminal_state";// 终端状态变化报警;
	public static String ALARM_FROM_VIDEO = "video_alarm";// 视频报警;
	public static String ALARM_FROM_DRIVE = "drive_alarm";// 驾驶行为报警;

	public static String ALARM_FROM_ADAS = "adas_alarm";
	public static String ALARM_FROM_ADAS_DRIVER_STATE = "adas_driver_state_alarm";
	public static String ALARM_FROM_ADAS_BLIND_AREA = "adas_blind_ara_alarm";
	public static String ALARM_FROM_ADAS_TIRE_PRESSURE = "adas_tire_pressure_alarm";

	// 报警处理方式
	public static int PROCESS_LISTEN = 1; // 监听

	public static int PROCESS_TAKE_PICTURE = 2; // 拍照

	public static int PROCESS_SEND_TEXT = 3;// 文本下发

	public static int PROCESS_CLEAR = 4; // 报警解除

	public AlarmRecord() {
		setCreateDate(new Date());
		setAlarmSource(ALARM_FROM_TERM);
	}

	public AlarmRecord(GPSRealData rd, String alarmType, String alarmSource) {

		this.setLatitude(rd.getLatitude());
		this.setLongitude(rd.getLongitude());
		this.setLocation(rd.getLocation());
		this.setPlateNo(rd.getPlateNo());
		this.startTime = rd.getSendTime();
		this.alarmType = alarmType;
		this.alarmSource = alarmSource;
		this.velocity = rd.getVelocity();
		this.status = AlarmRecord.STATUS_NEW;
		this.vehicleId = rd.getVehicleId();
		this.endTime = this.startTime;
		setCreateDate(new Date());
	}
	@DbId
	@DbColumn(columnName = "alarmId")
	private long entityId;

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long value) {
		entityId = value;
	}

	/**
	 * 车辆Id
	 */
	private long vehicleId;

	private String plateNo;

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String value) {
		plateNo = value;
	}

	private Date startTime = new Date(0);

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date value) {
		startTime = value;
	}

	private Date endTime;

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date value) {
		endTime = value;
	}

	// 开始的地点
	private String location;

	public String getLocation() {
		return location;
	}

	public void setLocation(String value) {
		location = value;
	}

	// 最终的地点
	private String location1;

	public String getLocation1() {
		return location1;
	}

	public void setLocation1(String value) {
		location1 = value;
	}

	// 行驶速度
	private double velocity;

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double value) {
		velocity = value;
	}

	// 时间间隔，以分钟为单位
	private double timeSpan;

	public double getTimeSpan() {
		return timeSpan;
	}

	public void setTimeSpan(double value) {
		timeSpan = value;
	}

	// 记录的状态
	private String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String value) {
		status = value;
	}

	private double longitude;

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double value) {
		longitude = value;
	}

	private double latitude;

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double value) {
		latitude = value;
	}

	private double longitude1;

	public double getLongitude1() {
		return longitude1;
	}

	public void setLongitude1(double value) {
		longitude1 = value;
	}

	private double latitude1;

	public double getLatitude1() {
		return latitude1;
	}

	public void setLatitude1(double value) {
		latitude1 = value;
	}

	// 司机
	private String driver;

	public String getDriver() {
		return driver;
	}

	public void setDriver(String value) {
		driver = value;
	}

	// > 0 代表此记录已经报过警，避免重复报警,默认是0
	private int processed;

	public int getProcessed() {
		return processed;
	}

	public void setProcessed(int value) {
		processed = value;
	}

	// 报警处理时间
	private Date processedTime;

	// 记录类型
	private String alarmSource;

	public String getAlarmSource() {
		return alarmSource;
	}

	public void setAlarmSource(String value) {
		alarmSource = value;
	}

	// 记录子类型
	private String alarmType;

	public String getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(String value) {
		alarmType = value;
	}

	// 地点类型
	private long station;

	public long getStation() {
		return station;
	}

	public void setStation(long value) {
		station = value;
	}

	
	// 视频文件名称
	private String videoFileName;

	public String getVideoFileName() {
		return videoFileName;
	}

	public void setVideoFileName(String value) {
		videoFileName = value;
	}

	// 起始和终止的油量
	private double gas1;

	public double getGas1() {
		return gas1;
	}

	public void setGas1(double value) {
		gas1 = value;
	}

	private double gas2;

	public double getGas2() {
		return gas2;
	}

	public void setGas2(double value) {
		gas2 = value;
	}

	// 起始和终止的里程
	private double mileage1;

	public double getMileage1() {
		return mileage1;
	}

	public void setMileage1(double value) {
		mileage1 = value;
	}

	private double mileage2;

	public double getMileage2() {
		return mileage2;
	}

	public void setMileage2(double value) {
		mileage2 = value;
	}


	public int CompareTo(Object obj) {
		AlarmRecord vi = (AlarmRecord) obj;
		return getStartTime().compareTo(vi.getStartTime());

	}

	public Date getProcessedTime() {
		return processedTime;
	}

	public void setProcessedTime(Date processedTime) {
		this.processedTime = processedTime;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

}