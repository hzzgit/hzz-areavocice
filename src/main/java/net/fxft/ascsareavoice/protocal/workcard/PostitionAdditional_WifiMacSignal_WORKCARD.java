package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.MyBuffer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：hzz
 * @description：WIFI定位获取的MAC地址及信号强度
 * @date ：2021/3/31 10:27
 */
@Data
public class PostitionAdditional_WifiMacSignal_WORKCARD {

    /**
     * 扩展id 固定 0xe6 否则无此标识丢弃扩展数据
     */
    private int extensionId;


    /**
     * 参数扩展 ID,这边根据文档是只有WIFI热点信息(0b:11)
     */
    private int paramId;


    /**
     * WIFI热点信息
     */
    private List<PostitionAdditional_WifiMacSignal_Info_WORKCARD> postitionAdditional_wifiMacSignal_info_workcards;


    /**
     * 读取到位置信息
     *
     * @param var1
     * @param var2
     * @return
     */
    public static PostitionAdditional_WifiMacSignal_WORKCARD ReadFromBytes(int var1, byte[] var2) {
        if (var2 != null) {
            MyBuffer myBuffer = new MyBuffer(var2);
            PostitionAdditional_WifiMacSignal_WORKCARD postitionAdditional_wifiMacSignal_workcard = new PostitionAdditional_WifiMacSignal_WORKCARD();
            postitionAdditional_wifiMacSignal_workcard.setExtensionId(myBuffer.getUnsignedByte());
            if (postitionAdditional_wifiMacSignal_workcard.getExtensionId() == 230) {
                postitionAdditional_wifiMacSignal_workcard.setParamId(myBuffer.getUnsignedByte());
                if (postitionAdditional_wifiMacSignal_workcard.getParamId() == 11) {
                    //wifi热点数据扩展
                    //wifi热点个数
                    int hotCo = myBuffer.getUnsignedByte();
                    if (hotCo > 0) {
                        postitionAdditional_wifiMacSignal_workcard.setPostitionAdditional_wifiMacSignal_info_workcards(new ArrayList<>());
                    }
                    for (int i = 0; i < hotCo; i++) {
                        //获取到wifi热点信息
                        PostitionAdditional_WifiMacSignal_Info_WORKCARD postitionAdditional_wifiMacSignal_info_workcard = new PostitionAdditional_WifiMacSignal_Info_WORKCARD();
                        postitionAdditional_wifiMacSignal_info_workcard.setMacLocation(myBuffer.getBcdString(6));
                        postitionAdditional_wifiMacSignal_info_workcard.setSignalStrength(myBuffer.getUnsignedByte());
                        postitionAdditional_wifiMacSignal_workcard.getPostitionAdditional_wifiMacSignal_info_workcards().add(postitionAdditional_wifiMacSignal_info_workcard);
                    }
                }
                return postitionAdditional_wifiMacSignal_workcard;
            } else {
                return null;
            }

        }
        return null;
    }


}
