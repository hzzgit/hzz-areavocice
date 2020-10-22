package net.fxft.ascsareavoice.service.GpsFliter;

import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.T808Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GPSFilter {

	private static final Logger log = LoggerFactory.getLogger(GPSFilter.class);
	//用于存车辆点位的Map
	private final static ConcurrentHashMap<String,List<GpsInfo>> gpsMap = new ConcurrentHashMap<String, List<GpsInfo>>();
	private final static ConcurrentHashMap<String,List<GpsInfo>> gpsUnvalidMap = new ConcurrentHashMap<String, List<GpsInfo>>();
	private final static Double chinaMinLat = 3.85;
	private final static Double chinaMaxLat = 53.55;
	private final static Double chinaMinLon = 73.55;
	private final static Double chinaMaxLon= 135.08333333333334;





	/**
	 * GPS飘点过滤JT0200类型
	 * @param gps
	 * @param vehicleId
	 * @return 返回值描述，data为gps数据，type：0是正常点，1是漂移点，2是时间异常点，3是经纬度异常点，6.未验证点
	 */
	public static List<Map<String,Object>> gpsFilter(GpsInfo gps, String vehicleId){
		//结果集集合
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			if(gps.getAltitude() == 0 && gps.getVelocity() == 0 && gps.getDirection() == 0 && !gps.isValid()){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 6);
				resultList.add(m);
				return resultList;
			}
			//如果时间格式不对或接收到未来时间，则时间异常点，这种的不会入库，所以先直接过滤
			if(!isDate(gps.getSendTime())){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 2);
				resultList.add(m);
				return resultList;
			}
			//设备上传时间早于或晚于设备记录时间20小时，则算时间异常点
			if(LocalDateTimeUtil.StringConverLocalDateTime(gps.getSendTime())
					.isBefore(LocalDateTimeUtil.StringConverLocalDateTime(gps.getSysRecordTime()).minusHours(5))
					|| LocalDateTimeUtil.StringConverLocalDateTime(gps.getSendTime())
					.isAfter(LocalDateTimeUtil.StringConverLocalDateTime(gps.getSysRecordTime()).plusHours(5))){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 2);
				resultList.add(m);
				return resultList;
			}
			//经纬度在中国范围外的算经纬度异常点
			if(gps.getLatitude() < chinaMinLat || gps.getLatitude() > chinaMaxLat
					|| gps.getLongitude() < chinaMinLon || gps.getLongitude() > chinaMaxLon ){
				log.debug("经纬度异常点:" + gps.toString());
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 3);
				resultList.add(m);
				return resultList;
			}
			if(!gpsMap.containsKey(vehicleId)){
				List<GpsInfo> jtList = Collections.synchronizedList(new ArrayList<GpsInfo>());
				gps.setPointType(1);
				//如果gpsMap里没有key为这个车辆对象的集合，说明这个车辆的点位是第一次传进来了，作为可疑点进行暂存，等第二个漂移点
				jtList.add(gps);
				gpsMap.put(vehicleId, jtList);
				return null;
			}else{
				List<GpsInfo> jtList = gpsMap.get(vehicleId);
				synchronized (jtList) {
					if(LocalDateTimeUtil.StringConverLocalDateTime(gps.getSendTime())
							.isBefore(LocalDateTimeUtil.StringConverLocalDateTime(jtList.get(jtList.size()-1).getSendTime()))){
						Map<String,Object> m = new HashMap<String, Object>();
						m.put("data", gps);
						m.put("type", 2);
						resultList.add(m);
						return resultList;
					}
					//控制是否返回数据
					Boolean isReturn = false;
					for (int i = jtList.size() - 1; i >= 0 ; i--) {
						int mileage =  getDistance(jtList.get(i).getLatitude(), jtList.get(i).getLongitude(),
								gps.getLatitude(), gps.getLongitude());
						double speed = 0;
						//如果里程为0，就默认其没速度
						if(mileage != 0){
							long time = timeDiff(gps.getSendTime(),jtList.get(i).getSendTime());
							if(time == 0){
								Map<String,Object> m = new HashMap<String, Object>();
								m.put("data", gps);
								m.put("type", 2);
								resultList.add(m);
								return resultList;
							}
							speed = mileage/time * 3.6;
							if(speed <= 160){
								if(speed > 130 && !gps.isValid()){
									Map<String,Object> m = new HashMap<String, Object>();
									m.put("data", gps);
									m.put("type", 6);
									resultList.add(m);
									return resultList;
								}
								//如果两点之间速度小于160KM/H，那么将之前的可疑点视为正常点
								jtList.get(i).setPointType(0);
								isReturn = true;
							}else{
								//如果两点之间速度小大于160KM/H，那么将这该点视为可疑点
								if(!isReturn){
									gps.setPointType(1);
									//一般默认第一点为正常点
									if(i != 0){
										jtList.get(i).setPointType(1);
									}
								}
								//钝角三角形判断
								if(i == 0 && jtList.size() == 2){
									if(jtList.get(1).getPointType() == 0){
										jtList.get(0).setPointType(1);
									}
								}
							}
						}else{
							if(!gps.isValid()){
								jtList.get(i).getAddUnvalidCount().addAndGet(1);
								log.debug("未定位点已累计" + jtList.get(i).getAddUnvalidCount() + "点:"+gps.toString());
								Map<String,Object> m = new HashMap<String, Object>();
								m.put("data", gps);
								m.put("type", 6);
								resultList.add(m);
								return resultList;
							}else{
								//如果未定位的点的累计超过五个，则增加一个定位点比较的判断
								if(jtList.get(i).getAddUnvalidCount().intValue() > 5){
									if(!gpsUnvalidMap.containsKey(vehicleId)){
										List<GpsInfo> jtListTemp = Collections.synchronizedList(new ArrayList<GpsInfo>());
										gps.setPointType(1);
										jtListTemp.add(gps);
										gpsUnvalidMap.put(vehicleId, jtListTemp);
										log.info("初始化暂存空间,key:" + vehicleId+",gpsUnvalidMap大小:"+gpsUnvalidMap.size());
										return null;
									}else{
										if(gpsUnvalidMap.get(vehicleId).size() < 5){
											List<GpsInfo> jtListTemp = gpsUnvalidMap.get(vehicleId);
											gps.setPointType(0);
											jtListTemp.add(gps);
											log.info("初始化暂存空间,key:" + vehicleId+",gpsUnvalidMap大小:"+gpsUnvalidMap.size());
											return null;
										}else{
											jtList.addAll(gpsUnvalidMap.get(vehicleId));
											gpsUnvalidMap.remove(vehicleId);
										}
									}
								}
							}
							if(jtList.size() > 1 && jtList.get(i).getPointType() == 1){
								if(!isReturn){
									gps.setPointType(1);
								}
							}else{
								jtList.get(i).setPointType(0);
								isReturn = true;
							}
						}
					}
					if(isReturn){
						//可返回时，如果未定位点集合还有存在数据，那就说明， 经纬度发生变化时，没有超过5个暂存点，则全部按照未定位点返回
						if(gpsUnvalidMap.containsKey(vehicleId)){
							List<GpsInfo> jtListTemp = gpsUnvalidMap.get(vehicleId);
							if(jtListTemp.size() > 0){
								for (int i = 0; i < jtListTemp.size(); i++) {
									Map<String,Object> m = new HashMap<String, Object>();
									m.put("data", jtListTemp.get(i));
									m.put("type", 6);
									resultList.add(m);
									log.info("处理暂存空间的数据，为未定位点："+ jtListTemp.get(i).toString());
								}
							}
							gpsUnvalidMap.remove(vehicleId);
						}
						//返回除了当前点位外所有存在内存中的点
						for (int i = 0; i < jtList.size(); i++) {
							Map<String,Object> m = new HashMap<String, Object>();
							if(jtList.get(i).getPointType() == 0){
								m.put("data", jtList.get(i));
								m.put("type", 0);
								resultList.add(m);
							}
							else{
								m.put("data", jtList.get(i));
								m.put("type", 1);
								resultList.add(m);
							}
						}
						jtList.clear();
						gps.setPointType(0);
						jtList.add(gps);

						return resultList;
					}else{
						gps.setPointType(1);
						jtList.add(gps);
					}
				}
			}
		} catch (Exception e) {
			log.debug("JT0200类型GPS点位过滤异常",e);
		}
		return null;
	}

	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public boolean allequals(double slat,double slng,double elat,double elng){
		return Math.abs(slat - elat) <= 0.000001 && Math.abs(slng - elng) <= 0.000001;
	}

/*	*//**
	 * GPS飘点过滤JT0704类型
	 * @param gpsList
	 * @param vehicleId
	 * @param tm
	 * @return 返回值描述，data为gps数据，type：0是正常点，1是漂移点，2是时间异常点，3是经纬度异常点，6.未验证点
	 *//*
	public static List<Map<String,Object>> gpsFilter(List<JT_0200> gpsList, String vehicleId, T808Message tm){
		//结果集集合
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			//设置个开关，用于判断0704中所有点位的经纬度是否全部一致
			boolean same = true;
			for (int i = 0; i < gpsList.size(); i++) {
				if(i == 0){
					continue;
				}
				if(Math.abs(gpsList.get(i).getLatitude() - gpsList.get(i-1).getLatitude()) <= 0.000001
						&& Math.abs(gpsList.get(i).getLongitude() - gpsList.get(i-1).getLongitude()) <= 0.000001){
					continue;
				}else {
					same = false;
					break;
				}
			}
			if(gpsList.size() == 1 || same){
				for (int i = 0; i < gpsList.size() ; i++) {
					Map<String,Object> m = new HashMap<String, Object>();
					GpsInfo g = new GpsInfo();
					g.setVehicleId(Long.valueOf(vehicleId));
					g.setSimNo(tm.getSimNo());
					g.setSendTime(gpsList.get(i).getTime());
					g.setUuId(tm.getDevMsgAttr().getUuid());
					Long sysRecordTime = tm.getDevMsgAttr().getCreateTime();
					String date = sdf.format(new Date(sysRecordTime));
					g.setSysRecordTime(date);
					double latitude = ((double)gpsList.get(i).getLatitude())/1000000;
					g.setLatitude(latitude);
					double longitude = ((double)gpsList.get(i).getLongitude())/1000000;
					g.setLongitude(longitude);
					g.setValid(gpsList.get(i).isValid());
					g.setDirection(gpsList.get(i).getCourse());
					g.setAltitude(gpsList.get(i).getAltitude());
					g.setVelocity(gpsList.get(i).getSpeed());
					m.put("data", g);
					m.put("type", 7);
					resultList.add(m);
				}
				return resultList;
			}else{
				for (int i = 0; i < gpsList.size() ; i++) {
					Map<String,Object> m = new HashMap<String, Object>();
					//封装GPS数据
					GpsInfo g = new GpsInfo();
					g.setVehicleId(Long.valueOf(vehicleId));
					g.setSimNo(tm.getSimNo());
					g.setSendTime(gpsList.get(i).getTime());
					g.setUuId(tm.getDevMsgAttr().getUuid());
					Long sysRecordTime = tm.getDevMsgAttr().getCreateTime();
					String date = sdf.format(new Date(sysRecordTime));
					g.setSysRecordTime(date);
					double latitude = ((double)gpsList.get(i).getLatitude())/1000000;
					g.setLatitude(latitude);
					double longitude = ((double)gpsList.get(i).getLongitude())/1000000;
					g.setLongitude(longitude);
					g.setValid(gpsList.get(i).isValid());
					g.setDirection(gpsList.get(i).getCourse());
					g.setAltitude(gpsList.get(i).getAltitude());
					g.setVelocity(gpsList.get(i).getSpeed());
					if(i == 0){
						if(g.getAltitude() == 0 && g.getVelocity() == 0 && g.getDirection() == 0 && !g.isValid()){
							m.put("data", g);
							m.put("type", 6);
							resultList.add(m);
						}else if(!isDate(g.getSendTime())){
							m.put("data", g);
							m.put("type", 2);
							resultList.add(m);
						}else if(g.getLatitude() < chinaMinLat || g.getLatitude() > chinaMaxLat
								|| g.getLongitude() < chinaMinLon || g.getLongitude() > chinaMaxLon ){
							m.put("data", g);
							m.put("type", 3);
							resultList.add(m);
						}else{
							m.put("data", g);
							m.put("type", 0);
							resultList.add(m);
						}
					}else{
						if(g.getAltitude() == 0 && g.getVelocity() == 0 && g.getDirection() == 0 && !g.isValid()){
							m.put("data", g);
							m.put("type", 6);
							resultList.add(m);
						}else if(!isDate(g.getSendTime())){
							m.put("data", g);
							m.put("type", 2);
							resultList.add(m);
						}else if(g.getLatitude() < chinaMinLat || g.getLatitude() > chinaMaxLat
								|| g.getLongitude() < chinaMinLon || g.getLongitude() > chinaMaxLon ){
							m.put("data", g);
							m.put("type", 3);
							resultList.add(m);
						}else{
							GpsInfo gps = new GpsInfo();
							boolean flag = false;
							for (int j = resultList.size() - 1 ; j >= 0 ; j--) {
								if(resultList.get(j).get("type").toString().equals("0")){
									gps = (GpsInfo) resultList.get(j).get("data");
									break;
								}
								if(j == 0){
									m.put("data", g);
									m.put("type", 0);
									resultList.add(m);
									flag = true;
								}
							}
							if(flag){
								continue;
							}
							int mileage =  getDistance(gps.getLatitude(), gps.getLongitude(),
									g.getLatitude(), g.getLongitude());
							if(mileage != 0){
								long time = timeDiff(g.getSendTime(),gps.getSendTime());
								if(time == 0){
									m.put("data", g);
									m.put("type", 2);
									resultList.add(m);
								}else{
									double speed = mileage/time * 3.6;
									if(speed <= 160){
										if(speed > 130 && !gps.isValid()){
											m.put("type", 6);
										}else{
											m.put("type", 0);
										}
										m.put("data", g);
										resultList.add(m);
									}else{
										m.put("data", g);
										m.put("type", 1);
										resultList.add(m);
									}
								}
							}else{
								if(!g.isValid()){
									m.put("type", 6);
								}else{
									m.put("type", 0);
								}
								m.put("data", g);
								resultList.add(m);
							}
						}
					}
				}
			}

			//用于计数返回集合正常点位的数量，如果只有一个，那么将这个正常点标记成未定位点
			int count = 0;
			//用户记录正常点的索引
			int index = 0;
			for (int i = 0; i < resultList.size() ; i++) {
				if(resultList.get(i).get("type").toString().equals("0")){
					count ++;
					index = i;
				}
				if(i == resultList.get(i).size() - 1){
					if(count <= 1){
						resultList.get(index).put("type",6);
					}
				}
			}
			return resultList;
		}catch (Exception e){
			log.debug("JT0704类型GPS点位过滤异常，点位" + gpsList.toString(),e);
		}
		return null;
	}*/

/*	*//**
	 * JT_2021 过滤
	 * @param gps
	 * @param vehicleId
	 * @param tm
	 * @return
	 *//*
	public static Map<String,Object> gpsFilter(JT_0200 gps, String vehicleId, T808Message tm){
		Map<String,Object> m = new HashMap<String, Object>();
		GpsInfo g = new GpsInfo();
		g.setVehicleId(Long.valueOf(vehicleId));
		g.setSimNo(tm.getSimNo());
		g.setSendTime(gps.getTime());
		g.setUuId(tm.getDevMsgAttr().getUuid());
		Long sysRecordTime = tm.getDevMsgAttr().getCreateTime();
		String date = sdf.format(new Date(sysRecordTime));
		g.setSysRecordTime(date);
		double latitude = ((double)gps.getLatitude())/1000000;
		g.setLatitude(latitude);
		double longitude = ((double)gps.getLongitude())/1000000;
		g.setLongitude(longitude);
		g.setValid(gps.isValid());
		g.setDirection(gps.getCourse());
		g.setAltitude(gps.getAltitude());
		g.setVelocity(gps.getSpeed());
		if(g.getAltitude() == 0 && g.getVelocity() == 0 && g.getDirection() == 0 && !g.isValid()){
			m.put("data", g);
			m.put("type", 6);
		}else if(!isDate(g.getSendTime())){
			m.put("data", g);
			m.put("type", 2);
		}else if(g.getLatitude() < chinaMinLat || g.getLatitude() > chinaMaxLat
				|| g.getLongitude() < chinaMinLon || g.getLongitude() > chinaMaxLon ){
			m.put("data", g);
			m.put("type", 3);
		}else{
			m.put("data", g);
			m.put("type", 0);
		}
		return m;
	}*/



	/**
	 * 通过经纬度获取距离(单位：米)
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static int getDistance(double lat1, double lng1, double lat2,double lng2) {

		double EARTH_RADIUS = 6378.137;
		double x1, y1, z1, x2, y2, z2;
		lat1 = Math.PI * lat1 / 180.0;
		lng1 = Math.PI * lng1 / 180.0;
		double radiusCosLat = EARTH_RADIUS * Math.cos(lat1);
		x1 = radiusCosLat * Math.sin(lng1);
		y1 = (EARTH_RADIUS * Math.sin(lat1));
		z1 = (radiusCosLat * Math.cos(lng1));
		z1 = -z1;
		lat2 = Math.PI * lat2 / 180.0;
		lng2 = Math.PI * lng2 / 180.0;
		double radiusCosLat2 = EARTH_RADIUS * Math.cos(lat2);
		x2 = radiusCosLat2 * Math.sin(lng2);
		y2 = (EARTH_RADIUS * Math.sin(lat2));
		z2 = (radiusCosLat2 * Math.cos(lng2));
		z2 = -z2;
		double result = EARTH_RADIUS * Math.acos(CalcVectorCos(x1, y1, z1, x2, y2, z2)) * 1000;
		return (int)result;
	}
	/**
	 * 计算2个向量之间的夹角余玄值
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 * @return
	 */
	private static double CalcVectorCos(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		double l1 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
		double l2 = Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2);
		double result = (x1 * x2 + y1 * y2 + z1 * z2) / (l1 * l2);
		if(result > 0.99999999999999 && result < 1) result = 1;
		if(result < -1) result = -1;
		if(result > 1) result = 1;
		return result;
	}

	/**
	 * 时间差，(单位：秒)
	 * @param endTime
	 * @param startTime
	 * @return
	 */
	private static long timeDiff(String endTime,String startTime){
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate=inputFormat.parse(startTime);
			endDate=inputFormat.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//得到两个日期对象的总毫秒数
		long firstDateMilliSeconds = startDate.getTime();
		long secondDateMilliSeconds = endDate.getTime();
		//得到两者之差
		long firstMinusSecond = secondDateMilliSeconds - firstDateMilliSeconds;
		//返回秒
		return firstMinusSecond / 1000;
	}


	/**
	 * 验证一个字符串是否是日期
	 * @param strDate
	 * @return
	 */
	private static boolean isDate(String strDate) {
		Pattern pattern = Pattern
				.compile("((\\d{2}(([02468][048])|([13579][26]))[\\-\\/\\s]?"
						+ "((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
						+ "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
						+ "(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468]"
						+ "[1235679])|([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))"
						+ "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]"
						+ "?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))"
						+ "))))(\\s((([0-1][0-9])|(2?[0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))");
		Matcher m = pattern.matcher(strDate);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}

	}



	private final static ConcurrentHashMap<String,List<GpsInfo>> gpsMapTemplate = new ConcurrentHashMap<String, List<GpsInfo>>();
	private final static ConcurrentHashMap<String,List<GpsInfo>> gpsUnvalidMapTemplate = new ConcurrentHashMap<String, List<GpsInfo>>();
	public static ConcurrentHashMap<String, List<GpsInfo>> getGpsMapTemplate() {
		return gpsMapTemplate;
	}

	public static ConcurrentHashMap<String, List<GpsInfo>> getGpsUnvalidMapTemplate() {
		return gpsUnvalidMapTemplate;
	}




	/**
	 * GPS飘点过滤JT0200类型
	 * @param gps
	 * @param vehicleId
	 * @return 返回值描述，data为gps数据，type：0是正常点，1是漂移点，2是时间异常点，3是经纬度异常点，6.未验证点
	 */
	public static List<Map<String,Object>> gpsRecalculationFilter(GpsInfo gps, String vehicleId){
		//结果集集合
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		try {
			if(gps.getAltitude() == 0 && gps.getVelocity() == 0 && gps.getDirection() == 0 && !gps.isValid()){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 6);
				resultList.add(m);
				return resultList;
			}
			//如果时间格式不对或接收到未来时间，则时间异常点，这种的不会入库，所以先直接过滤
			if(!isDate(gps.getSendTime())){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 2);
				resultList.add(m);
				return resultList;
			}
			//经纬度在中国范围外的算经纬度异常点
			if(gps.getLatitude() < chinaMinLat || gps.getLatitude() > chinaMaxLat
					|| gps.getLongitude() < chinaMinLon || gps.getLongitude() > chinaMaxLon ){
				Map<String,Object> m = new HashMap<String, Object>();
				m.put("data", gps);
				m.put("type", 3);
				resultList.add(m);
				return resultList;
			}
			if(!gpsMapTemplate.containsKey(vehicleId)){
				List<GpsInfo> jtList = Collections.synchronizedList(new ArrayList<GpsInfo>());
				gps.setPointType(1);
				//如果gpsMap里没有key为这个车辆对象的集合，说明这个车辆的点位是第一次传进来了，作为可疑点进行暂存，等第二个漂移点
				jtList.add(gps);
				gpsMapTemplate.put(vehicleId, jtList);
				return null;
			}else{
				List<GpsInfo> jtList = gpsMapTemplate.get(vehicleId);
				synchronized (jtList) {
					if(LocalDateTimeUtil.StringConverLocalDateTime(gps.getSendTime())
							.isBefore(LocalDateTimeUtil.StringConverLocalDateTime(jtList.get(jtList.size()-1).getSendTime()))){
						Map<String,Object> m = new HashMap<String, Object>();
						m.put("data", gps);
						m.put("type", 2);
						resultList.add(m);
						return resultList;
					}
					//控制是否返回数据
					Boolean isReturn = false;
					for (int i = jtList.size() - 1; i >= 0 ; i--) {
						int mileage =  getDistance(jtList.get(i).getLatitude(), jtList.get(i).getLongitude(),
								gps.getLatitude(), gps.getLongitude());
						double speed = 0;
						//如果里程为0，就默认其没速度
						if(mileage != 0){
							long time = timeDiff(gps.getSendTime(),jtList.get(i).getSendTime());
							if(time == 0){
								Map<String,Object> m = new HashMap<String, Object>();
								m.put("data", gps);
								m.put("type", 2);
								resultList.add(m);
								return resultList;
							}
							speed = mileage/time * 3.6;
							if(speed <= 160){
								if(speed > 130 && !gps.isValid()){
									Map<String,Object> m = new HashMap<String, Object>();
									m.put("data", gps);
									m.put("type", 6);
									resultList.add(m);
									return resultList;
								}
								//如果两点之间速度小于160KM/H，那么将之前的可疑点视为正常点
								jtList.get(i).setPointType(0);
								isReturn = true;
							}else{
								//如果两点之间速度小大于160KM/H，那么将这该点视为可疑点
								if(!isReturn){
									gps.setPointType(1);
									//一般默认第一点为正常点
									if(i != 0){
										jtList.get(i).setPointType(1);
									}
								}
								//钝角三角形判断
								if(i == 0 && jtList.size() == 2){
									if(jtList.get(1).getPointType() == 0){
										jtList.get(0).setPointType(1);
									}
								}
							}
						}else{
							if(!gps.isValid()){
								jtList.get(i).getAddUnvalidCount().addAndGet(1);
								log.debug("未定位点已累计" + jtList.get(i).getAddUnvalidCount() + "点:"+gps.toString());
								Map<String,Object> m = new HashMap<String, Object>();
								m.put("data", gps);
								m.put("type", 6);
								resultList.add(m);
								return resultList;
							}else{
								//如果未定位的点的累计超过五个，则增加一个定位点比较的判断
								if(jtList.get(i).getAddUnvalidCount().intValue() >= 5){
									if(!gpsUnvalidMapTemplate.containsKey(vehicleId)){
										List<GpsInfo> jtListTemp = Collections.synchronizedList(new ArrayList<GpsInfo>());
										gps.setPointType(1);
										jtListTemp.add(gps);
										gpsUnvalidMapTemplate.put(vehicleId, jtListTemp);
										return null;
									}else{
										if(gpsUnvalidMapTemplate.get(vehicleId).size() < 5){
											List<GpsInfo> jtListTemp = gpsUnvalidMapTemplate.get(vehicleId);
											gps.setPointType(0);
											jtListTemp.add(gps);
											return null;
										}else{
											jtList.addAll(gpsUnvalidMapTemplate.get(vehicleId));
											gpsUnvalidMapTemplate.remove(vehicleId);
										}
									}
								}
							}
							if(jtList.size() > 1 && jtList.get(i).getPointType() == 1){
								if(!isReturn){
									gps.setPointType(1);
								}
							}else{
								jtList.get(i).setPointType(0);
								isReturn = true;
							}
						}
					}
					if(isReturn){
						//可返回时，如果未定位点集合还有存在数据，那就说明， 经纬度发生变化时，没有超过5个暂存点，则全部按照未定位点返回
						if(gpsUnvalidMapTemplate.containsKey(vehicleId)){
							List<GpsInfo> jtListTemp = gpsUnvalidMapTemplate.get(vehicleId);
							if(jtListTemp.size() > 0){
								for (int i = 0; i < jtListTemp.size(); i++) {
									Map<String,Object> m = new HashMap<String, Object>();
									m.put("data", jtListTemp.get(i));
									m.put("type", 6);
									resultList.add(m);
								}
							}
							gpsUnvalidMapTemplate.remove(vehicleId);
						}
						//返回除了当前点位外所有存在内存中的点
						for (int i = 0; i < jtList.size(); i++) {
							Map<String,Object> m = new HashMap<String, Object>();
							if(jtList.get(i).getPointType() == 0){
								m.put("data", jtList.get(i));
								m.put("type", 0);
								resultList.add(m);
							}else{
								m.put("data", jtList.get(i));
								m.put("type", 1);
								resultList.add(m);
							}
						}
						jtList.clear();
						gps.setPointType(0);
						jtList.add(gps);
						return resultList;
					}else{
						gps.setPointType(1);
						jtList.add(gps);
					}
				}
			}
		} catch (Exception e) {
			log.debug("GPS点位重算过滤异常",e);
		}
		return null;
	}

//	/**
//	 * 二次过滤
//	 * @param gpsList
//	 * @return
//	 */
//	public static List<GpsInfo> secondFilter(List<GpsInfo> gpsList){
//		for (int i = gpsList.size() - 1; i >= 0; i--) {
//			if(i == 0 || i == gpsList.size() - 1){
//				continue;
//			}
//			boolean firstSpeed = (double)getDistance(gpsList.get(i-1).getLatitude(),gpsList.get(i-1).getLatitude()
//					,gpsList.get(i).getLongitude(),gpsList.get(i).getLongitude())
//					/ timeDiff(gpsList.get(i).getSendTime(),gpsList.get(i-1).getSendTime()) * 3.6 > 160;
//			boolean lastSpeed = (double)getDistance(gpsList.get(i).getLatitude(),gpsList.get(i).getLatitude()
//					,gpsList.get(i+1).getLongitude(),gpsList.get(i+1).getLongitude())
//					/ timeDiff(gpsList.get(i+1).getSendTime(),gpsList.get(i).getSendTime()) * 3.6 > 160;
//			if (firstSpeed && lastSpeed){
//				gpsList.remove(i);
//			}
//		}
//		return gpsList;
//	}






}
