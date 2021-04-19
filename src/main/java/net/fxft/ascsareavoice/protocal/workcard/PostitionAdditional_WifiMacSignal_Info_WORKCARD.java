package net.fxft.ascsareavoice.protocal.workcard;

import lombok.Data;

/**
 * @author ：hzz
 * @description：WIFI定位获取的MAC地址及信号强度 ----WIFI热点信息
 * @date ：2021/3/31 10:27
 */
@Data
public class PostitionAdditional_WifiMacSignal_Info_WORKCARD {

    /**
     * mac地址
     */
    private String MacLocation;


    /**
     * 信号强度
     */
    private  int signalStrength;




}
