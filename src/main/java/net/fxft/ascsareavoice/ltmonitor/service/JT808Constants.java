package net.fxft.ascsareavoice.ltmonitor.service;

import java.util.HashMap;
import java.util.Map;

public class JT808Constants { // 行车记录仪命令映射
	private static Map<String, Integer> recorderCmdMap = new HashMap<String, Integer>();
	public static java.util.Hashtable CommandDescr = new java.util.Hashtable();

	private static void loadMap() {
		CommandDescr.put("0x8001", "通用应答");
		CommandDescr.put("0x8003", "补传分包请求");
		CommandDescr.put("0x8804", "录音命令");
		CommandDescr.put("0x8201", "点名");
		CommandDescr.put("0x8600", "设置圆形区域");
		CommandDescr.put("0x8601", "删除圆形区域");
		CommandDescr.put("0x8602", "设置矩形区域");
		CommandDescr.put("0x8603", "删除矩形区域");
		CommandDescr.put("0x8604", "设置多边形区域");
		CommandDescr.put("0x8605", "删除多边形区域");
		CommandDescr.put("0x8606", "设置路线");
		CommandDescr.put("0x8607", "删除路线");
		CommandDescr.put("0x8802", "媒体检索");
		CommandDescr.put("0x8803", "媒体上传");
		CommandDescr.put("0x8805", "单条存储多媒体上传命令");
		CommandDescr.put("0x8202", "位置跟踪");
		CommandDescr.put("0x8300", "文本信息下发");
		CommandDescr.put("0x8100", "注册应答");
		CommandDescr.put("0x8103", "设置终端参数");
		CommandDescr.put("0x8104", "查询终端参数");
		CommandDescr.put("0x8106", "查询指定终端参数");
		CommandDescr.put("0x8105", "终端控制");
		CommandDescr.put("0x8203", "人工确认报警消息");
		CommandDescr.put("0x8801", "拍照");
		CommandDescr.put("0x8301", "设置事件");
		CommandDescr.put("0x8302", "提问下发");
		CommandDescr.put("0x8303", "菜单设置");
		CommandDescr.put("0x8304", "信息服务");
		CommandDescr.put("0x8400", "电话回拨");
		CommandDescr.put("0x8401", "电话本设置");
		CommandDescr.put("0x8500", "车辆控制");
		CommandDescr.put("0x8700", "行车记录仪采集");
		CommandDescr.put("0x8701", "行车记录参数下传命令");
		CommandDescr.put("0x8701", "行车记录参数下传命令");
		CommandDescr.put("0x8900", "数据下行透传");
		CommandDescr.put("0x8800", "多媒体数据上传应答");
		CommandDescr.put("0x8801", "摄像头立即拍摄命令");

		CommandDescr.put("0x0001", "终端应答");
		CommandDescr.put("0x0002", "终端心跳");
		CommandDescr.put("0x0003", "终端注销");
		CommandDescr.put("0x0102", "终端鉴权");
		CommandDescr.put("0x0100", "终端注册");
		CommandDescr.put("0x0104", "查询终端参数应答");
		CommandDescr.put("0x0200", "位置信息");
		CommandDescr.put("0x0201", "点名应答");
		CommandDescr.put("0x0301", "事件报告");
		CommandDescr.put("0x0302", "提问应答");
		CommandDescr.put("0x0303", "终端信息点播");
		CommandDescr.put("0x0702", "驾驶员身份采集上报");
		CommandDescr.put("0x0701", "电子运单上报");
		CommandDescr.put("0x0704", "定位数据补报");
		CommandDescr.put("0x0800", "多媒体事件信息上传");
		CommandDescr.put("0x0801", "多媒体数据上传");
		CommandDescr.put("0x0802", "存储多媒体数据检索应答");
		CommandDescr.put("0x0805", "拍照命令应答");
		CommandDescr.put("0x0900", "数据上行透传");


		CommandDescr.put("0xA004", "终端请求地址");
		CommandDescr.put("0xB004", "终端请求地址应答");
		CommandDescr.put("0xA005", "终端AGPS请求");
		CommandDescr.put("0xB005", "终端AGPS应答");

		CommandDescr.put("0x9101", "实时音视频请求");
		CommandDescr.put("0x9102", "音视频实时传输控制");
		CommandDescr.put("0x9003", "查询终端音视频属性");
		CommandDescr.put("0x1003", "终端上传音视频属性");
		CommandDescr.put("0x1005", "终端上传乘客流量");
		CommandDescr.put("0x9105", "实时音视频状态通知");
		CommandDescr.put("0x9205", "查询音视频资源列表");
		CommandDescr.put("0x1205", "终端上传音视频资源列表");
		CommandDescr.put("0x9201", "远程录像回放请求");
		CommandDescr.put("0x9202", "远程录像回放控制");
		CommandDescr.put("0x9206", "文件上传指令");
		CommandDescr.put("0x1206", "文件上传完成通知");
		CommandDescr.put("0x9207", "文件上传控制");

		CommandDescr.put("0x1210", "报警附件信息消息");
		CommandDescr.put("0x1211", "文件信息上传");
		CommandDescr.put("0x1212", "文件上传完成消息");
		CommandDescr.put("0x9208", "报警附件上传指令");
		CommandDescr.put("0x9212", "文件上传完成消息应答");

		recorderCmdMap.put("driverInfo", 0x01); // 驾驶员代码
		recorderCmdMap.put("clock", 0x02); // 时钟
		recorderCmdMap.put("mileageIn360h", 0x03);// 360小时内里程
		recorderCmdMap.put("feature", 0x04);
		recorderCmdMap.put("speedIn360h", 0x05);
		recorderCmdMap.put("vehicleInfo", 0x06);
		recorderCmdMap.put("accident", 0x07);
		recorderCmdMap.put("mileageIn2d", 0x08); // 采集2天内的里程
		recorderCmdMap.put("speedIn2d", 0x09); // 采集2天内的行驶速度
		recorderCmdMap.put("overdrive", 0x11); // 疲劳驾驶 超过3小时的数据
		recorderCmdMap.put("setdriverInfo", 0x81); // 设置驾驶员代码
		recorderCmdMap.put("setvehicleInfo", 0x82); // 设置车辆VIN、车牌等
		recorderCmdMap.put("setclock", 0xC2); // 设置时钟
		recorderCmdMap.put("setfeature", 0xC3); // 设置车辆特征系数

	}

	public static Integer getRecorderCmd(String cmdType) {
		if (recorderCmdMap.isEmpty()) {
			loadMap();
		}
		return recorderCmdMap.get(cmdType);
	}

	public static String GetDescr(String cmdType) {
		if (CommandDescr.isEmpty()) {
			loadMap();
		}
		return "" + CommandDescr.get(cmdType);
	}

	// 录音命令
	public static final int CMD_AUDIO_RECORDER = 0x8804;

	// 点名
	public static final int CMD_REAL_MONITOR = 0x8201;

	// 位置跟踪
	public static final int CMD_LOCATION_MONITOR = 0x8202;

	// 设置圆形区域
	public static final int CMD_CIRCLE_CONFIG = 0x8600;

	// 删除围栏
	public static final int CMD_DELETE_CIRCLE = 0x8601;

	// 设置矩形区域
	public static final int CMD_RECT_CONFIG = 0x8602;
	// 删除围栏
	public static final int CMD_DELETE_RECT = 0x8603;

	// 设置多边形区域
	public static final int CMD_POLYGON_CONFIG = 0x8604;
	// 删除围栏
	public static final int CMD_DELETE_POLYGON = 0x8605;

	// 设置线路
	public static final int CMD_ROUTE_CONFIG = 0x8606;

	public static final int CMD_DELETE_ROUTE = 0x8607;

	// 媒体检索
	public static final int CMD_MEDIA_SEARCH = 0x8802;

	// 媒体上传
	public static final int CMD_MEDIA_UPLOAD = 0x8803;

	// 单条存储多媒体上传命令
	public static final int CMD_MEDIA_UPLOAD_SINGLE = 0x8805;

	// 临时位置跟踪
	public static final int CMD_TEMP_TRACK = 0x8202;

	// 文本信息下发
	public static final int CMD_SEND_TEXT = 0x8300;

	// 设置终端参数
	public static final int CMD_CONFIG_PARAM = 0x8103;

	// 查询终端参数
	public static final int CMD_QUERY_PARAM = 0x8104;

	// 查询指定终端参数
	public static final int CMD_QUERY_SPECIAL_PARAM = 0x8106;

	// 终端控制
	public static final int CMD_CONTROL_TERMINAL = 0x8105;

	// 拍照
	public static final int CMD_TAKE_PHOTO = 0x8801;

	// 设置事件
	public static final int CMD_EVENT_SET = 0x8301;
	// 提问下发
	public static final int CMD_QUESTION = 0x8302;
	// 菜单设置
	public static final int CMD_SET_MENU = 0x8303;
	// 信息服务
	public static final int CMD_INFORMATION = 0x8304;

	// 电话回拨
	public static final int CMD_DIAL_BACK = 0x8400;
	// 电话本设置
	public static final int CMD_PHONE_BOOK = 0x8401;
	// 车辆控制
	public static final int CMD_CONTROL_VEHICLE = 0x8500;

	// 透明传输
	public static final int CMD_TRANS = 0x8900;

	// 行车记录仪采集
	public static final int CMD_VEHICLE_RECORDER = 0x8700;

	// 行车记录参数下传命令
	public static final int CMD_VEHICLE_RECORDER_CONFIG = 0x8701;
	//报警解除
	public static final int CMD_CLEAR_ALARM = 0x8203;


	//实时音视频请求
	public  static  final  int CMD_REALTIME_VIDEO_REQ = 0x9101;
	//停止实时音视频请求
	public  static  final  int CMD_REALTIME_VIDEO_STOP = 0x9102;
	//查询音视频资源目录
	public  static  final  int CMD_SEARCH_VIDEO_RESOURCE = 0x9205;

	//视频回放
	public  static  final  int CMD_VIDEO_PLAY_BACK = 0x9201;
	//视频回放控制
	public  static  final  int CMD_VIDEO_PLAY_BACK_CONTROL = 0x9202;
	//录像下载
	public static final int CMD_DOWNLOAD_VIDEO = 0x9206;
	//录像下载控制
	public static final int CMD_DOWNLOAD_VIDEO_CONTROL = 0x9207;


	/**
	 * 上传完成通知
	 */
	public static final int CMD_DOWNLOAD_COMPLETED_INFOM = 0x1206;

}