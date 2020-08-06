package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.service.impl.BaiduMapFixService;
import net.fxft.ascsareavoice.ltmonitor.util.Constants;
import net.fxft.ascsareavoice.ltmonitor.vo.PointLatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图偏移算法
 *
 * @author Administrator
 */
public class MapFixService {

    static double pi = 3.14159265358979324;

    //
    // Krasovsky 1940
    //
    // a = 6378245.0, 1/f = 298.3
    // b = a * (1 - f)
    // ee = (a^2 - b^2) / a^2;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;

    /**
     * 加偏，将原始坐标转换成百度地图坐标或火星坐标
     *
     * @param lat
     * @param lng
     * @param mapType
     * @return
     */
    public static PointLatLng fix(double lat, double lng, String mapType) {
        if (Constants.MAP_BAIDU.equals(mapType)) {
            PointLatLng pt = wgsToGcj(lat, lng);
            return BaiduMapFixService.bd_encrypt(pt.lat, pt.lng);

            //return BaiduMapFixService.fix(lat, lng,
            //BaiduMapFixService.COORD_TYPE_GPS);
        } else {
            return wgsToGcj(lat, lng);
        }
    }

    /**
     * 将百度或谷歌地图坐标还原成原生的gps坐标
     *
     * @param lat
     * @param lng
     * @param mapType
     * @return
     */
    public static PointLatLng reverse(double lat, double lng, String mapType) {
        if (Constants.MAP_GPS.equals(mapType)) {
            return new PointLatLng(lng, lat);
        } else if (Constants.MAP_BAIDU.equals(mapType)) {
            PointLatLng p1 = BaiduMapFixService.bd_decrypt(lat, lng);
            p1 = MapFixService.gcjToWgs(p1.lat, p1.lng);//转换为GPS坐标
            return p1;
        } else {
            return gcjToWgs(lat, lng);
        }
    }


    /**
     * 纠偏 火星坐标转原始坐标 采用逆推法
     *
     * @param wgLat
     * @param wgLon
     * @return
     */
    public static PointLatLng gcjToWgs(double wgLat, double wgLon) {
        PointLatLng pl = wgsToGcj(wgLat, wgLon);
        double offsetLat = pl.getLat() - wgLat;
        double offsetLng = pl.getLng() - wgLon;

        return new PointLatLng(wgLon - offsetLng, wgLat - offsetLat);
    }

    //

    /**
     * 加偏 wgs 坐标 转 火星坐标GCJ-02
     *
     * @param wgLat
     * @param wgLon
     * @return
     */
    // World Geodetic System ==> Mars Geodetic System
    private static PointLatLng wgsToGcj(double wgLat, double wgLon) {
        double mgLat = 0;
        double mgLon = 0;
        if (outOfChina(wgLat, wgLon)) {
            mgLat = wgLat;
            mgLon = wgLon;
            return new PointLatLng(mgLon, mgLat);
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;

        PointLatLng pl = new PointLatLng(mgLon, mgLat);
        return pl;

    }

    private static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * const double x_pi = 3.14159265358979324 * 3000.0 / 180.0; bd_encrypt 将
     * GCJ-02 坐标转换成 BD-09 坐标， bd_decrypt 反之。 void bd_encrypt(double gg_lat,
     * double gg_lon, double &bd_lat, double &bd_lon) { double x = gg_lon, y =
     * gg_lat; double z = sqrt(x * x + y * y) + 0.00002 * sin(y * x_pi); double
     * theta = atan2(y, x) + 0.000003 * cos(x * x_pi); bd_lon = z * cos(theta) +
     * 0.0065; bd_lat = z * sin(theta) + 0.006; }
     * <p>
     * void bd_decrypt(double bd_lat, double bd_lon, double &gg_lat, double
     * &gg_lon) { double x = bd_lon - 0.0065, y = bd_lat - 0.006; double z =
     * sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi); double theta = atan2(y, x)
     * - 0.000003 * cos(x * x_pi); gg_lon = z * cos(theta); gg_lat = z *
     * sin(theta); }
     */

    private static double EARTH_RADIUS = 6378.137;// 地球半径

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    // 计算两点之间的距离，以米为单位
    public static double GetDistanceByMeter(double lng1, double lat1,
                                            double lng2, double lat2) {
        if (lng1 == lng2 && lat1 == lat2)
            return 0;

        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000 * 1000) / 10000;
        return s;
    }


    // 点是否在矩形中
    public static Boolean IsInRect(double lng, double lat, double lng1,
                                   double lat1, double lng2, double lat2) {
        return lng >= Math.min(lng1, lng2) && lng <= Math.max(lng1, lng2)
                && lat >= Math.min(lat1, lat2) && lat <= Math.max(lat1, lat2);
    }

    // 点是否在圆形中
    public static Boolean IsInCircle(PointLatLng p, PointLatLng center,
                                     double radius) {
        double distance = MapFixService.GetDistanceByMeter(center.lng,
                center.lat, p.lng, p.lat);

        return distance <= radius;
    }

    // 点是否在多边形中
    public static Boolean IsInPolygon(PointLatLng p, List<PointLatLng> points) {
        int i, j = points.size() - 1;
        boolean oddNodes = false;

        double ret;
        for (i = 0; i < points.size(); i++) {
            if ((points.get(i).lat < p.lat && points.get(j).lat >= p.lat)
                    || (points.get(j).lat < p.lat && points.get(i).lat >= p.lat)) {
                ret = points.get(i).lng + (p.lat - points.get(i).lat)
                        / (points.get(j).lat - points.get(i).lat)
                        * (points.get(j).lng - points.get(i).lng);
                if (ret < p.lng) {
                    oddNodes = !oddNodes;
                }
            }
            j = i;
        }
        return oddNodes;
    }

    // / <summary>
    // / 计算叉乘 |P0P1| × |P0P2|
    // / </summary>
    // / <param name="p1"></param>
    // / <param name="p2"></param>
    // / <param name="p0"></param>
    // / <returns></returns>
    private static double MultiplyPoint(PointLatLng p1, PointLatLng p2,
                                        PointLatLng p0) {
        return ((p1.lng - p0.lng) * (p2.lat - p0.lat) - (p2.lng - p0.lng)
                * (p1.lat - p0.lat));
    }

    public static double ESP = 0.000001;// (1e-5)

    // / <summary>
    // / 判断点是否在线段上，考虑可控误差
    // / </summary>
    // / <param name="p1"></param>
    // / <param name="p2"></param>
    // / <param name="p0"></param>
    // / <param name="offset"></param>
    // / <returns></returns>
    public static boolean IsPointOnLine(PointLatLng p1, PointLatLng p2,
                                        PointLatLng p0, double offset) {
        double lat = p1.lat + (p2.lat - p1.lat) / 2;
        double lng = p1.lng + (p2.lng - p1.lng) / 2;

        double d = MapFixService.GetDistanceByMeter(p0.lng, p0.lat, lng, lat);

        double d1 = getDistanceFromLine(p1, p2, p0);

        double xy = Math.abs(MultiplyPoint(p1, p2, p0));

        //return isPointOnRouteSegment(p1, p2, p0,offset);
        if (xy >= ESP * offset)
            return false;
        System.out.println("高：" + d1 + ",与中点之间的距离:" + d + "，阀值:" + offset + ",叉积:" + xy / ESP);

        double y = (p0.lng - p1.lng) * (p0.lng - p2.lng);
        double x = (p0.lat - p1.lat) * (p0.lat - p2.lat);
        //System.out.println(y + "," + x);
        if (y > 0)
            return false;
        if (x > 0)
            return false;
        //if (x > 0.0001)
        //return false;
        return true;
    }

    /**
     * 判断点p0是否在点p1和p2之间直线上
     *
     * @param p1
     * @param p2
     * @param p0
     * @param bufferLen
     * @return
     */
    public static boolean isPointOnRouteSegment(PointLatLng p1, PointLatLng p2,
                                                PointLatLng p0, double bufferLen) {
        double a = GetDistanceByMeter(p0.lng, p0.lat, p1.lng, p1.lat);
        double b = GetDistanceByMeter(p0.lng, p0.lat, p2.lng, p2.lat);
        double c = GetDistanceByMeter(p2.lng, p2.lat, p1.lng, p1.lat);
        double t1 = b * b + c * c - a * a;//判断是三角形或者
        double t2 = a * a + c * c - b * b;
        if (t1 < 0 || t2 < 0){
            return false;
        }
        double p = (a + b + c) / 2;//这边是获取三角形的半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
        //在计算三角形面积的时候经常会用到一种公式，即S=√￣(q*(q-x)*(q-y)*(q-z))；这里的q就是半周长，而x,y,z分别为三边的边长，q=(x+y+z)/2。
        double h = 2 * s / c;//求三角形的高
        if (h > bufferLen)//高度如果大于线条宽度,那说明不在这个线段上
            return false;

        return true;
    }


    public static double getDistanceFromLine(PointLatLng p1, PointLatLng p2,
                                             PointLatLng p0) {
        double a = GetDistanceByMeter(p0.lng, p0.lat, p1.lng, p1.lat);
        double b = GetDistanceByMeter(p0.lng, p0.lat, p2.lng, p2.lat);
        double c = GetDistanceByMeter(p2.lng, p2.lat, p1.lng, p1.lat);
        double p = (a + b + c) / 2;
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));

        double h = 2 * s / c;
        return h;
    }


    public static void main(String[] args) {

        double d = MapFixService.GetDistanceByMeter(121.66592, 38.90836, 121.65552, 38.90826);

        PointLatLng p = new PointLatLng(121.66592, 38.90846);
        List<PointLatLng> points = new ArrayList<PointLatLng>();
        points.add(new PointLatLng(121.65552, 38.90886));
        points.add(new PointLatLng(121.65613, 38.90886));
        points.add(new PointLatLng(121.65613, 38.90843));
        points.add(new PointLatLng(121.65552, 38.90843));
        points.add(new PointLatLng(121.65552, 38.90886));

        Boolean isIn = MapFixService.IsInPolygon(p, points);

        double lat = 34.123456;
        double lng = 111.234567;
        double maxLatitude = lat + 0.00000899 * 90000;
        double maxLongitude = lng + 0.0000109 * 900000;
        double d1 = MapFixService.GetDistanceByMeter(lng, lat, maxLongitude, lat);
        double d2 = MapFixService.GetDistanceByMeter(lng, lat, lng, maxLatitude);
        //double d1 = MapFixService.GetDistanceByMeter(111.234567, 34.123456, 111.234578, 34.123456);

        //double d2 = MapFixService.GetDistanceByMeter(111.234567, 34.12345, 111.234567, 34.12346);

        PointLatLng p1 = new PointLatLng(118.421619, 24.756096);
        PointLatLng p2 = new PointLatLng(118.421399, 24.756647);
        PointLatLng mp = new PointLatLng(118.421399, 24.756647);
//		PointLatLng mp=new PointLatLng(118.42168259347842,24.756212193430752);
        MapFixService.isPointOnRouteSegment(p1, p2, mp, 10);
        /**
         for (int m = 0; m < 100; m++) {
         double wgLat = 32.178390 + m * 0.01;
         double wgLon = 120.173890 + m * 0.01;
         PointLatLng pl = MapFixService.wgsToGcj(wgLat, wgLon);

         PointLatLng pl2 = MapFixService.gcjToWgs(pl.getLat(), pl.getLng());

         double offsetLat = pl2.getLat() - pl.getLat();
         double offsetLng = pl2.getLng() - pl.getLng();

         double distance = GetDistanceByMeter(pl2.getLng(), pl2.getLat(),
         wgLon, wgLat);

         System.out.println(offsetLat + "," + offsetLng + ",distance:"
         + distance);
         }*/
    }

}
