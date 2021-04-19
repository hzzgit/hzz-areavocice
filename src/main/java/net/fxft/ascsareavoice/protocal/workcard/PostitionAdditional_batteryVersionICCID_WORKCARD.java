package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.MyBuffer;
import lombok.Data;
import net.fxft.common.util.ByteUtil;

/**
 * @author ：hzz
 * @description：扩展的附加消息ID，内容包含电池电量，版本信息，ICCID
 * @date ：2021/3/31 10:27
 */
@Data
public class PostitionAdditional_batteryVersionICCID_WORKCARD {

    /**
     * 扩展id 固定 0xe6 否则无此标识丢弃扩展数据
     */
    private int extensionId;


    /**
     * 位置数据扩展ID,这边根据文档是只有WIFI热点信息(0b:11)
     */
    private int paramId;


    /**
     * 扩展数据长度
     */
    private int length;


    /**
     * 备用电池电量
     * 百分比
     */
    private int batteryLevel;


    /**
     * 版本信息
     */
    private String versionInfo;

    /**
     * ICCID值
     */
    private String IccId;



    /**
     * 读取
     *
     * @param var1
     * @param var2
     * @return
     */
    public static PostitionAdditional_batteryVersionICCID_WORKCARD ReadFromBytes(int var1, byte[] var2) {
        if (var2 != null) {
            MyBuffer myBuffer = new MyBuffer(var2);
            PostitionAdditional_batteryVersionICCID_WORKCARD postitionAdditional_batteryVersionICCID_workcard = new PostitionAdditional_batteryVersionICCID_WORKCARD();
            int extensionId = myBuffer.getUnsignedByte();
            if(extensionId==230){
                while (myBuffer.hasRemain()){
                    int paramId = myBuffer.getUnsignedByte();
                    short length = myBuffer.getShort();
                    if(paramId==2){
                        //备用电池电量百分比
                        postitionAdditional_batteryVersionICCID_workcard.setBatteryLevel( myBuffer.getUnsignedByte());
                    }else if(paramId==7){
                        //版本信息
                        postitionAdditional_batteryVersionICCID_workcard.setVersionInfo(myBuffer.getString(length));
                    }else if(paramId==32){
                        //ICCID 值
                        postitionAdditional_batteryVersionICCID_workcard.setIccId(myBuffer.getBcdString(length));
                    }else {
                        //其他，未匹配则直接跳过
                        myBuffer.gets(length);
                    }
                }
                return postitionAdditional_batteryVersionICCID_workcard;
            }else{
                return null;
            }

        } else {
            return null;
        }

    }


    public static void main(String[] args) {
        String 版本信息="89860448041980280079 ";
        byte[] bytes = ByteUtil.hexStrToBytes(版本信息);
        MyBuffer myBuffer=new MyBuffer(bytes);
        String string = myBuffer.getBcdString(10);
        System.out.println(string);
    }

}
