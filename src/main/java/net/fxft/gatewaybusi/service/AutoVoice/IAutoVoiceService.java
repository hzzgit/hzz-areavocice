package net.fxft.gatewaybusi.service.AutoVoice;

import com.ltmonitor.entity.GPSRealData;

public interface IAutoVoiceService {

    //根据截取到的实时数据进行一系列的处理，主要处理逻辑
    public void autoVoiceMain(GPSRealData rd);
}
