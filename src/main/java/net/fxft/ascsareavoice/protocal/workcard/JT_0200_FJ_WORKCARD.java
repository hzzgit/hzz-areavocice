package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.MyBuffer;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.jt808.protocol.T808MessageHeader;
import lombok.extern.slf4j.Slf4j;
import net.fxft.common.util.ByteUtil;

/**
 * 0200附加协议解析之电子工牌
 */
@Slf4j

public class JT_0200_FJ_WORKCARD {

    /**
     * 通过wifi解析出来的位置信息
     */
    private PostitionAdditional_LocationInfo_WORKCARD wifiLocationInfo;


    /**
     * 通过LBS解析出来的位置信息
     */
    private PostitionAdditional_LocationInfo_WORKCARD lbsLocationInfo;

    /**
     * 解析到wifi热点信息
     */
    private PostitionAdditional_WifiMacSignal_WORKCARD postitionAdditional_wifiMacSignal_workcard;

    /**
     * 备用电池电量和版本信息和iccid
     */
    private PostitionAdditional_batteryVersionICCID_WORKCARD postitionAdditional_batteryVersionICCID_workcard;


    /**
     * 解析IMEI
     */
    private String IMEI;

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    private T808MessageHeader header = new T808MessageHeader();



    private byte[] UnEscape(byte[] data) {
        MyBuffer buff = new MyBuffer();

        for (int i = 0; i < data.length; ++i) {
            if (data[i] == 125) {
                if (data[i + 1] == 1) {
                    buff.put((byte) 125);
                    ++i;
                } else if (data[i + 1] == 2) {
                    buff.put((byte) 126);
                    ++i;
                }
            } else {
                buff.put(data[i]);
            }
        }

        byte[] a = buff.array();
        return a;
    }

    /**
     * 写入到通用的808 中0200定位协议
     */
    public void WriteJt0200(JT_0200 jt_0200){
        String status =String.valueOf(jt_0200.getStrStatus()) ;//获取到状态码
        char[] ch = status.toCharArray();
        //是否定位，0未定位，1定位
        int isLocation=0;
        if (ch.length == 32) {
            int m = 31;
            isLocation= ch[m - 1] - 48;
        }
        if(isLocation==0){
            //如果是未定位，那么就要判断是否有wifi定位或者lsb定位
            if(wifiLocationInfo!=null&&wifiLocationInfo.getIsLocation()==1){
                //如果wifi定位存在
                changeJt0200Status(jt_0200,wifiLocationInfo);

            }else if(lbsLocationInfo!=null&&lbsLocationInfo.getIsLocation()==1){
                //否则判断lbs定位是否存在
                changeJt0200Status(jt_0200,lbsLocationInfo);
            }
        }
    }

    /**
     * 如果判断完确实是需要使用wifi或者lbs定位
     * @param jt_0200
     * @param postitionAdditional_locationInfo_workcard
     */
    private void changeJt0200Status(JT_0200 jt_0200,PostitionAdditional_LocationInfo_WORKCARD postitionAdditional_locationInfo_workcard){
        int isLocation = postitionAdditional_locationInfo_workcard.getIsLocation();
        int northofTheEquator = postitionAdditional_locationInfo_workcard.getNorthofTheEquator();
        int westLongitudeEastLongitude = postitionAdditional_locationInfo_workcard.getWestLongitudeEastLongitude();
        int latitude = postitionAdditional_locationInfo_workcard.getLatitude();
        int longitude = postitionAdditional_locationInfo_workcard.getLongitude();
        jt_0200.setLatitude(latitude);
        jt_0200.setLongitude(longitude);

        if(northofTheEquator==0){
            northofTheEquator=1;
        }else if(northofTheEquator==1){
            northofTheEquator=0;
        }
        if(westLongitudeEastLongitude==0){
            westLongitudeEastLongitude=1;
        }else if(westLongitudeEastLongitude==1){
            westLongitudeEastLongitude=0;
        }
        StringBuilder strStatus =new StringBuilder(jt_0200.getStrStatus()) ;
        strStatus.replace(30,31, String.valueOf(isLocation));
        strStatus.replace(29,30, String.valueOf(northofTheEquator));
        strStatus.replace(28,29, String.valueOf(westLongitudeEastLongitude));
        jt_0200.setStrStatus(strStatus.toString());
        int status = Integer.parseInt(strStatus.toString(), 2);
        jt_0200.setStatus(status);
        jt_0200.setIsValid(isLocation==1?true :false);
    }




    public void ReadFromBytes(byte[] messageBytes) {
        byte[] validMessageBytes = this.UnEscape(messageBytes);
        try {
            int start = this.header.ReadFromBytes(validMessageBytes, 1) + 1;
            if (this.header.getMessageSize() > 0) {
                byte[] sourceData = new byte[this.header.getMessageSize() - 28];
                System.arraycopy(validMessageBytes, start + 28, sourceData, 0, sourceData.length);
                MyBuffer buff = new MyBuffer(sourceData);
                while (buff.hasRemain()) {
                    int additionalId = buff.getUnsignedByte();
                    int additionalLength = buff.getUnsignedByte();
                    if (additionalLength > 0) {
                        int remain = buff.remain();
                        if (remain < additionalLength) {
                            log.debug("附加协议长度异常！id=" + additionalId + ", 附加长度:" + additionalLength + "; remain=" + remain + "; bytes=" + ByteUtil.byteToHexStr(messageBytes));
                            break;
                        }
                    }
                    byte[] additionalBytes = buff.gets(additionalLength);

                    if(additionalId==241){//wifi的定位信息
                        wifiLocationInfo = PostitionAdditional_LocationInfo_WORKCARD.ReadFromBytes(0,additionalBytes);
                    }
                    if(additionalId==242){//lbs的定位信息
                        lbsLocationInfo = PostitionAdditional_LocationInfo_WORKCARD.ReadFromBytes(0,additionalBytes);
                    }

                    if(additionalId==240){
                        //WIFI定位获取的MAC地址及信号强度
                        postitionAdditional_wifiMacSignal_workcard=PostitionAdditional_WifiMacSignal_WORKCARD.ReadFromBytes(0,additionalBytes);
                    }
                    if(additionalId==254){
                        //扩展的附加消息ID，内容包含电池电量，版本信息，ICCID
                        postitionAdditional_batteryVersionICCID_workcard=PostitionAdditional_batteryVersionICCID_WORKCARD.ReadFromBytes(0,additionalBytes);
                    }

                    if(additionalId==21){
                        //IMEI号
                        MyBuffer myBuffer2=new MyBuffer(additionalBytes);
                       IMEI= myBuffer2.getBcdString(additionalLength);
                    }
                }
            }
        } catch (Exception var7) {
            log.error("解析附加协议异常", var7);
        }


    }


    public PostitionAdditional_LocationInfo_WORKCARD getWifiLocationInfo() {
        return wifiLocationInfo;
    }

    public void setWifiLocationInfo(PostitionAdditional_LocationInfo_WORKCARD wifiLocationInfo) {
        this.wifiLocationInfo = wifiLocationInfo;
    }

    public PostitionAdditional_LocationInfo_WORKCARD getLbsLocationInfo() {
        return lbsLocationInfo;
    }

    public void setLbsLocationInfo(PostitionAdditional_LocationInfo_WORKCARD lbsLocationInfo) {
        this.lbsLocationInfo = lbsLocationInfo;
    }

    public PostitionAdditional_WifiMacSignal_WORKCARD getPostitionAdditional_wifiMacSignal_workcard() {
        return postitionAdditional_wifiMacSignal_workcard;
    }

    public void setPostitionAdditional_wifiMacSignal_workcard(PostitionAdditional_WifiMacSignal_WORKCARD postitionAdditional_wifiMacSignal_workcard) {
        this.postitionAdditional_wifiMacSignal_workcard = postitionAdditional_wifiMacSignal_workcard;
    }

    public PostitionAdditional_batteryVersionICCID_WORKCARD getPostitionAdditional_batteryVersionICCID_workcard() {
        return postitionAdditional_batteryVersionICCID_workcard;
    }

    public void setPostitionAdditional_batteryVersionICCID_workcard(PostitionAdditional_batteryVersionICCID_WORKCARD postitionAdditional_batteryVersionICCID_workcard) {
        this.postitionAdditional_batteryVersionICCID_workcard = postitionAdditional_batteryVersionICCID_workcard;
    }

    public static void main(String[] args) throws Exception {
        String bytes="7E0200007305670465977500D200000001000C000C018DF8DE071BE7180000000000002104161535290104001F9C23150F38363735363730343635393737353330011FF109A2018DEBA4071BF998FE30E60200011B07001B4D53333330572D56312E30323B513A33313B4D323A34302C33303020000A898604A2192171148499B77E\n";
        byte[] bytes1 = ByteUtil.hexStrToBytes(bytes);
        JT_0200_FJ_WORKCARD jt_0200_fj_workcard=new JT_0200_FJ_WORKCARD();
        jt_0200_fj_workcard.ReadFromBytes(bytes1);

        String by="7E0200007305670465977500D200000001000C000C018DF8DE071BE7180000000000002104161535290104001F9C23150F38363735363730343635393737353330011FF109A2018DEBA4071BF998FE30E60200011B07001B4D53333330572D56312E30323B513A33313B4D323A34302C33303020000A898604A2192171148499B77E\n";
        byte[] bytes2 = ByteUtil.hexStrToBytes(by);

        T808Message t808Message=new T808Message();
        t808Message.ReadFromBytes(bytes2);
        JT_0200 jt_0200 = (JT_0200) t808Message.getMessageContents();
        jt_0200_fj_workcard.WriteJt0200(jt_0200);
        System.out.println(1);

        System.out.println(1);



    }
}
