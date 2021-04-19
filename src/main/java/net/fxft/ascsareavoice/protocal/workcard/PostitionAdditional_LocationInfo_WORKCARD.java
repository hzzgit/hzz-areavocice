package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.MyBuffer;
import com.ltmonitor.util.StringUtil;

import java.math.BigDecimal;

/**
 * @author ：hzz
 * @description：电子工牌附加协议中的wifi定位和lbs定位
 * @date ：2021/3/31 10:27
 */
public class PostitionAdditional_LocationInfo_WORKCARD  {

    /**
     * 是否定位， 1：定位；0：未定位
     */
    private int isLocation;

    /**
     * 1：北纬；0：南纬
     */
    private int northofTheEquator;

    /**
     * 1：东经；0：西经
     */
    private int westLongitudeEastLongitude;

    /**
     * 纬度
     */
    private int latitude;
    /**
     * 经度
     */
    private int longitude;

    /**
     * 读取到位置信息
     * @param var1
     * @param var2
     * @return
     */
    public static PostitionAdditional_LocationInfo_WORKCARD ReadFromBytes(int var1, byte[] var2){
        if(var2!=null&&var2.length==9){
            PostitionAdditional_LocationInfo_WORKCARD postitionAdditional_locationInfo_workcard=new PostitionAdditional_LocationInfo_WORKCARD();
            MyBuffer myBuffer=new MyBuffer(var2);
            int status = myBuffer.getUnsignedByte();
            String strStatus = Integer.toBinaryString(status);
            strStatus = StringUtil.leftPad(strStatus, 8, '0');
            postitionAdditional_locationInfo_workcard.setIsLocation(Integer.parseInt(strStatus.substring(0, 1)));
            postitionAdditional_locationInfo_workcard.setNorthofTheEquator(Integer.parseInt(strStatus.substring(1, 2)));
            postitionAdditional_locationInfo_workcard.setWestLongitudeEastLongitude(Integer.parseInt(strStatus.substring(2, 3)));
            int latitude = myBuffer.getInt();
            int longitude = myBuffer.getInt();
            double[] doubles = PositionUtil.gcj02_To_Gps84(0.000001 * latitude, 0.000001 * longitude);
            postitionAdditional_locationInfo_workcard.setLatitude(keeppointto6(doubles[0]));
            postitionAdditional_locationInfo_workcard.setLongitude(keeppointto6(doubles[1]));
            return postitionAdditional_locationInfo_workcard;
        }
        return null;
    }

    /**
     * 保留6位小数点之后的整数
     * @return
     */
    private static int keeppointto6(double a){
        BigDecimal bigDecimal=new BigDecimal(a);
        double f1 = bigDecimal.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
        Double v = f1 * 1000000;
        int intValue = v.intValue();
        return intValue;
    }

    public int getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(int isLocation) {
        this.isLocation = isLocation;
    }

    public int getNorthofTheEquator() {
        return northofTheEquator;
    }

    public void setNorthofTheEquator(int northofTheEquator) {
        this.northofTheEquator = northofTheEquator;
    }

    public int getWestLongitudeEastLongitude() {
        return westLongitudeEastLongitude;
    }

    public void setWestLongitudeEastLongitude(int westLongitudeEastLongitude) {
        this.westLongitudeEastLongitude = westLongitudeEastLongitude;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }
}
