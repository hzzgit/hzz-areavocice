package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.IMessageBody;
import com.ltmonitor.jt808.protocol.MyBuffer;
import com.ltmonitor.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.fxft.common.util.ByteUtil;

/**
 * 0204下班打卡信息
 */
@Slf4j
public class JT_0204_WORKCARD implements IMessageBody {

    protected int alarmFlag;
    protected int status;
    protected int latitude;
    protected int longitude;
    protected short altitude;
    protected short speed;
    protected short course;
    protected String time;
    protected boolean valid;
    protected String strStatus;
    protected String strWarn;

    public int getAlarmFlag() {
        return alarmFlag;
    }

    public void setAlarmFlag(int alarmFlag) {
        this.alarmFlag = alarmFlag;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public short getAltitude() {
        return altitude;
    }

    public void setAltitude(short altitude) {
        this.altitude = altitude;
    }

    public short getSpeed() {
        return speed;
    }

    public void setSpeed(short speed) {
        this.speed = speed;
    }

    public short getCourse() {
        return course;
    }

    public void setCourse(short course) {
        this.course = course;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getStrStatus() {
        return strStatus;
    }

    public void setStrStatus(String strStatus) {
        this.strStatus = strStatus;
    }

    public String getStrWarn() {
        return strWarn;
    }

    public void setStrWarn(String strWarn) {
        this.strWarn = strWarn;
    }

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

    @Override
    public void ReadFromBytes(int i, byte[] bytes) {
        MyBuffer buff = new MyBuffer(bytes);
        this.setAlarmFlag(buff.getInt());
        this.setStatus(buff.getInt());
        this.setLatitude(buff.getInt());
        this.setLongitude(buff.getInt());
        this.setAltitude(buff.getShort());
        this.setSpeed(buff.getShort());
        this.course = buff.getShort();
        this.time = buff.getBcdDateString();
        int pos = 28;

        int additionalLength;
        try {
            for(; buff.hasRemain(); pos = pos + 2 + additionalLength) {
                int additionalId = buff.getUnsignedByte();
                additionalLength = buff.getUnsignedByte();
                if (additionalLength > 0) {
                    int remain = buff.remain();
                    if (remain < additionalLength) {
                        log.debug("附加协议长度异常！id=" + additionalId + ", 附加长度:" + additionalLength + "; remain=" + remain + "; bytes=" + ByteUtil.byteToHexStr(bytes));
                        break;
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
                        int length = myBuffer2.getUnsignedByte();
                        IMEI= myBuffer2.getBcdString(length);
                    }

                }
            }
        } catch (Exception var12) {
            log.error("解析PositionAdditional_WORKCARD电子工牌协议出错，但不影响0200基本数据！bytes=" + ByteUtil.byteToHexStr(bytes), var12);
        }

        this.strWarn = Integer.toBinaryString(this.getAlarmFlag());
        this.strWarn = StringUtil.leftPad(this.strWarn, 32, '0');
        this.strStatus = Integer.toBinaryString(this.getStatus());
        this.strStatus = StringUtil.leftPad(this.strStatus, 32, '0');
        this.setValid(this.getStrStatus().substring(30, 31).equals("1"));

    }



    @Override
    public int getMsgType() {
        return 516;
    }

    @Override
    public byte[] WriteToBytes(int i) {
        return new byte[0];
    }



    @Override
    public boolean isV2019(int version) {
        return false;
    }
}
