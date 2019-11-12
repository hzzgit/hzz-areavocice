package com.ltmonitor.service.impl;

import com.ltmonitor.entity.SystemConfig;
import com.ltmonitor.service.ILocationService;
import com.ltmonitor.service.ISystemConfigService;
import com.ltmonitor.util.DateUtil;
import com.ltmonitor.util.StringUtil;
import net.fxft.common.util.BasicUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

/**
 * 调用百度API和高德地图实现地理位置的逆向编码查询 需要申请百度地图和高德地图开发key
 * 
 * @author Administrator
 *         参见百度API说明:http://developer.baidu.com/map/webservice-geocoding.htm
 *         返回码定义 0 正常 1 服务器内部错误 2 请求参数非法 3 权限校验失败 4 配额校验失败 5 ak不存在或者非法 101 服务禁用
 *         102 不通过白名单或者安全码不对 2xx 无权限 3xx 配额错误
 */
@Service("locationService")
public class LocationService implements ILocationService {

	protected Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * 百度API的注册KEY
	 */
	private String baiduApiKey;
	/**
	 * bd09ll（百度经纬度坐标）、gcj02ll（国测局经纬度坐标，火星坐标）、wgs84ll（ GPS经纬度）
	 */
	private String coordtype;
	/**
	 * 四维地图的key
	 */
	private String smartEarthKey = "zhtt";

	@Autowired
	private ISystemConfigService systemConfigService;

	private String aMapWebServiceKey = "a7c538c85b1d65c958a20a46761a1db1";

	private String mapType="mapType";


	public String getMapType() {
		return mapType;
	}


	/**
	 * 调用次数计数器
	 */
	private long times = 0;

	public LocationService() {

		baiduApiKey = "Vow719yQDau4cUmH8ZEUbeK0xu3s9KCe";// 默认的API KEY
		coordtype = "wgs84ll"; // 默认是GPS经纬度坐标
		// System.setProperty("sun.net.client.defaultConnectTimeout", "3000");
		// System.setProperty("sun.net.client.defaultReadTimeout", "3000");
	}

	/**
	 * 从数据库中读取key的配置
	 */
	@PostConstruct
    @Scheduled(fixedRate = 1800000)
	public void init() {
		try {
			SystemConfig sc = systemConfigService.getSystemConfig();
			if (sc != null) {
				if (sc.getBaiduKey() != null)
					this.baiduApiKey = sc.getBaiduWebServiceKey();
				if (sc.getSmartKey() != null)
					this.smartEarthKey = sc.getSmartKey();
				if(sc.getMapType()!=null){
					mapType=sc.getMapType();
				}
				this.aMapWebServiceKey = sc.getAmapWebServiceKey();
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
	}

	/**
	 * 根据经纬度坐标获取地理位置信息
	 * 偶数调用高德方法，奇数调用百度方法
	 */
	@Override
	public String getLocation(double lat, double lng, String remark) {
		if(times == Integer.MAX_VALUE)
			times = 0;
		String location = null;
		if (times++ % 2 == 0) {
			location = this.getLocationFromAMap(lat, lng);
			if (StringUtil.isNullOrEmpty(location)) {
				location = getLocationFromBaiduMap(lat, lng);
			}
		}else{
			location = this.getLocationFromBaiduMap(lat, lng);
			if (StringUtil.isNullOrEmpty(location)) {
				location = getLocationFromAMap(lat, lng);
			}
		}
		if (BasicUtil.isEmpty(location)) {
			log.debug("getLocation返回空！remark=" + remark + "; lat=" + lat + "; lng=" + lng);
		}
		return location;
	}

	/**
	 * 调用百度地图API，获取地理位置信息
	 */
	private String getLocationFromBaiduMap(double lat, double lng) {
		if (lat <= 10 || lng <= 10)
			return "无效坐标";
		Date start = new Date();
		URLConnection connection = null;
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			boolean displayPoi = true;
			int poi = displayPoi ? 1 : 0; // 是否显示周边100米内的POI

			URL url = new URL("http://api.map.baidu.com/geocoder/v2/?ak="
					+ baiduApiKey + "&location=" + lat + "," + lng
					+ "&output=json&pois=" + poi + "&coordtype=" + coordtype);

			// System.out.println(url.toString());
			connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

			// httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
			// httpURLConnection.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");

			int code = httpURLConnection.getResponseCode();
			if (code == 450) {
				return "450错误";
			}
			/**
			 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
			 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
			 */
			// connection.setDoOutput(true);
			// out = new OutputStreamWriter(connection.getOutputStream(),
			// "utf-8");
			// remember to clean up
			// out.flush();
			// out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String res;
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String responseString = sb.toString();
			// System.out.println(responseString);

			Date end = new Date();

			// log.error("定位信息耗时:" + DateUtil.getSeconds(start, end));
			JSONObject jsonArr = JSONObject.fromObject(responseString);
			String status = jsonArr.getString("status");
			if ("0".equals(status)) {
				JSONObject result = jsonArr.getJSONObject("result");
				StringBuilder location = new StringBuilder();
				String address = result.getString("formatted_address");
				location.append(address);
				JSONArray jr = result.getJSONArray("pois");

				if (jr.isEmpty() == false) {
					JSONObject j = jr.getJSONObject(0);

					location.append(j.get("name")).append(j.get("direction"))
							.append(j.getInt("distance")).append("米");
				}
				return location.toString();
			} else {
				// String info = jsonArr.getString("info");
				//log.error("解析错误,错误码:" + status + ",文本:" + responseString);
			}

			return "";// "解析错误,错误码:" + status;
		} catch (Exception ex) {
			log.info("解析错误,发生异常:" + ex.getMessage());
			return "";
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {

			}

			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {

			}

		}

	}

	/**
	 * 调用高德地图 WebService Key获得地理位置信息
	 * 
	 * @param lat
	 * @param lng
	 * @return
	 */
	private String getLocationFromAMap(double lat, double lng) {
		if (lat <= 10 || lng <= 10)
			return "无效坐标";
		Date start = new Date();
		URLConnection connection = null;
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			/**
			 * URL url = new URL(
			 * "http://f.gis.cttic.cn:9000/SE_RGC2?st=Rgc2&point=" + lng + "," +
			 * lat + "&type=11&output=json&uid="+this.smartEarthKey);
			 */
			//
			URL url = new URL("http://restapi.amap.com/v3/geocode/regeo?key="
					+ this.aMapWebServiceKey + "&location=" + lng + "," + lat
					+ "&output=json&extensions=all");

			// System.out.println(url.toString());
			connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			/**
			 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
			 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
			 */
			// connection.setDoOutput(true);
			// out = new OutputStreamWriter(connection.getOutputStream(),
			// "utf-8");
			// remember to clean up
			// out.flush();
			// out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String res;
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String responseString = sb.toString();
			// System.out.println(responseString);
			StringBuilder location = new StringBuilder();
			JSONObject jsonArr = JSONObject.fromObject(responseString);
			if ("1".equals(jsonArr.getString("status"))) {
				JSONObject result = jsonArr.getJSONObject("regeocode");
				location.append(result.getString("formatted_address"));
				// JSONObject ac = result.getJSONObject("addressComponent");
				JSONArray jr = result.getJSONArray("pois");

				if (jr.isEmpty() == false) {
					JSONObject poi = jr.getJSONObject(0);

					location.append(poi.get("name"))
							.append(poi.get("direction"))
							.append(poi.getInt("distance")).append("米");
				}
			} else {
				String info = jsonArr.getString("info");
				log.error("解析错误,错误码:" + info + ",文本:" + responseString);
			}

			return location.toString();// "解析错误,错误码:" + status;
		} catch (Exception ex) {
			log.info("解析错误,发生异常:" + ex.getMessage());
			return "";
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {

			}

			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {

			}

		}

	}

	private String getSmartLocation(double lat, double lng) {
		if (lat <= 10 || lng <= 10)
			return "无效坐标";
		Date start = new Date();
		URLConnection connection = null;
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			/**
			 * URL url = new URL(
			 * "http://f.gis.cttic.cn:9000/SE_RGC2?st=Rgc2&point=" + lng + "," +
			 * lat + "&type=11&output=json&uid="+this.smartEarthKey);
			 */

			URL url = new URL(
					"http://a.map.icttic.cn:81/SE_RGC2?st=Rgc2&point=" + lng
							+ "," + lat + "&type=11&output=json&uid="
							+ this.smartEarthKey);

			// System.out.println(url.toString());
			connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			/**
			 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
			 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
			 */
			// connection.setDoOutput(true);
			// out = new OutputStreamWriter(connection.getOutputStream(),
			// "utf-8");
			// remember to clean up
			// out.flush();
			// out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String res;
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String responseString = sb.toString();
			// System.out.println(responseString);
			StringBuilder location = new StringBuilder();
			JSONObject jsonArr = JSONObject.fromObject(responseString);
			if ("ok".equals(jsonArr.getString("status"))) {
				JSONObject result = jsonArr.getJSONObject("result");
				if (result.has("district_text"))
					location.append(result.getString("district_text"));
				if (result.has("address"))
					location.append(result.getString("address"));
				if (result.has("road_address"))
					location.append(",").append(
							result.getString("road_address"));

			}

			// log.error("解析错误,错误码:" + status+",文本:"+responseString);

			return location.toString();// "解析错误,错误码:" + status;
		} catch (Exception ex) {
			log.info("解析错误,发生异常:" + ex.getMessage());
			return "";
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {

			}

			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {

			}

		}

	}

	public String getBaiduRoute(double lat, double lng, double lat1, double lng1) {
		if (lat <= 10 || lng <= 10)
			return "无效坐标";
		Date start = new Date();
		URLConnection connection = null;
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			// http://api.map.baidu.com/direction/v1?mode=driving&origin=清华大学&destination=北京大学
			// +""&origin_region=北京&destination_region=北京&output=json&ak=E4805d16520de693a3fe707cdc962045
			URL url = new URL(
					"http://api.map.baidu.com/direction/v1?mode=driving&origin="
							+ lat + "," + lng + "&destination=" + lat1 + ","
							+ lng1 + "&origin_region=大连&destination_region=盘锦"
							+ "&ak=" + baiduApiKey
							+ "&output=json&coordtype=gcj02");

			// System.out.println(url.toString());
			connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

			int code = httpURLConnection.getResponseCode();
			if (code == 450) {
				return "450错误";
			}
			/**
			 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
			 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
			 */
			// connection.setDoOutput(true);
			// out = new OutputStreamWriter(connection.getOutputStream(),
			// "utf-8");
			// remember to clean up
			// out.flush();
			// out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String res;
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String responseString = sb.toString();
			StringBuilder location = new StringBuilder();
			JSONObject jsonArr = JSONObject.fromObject(responseString);
			System.out.println("begin");
			File file = new File("E:\\gpsdata.txt");
			// String str = "123";
			ArrayList<String> ls = new ArrayList<String>();
			FileWriter writer = new FileWriter(file);

			if ("0".equals(jsonArr.getString("status"))) {
				JSONObject result = jsonArr.getJSONObject("result");

				JSONArray js = result.getJSONArray("routes");
				int total = 0;
				for (int m = 0; m < js.size(); m++) {
					JSONObject jo = js.getJSONObject(m);
					JSONArray steps = jo.getJSONArray("steps");
					for (int k = 0; k < steps.size(); k++) {
						JSONObject area = steps.getJSONObject(k);
						String path = area.getString("path");

						JSONObject org = area
								.getJSONObject("stepOriginLocation");
						JSONObject dest = area
								.getJSONObject("stepOriginLocation");

						// System.out.println(org.getDouble("lng")+","+
						// org.getDouble("lat"));
						// System.out.println(dest.getDouble("lng")+","+
						// dest.getDouble("lat"));
						String[] ps = path.split(";");
						// double x = jo.getDouble("lng");
						// double y = jo.getDouble("lat");
						// System.out.println(locationName+","+ x + "," + y);
						if (ps.length == 0) {
							System.out.println("no path--------------------");
						}
						for (String p : ps) {
							System.out.println(p);
							writer.write(p);
							ls.add(p);
							writer.write("\r\n");
							total++;
						}

						// if(total>1000)
						// return null;
						total++;
					}
				}
				System.out.println("共" + total);

				for (int m = 0; m < ls.size(); m++) {
					writer.write(ls.get(ls.size() - m - 1));
					writer.write("\r\n");
				}
				writer.flush();
				writer.close();

			}

			// System.out.println(responseString);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			System.out.println(ex.getStackTrace());
		}

		return null;
	}

	public String getRoute(double lat, double lng, double lat1, double lng1) {
		if (lat <= 10 || lng <= 10)
			return "无效坐标";
		Date start = new Date();
		URLConnection connection = null;
		OutputStreamWriter out = null;
		BufferedReader in = null;
		try {
			/**
			 * URL url = new URL(
			 * "http://f.gis.cttic.cn:9000/SE_RGC2?st=Rgc2&point=" + lng + "," +
			 * lat + "&type=11&output=json&uid="+this.smartEarthKey);
			 */
			URL url = new URL("http://a.map.icttic.cn:81/SE_RC?st=RC&start="
					+ lng + "," + lat + "&end=" + lng1 + "," + lat1
					+ "&type=2&output=json&uid=" + this.smartEarthKey);

			// System.out.println(url.toString());
			connection = url.openConnection();
			connection.setConnectTimeout(3000);
			connection.setReadTimeout(3000);
			/**
			 * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
			 * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
			 */
			// connection.setDoOutput(true);
			// out = new OutputStreamWriter(connection.getOutputStream(),
			// "utf-8");
			// remember to clean up
			// out.flush();
			// out.close();
			// 一旦发送成功，用以下方法就可以得到服务器的回应：
			String res;
			InputStream l_urlStream;
			l_urlStream = connection.getInputStream();
			in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
			StringBuilder sb = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			String responseString = sb.toString();
			// System.out.println(responseString);
			StringBuilder location = new StringBuilder();
			JSONObject jsonArr = JSONObject.fromObject(responseString);
			if ("ok".equals(jsonArr.getString("status"))) {
				JSONObject result = jsonArr.getJSONObject("result");

				JSONArray js = result.getJSONArray("events");

				for (int m = 0; m < js.size(); m++) {
					JSONObject jo = js.getJSONObject(m);
					String locationName = jo.getString("roadname");
					double x = jo.getDouble("lng");
					double y = jo.getDouble("lat");
					// System.out.println(locationName+","+ x + "," + y);
					System.out.println(x + "," + y);
				}

			}

			// log.error("解析错误,错误码:" + status+",文本:"+responseString);

			return location.toString();// "解析错误,错误码:" + status;
		} catch (Exception ex) {
			log.info("解析错误,发生异常:" + ex.getMessage());
			return "";
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {

			}

			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {

			}

		}

	}

	public static void main(String[] args) throws IOException {

		LocationService ls = new LocationService();

		// ls.getBaiduRoute(38.9265311, 121.601282, 41.157719, 122.070121);
		// return;

		double lat = 34.91845;
		double lng = 116.42028;
		boolean displayPoi = true;
		Date start = new Date();
		for (int m = 0; m < 1; m++) {
			String location1 = ls.getLocationFromBaiduMap(lat, lng);
			System.out.println("baidu location:" + location1);

		}
		Date end = new Date();
		double seconds = DateUtil.getSeconds(start, end);
		System.out.println("耗时:" + seconds);
		start = new Date();
		for (int m = 0; m < 1; m++) {
			String location = ls.getLocationFromAMap(lat, lng);
			System.out.println("AMap location:" + location);
		}
		end = new Date();
		seconds = DateUtil.getSeconds(start, end);
		System.out.println("耗时:" + seconds);

		// System.out.println(location);
	}

	public String getCoordtype() {
		return coordtype;
	}

	public void setCoordtype(String coordtype) {
		this.coordtype = coordtype;
	}

	public String getBaiduApiKey() {
		return baiduApiKey;
	}

	public void setBaiduApiKey(String apiKey) {
		this.baiduApiKey = apiKey;
	}

	public String getSmartEarthKey() {
		return smartEarthKey;
	}

	public void setSmartEarthKey(String smartEarthKey) {
		this.smartEarthKey = smartEarthKey;
	}

}
