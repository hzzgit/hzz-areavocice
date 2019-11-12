package com.ltmonitor.service.impl;

import com.ltmonitor.service.MapFixService;
import com.ltmonitor.util.Constants;
import com.ltmonitor.vo.PointLatLng;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 调用百度API实现坐标加偏
 *
 * @author Administrator
 * 参见百度API坐标转换服务说明:http://developer.baidu.com/map/changeposition.htm
 * 返回码定义 1 内部错误 21 from非法 22 to非法 24 coords格式非法 25 coords个数非法，超过限制
 */
public class BaiduMapFixService {

    protected static Logger logger = LoggerFactory.getLogger(BaiduMapFixService.class);
    /**
     * gps设备坐标，基于wgs坐标系
     */
    public static int COORD_TYPE_GPS = 1;

    /**
     * 谷歌坐标，适用于所有的火星坐标系
     */
    public static int COORD_TYPE_GOOGLE = 3;
    /**
     * 百度API的注册KEY
     */
    private static String apiKey = "GT3YSZNuqHkwbGyAY4maPaVw";

    public BaiduMapFixService() {

        apiKey = "GT3YSZNuqHkwbGyAY4maPaVw";// 默认的API KEY
    }

    /**
     * 将百度坐标转换成原始GPS坐标
     *
     * @param lat
     * @param lng
     * @return
     */
    public static PointLatLng reverse(double lat, double lng) {

        PointLatLng p1 = bd_decrypt(lat, lng);//转换为火星坐标

        p1 = MapFixService.gcjToWgs(p1.lat, p1.lng);//转换为GPS坐标

        return p1;
    }

    static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;

    /**
     * 火星坐标转换成百度坐标
     *
     * @param gg_lat
     * @param gg_lon
     * @return
     */
    public static PointLatLng bd_encrypt(double gg_lat, double gg_lon) {
        double bd_lat;
        double bd_lon;
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * x_pi);
        bd_lon = z * Math.cos(theta) + 0.0065;
        bd_lat = z * Math.sin(theta) + 0.006;

        return new PointLatLng(bd_lon, bd_lat);
    }

    public static PointLatLng bd_decrypt(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new PointLatLng(gg_lon, gg_lat);
    }

    /**
     * @param lat
     * @param lng
     * @param corrdType 参见常量定义 1 代表gps 2代表火星
     * @return
     */
    public static PointLatLng fix(double lat, double lng, int corrdType) {
        if (lat <= 10 || lng <= 10)
            return new PointLatLng(0, 0);
        String strCoords = lng + "," + lat;
        try {

            String jsonstr = request(strCoords.toString(), corrdType);
            JSONObject jsonArr = JSONObject.fromObject(jsonstr);
            int status = jsonArr.getInt("status");
            if (status == 0) {
                JSONArray arr = jsonArr.getJSONArray("result");
                for (int m = 0; m < arr.size(); m++) {
                    JSONObject jo = arr.getJSONObject(m);
                    PointLatLng p = new PointLatLng(jo.getDouble("x"),
                            jo.getDouble("y"));
                    return p;
                }
            } else {
                logger.error("解析错误,错误码:" + status);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return new PointLatLng(0, 0);
    }

    public static List<PointLatLng> fix(List<PointLatLng> corrds, int corrdType) {

        List<PointLatLng> result = new ArrayList<PointLatLng>();
        if (corrds.size() == 0)
            return result;
        StringBuilder strCoords = new StringBuilder();
        for (PointLatLng p : corrds) {
            strCoords.append(p.lng).append(',').append(p.lat).append(';');
        }
        strCoords.deleteCharAt(strCoords.length() - 1);

        Date start = new Date();
        try {

            String jsonstr = request(strCoords.toString(), corrdType);
            JSONObject jsonArr = JSONObject.fromObject(jsonstr);
            int status = jsonArr.getInt("status");
            if (status == 0) {
                JSONArray arr = jsonArr.getJSONArray("result");

                for (int m = 0; m < arr.size(); m++) {
                    JSONObject jo = arr.getJSONObject(m);
                    PointLatLng p = new PointLatLng(jo.getDouble("x"),
                            jo.getDouble("y"));
                    result.add(p);
                }

            } else {
                logger.error("解析错误,错误码:" + status);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return result;
    }

    private static String request(String strCoords, int coordtype)
            throws Exception {
        URL url = new URL("http://api.map.baidu.com/geoconv/v1/?ak=" + apiKey
                + "&coords=" + strCoords + "&output=json&from=" + coordtype);

        URLConnection connection = null;
        OutputStreamWriter out = null;
        BufferedReader in = null;
        try {
            connection = url.openConnection();

            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            /**
             * 然后把连接设为输出模式。URLConnection通常作为输入来使用，比如下载一个Web页。
             * 通过把URLConnection设为输出，你可以把数据向你个Web页传送。下面是如何做：
             */
            connection.setDoOutput(true);
            out = new OutputStreamWriter(connection.getOutputStream(), "utf-8");
            // remember to clean up
            out.flush();
            out.close();
            // 一旦发送成功，用以下方法就可以得到服务器的回应：
            String res;
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(l_urlStream, "UTF-8"));
            StringBuilder sb = new StringBuilder("");
            while ((res = in.readLine()) != null) {
                sb.append(res.trim());
            }
            String jsonstr = sb.toString();
            return jsonstr;
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

        // BaiduMapFixService ls = new BaiduMapFixService();
        for (int m = 0; m < 100; m++) {
            double lat = 31.49 + m * 0.01;
            double lng = 119.17 + m * 0.01;

            PointLatLng p = MapFixService.fix(lat, lng, Constants.MAP_BAIDU);

            //PointLatLng p1 = bd_decrypt(p.lat, p.lng);//reverse(p.lat, p.lng);
            PointLatLng p1 = MapFixService.reverse(p.lat, p.lng, Constants.MAP_BAIDU);

            //p1 = MapFixService.gcjToWgs(p1.lat, p1.lng);

            double d = MapFixService.GetDistanceByMeter(p1.lng, p1.lat, lng,
                    lat);


            System.out.println((p1.lng - lng) + "," + (p1.lat - lat) + ",误差距离:" + d);
        }

        List<PointLatLng> coords = new ArrayList<PointLatLng>();
        // coords.add(p);
        coords.add(new PointLatLng(120.11, 34.11));

        coords = fix(coords, COORD_TYPE_GPS);
        for (PointLatLng pt : coords) {
            System.out.println(pt.lng + "," + pt.lat);
        }

    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
