package net.fxft.gatewaybusi.dataprocess;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.entity.VehicleData;
import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.JT_0201;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.util.DateUtil;
import net.fxft.gateway.util.SimNoUtil;
import net.fxft.gatewaybusi.service.AutoVoice.IAutoVoiceService;
import net.fxft.gatewaybusi.service.MapArea.AreaAlarmService;
import net.fxft.gatewaybusi.service.impl.RealDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class Process0200_RealData{

    private static final Logger log = LoggerFactory.getLogger(Process0200_RealData.class);

    private static long MaxAfterMillis = 20*3600*1000;

    @Autowired
    private RealDataService realDataService;

    //语音播报服务层
    @Autowired
    private IAutoVoiceService autoVoiceService;

    //注入围栏报警的类
    @Autowired
    private AreaAlarmService areaAlarmService;

    public void processData(String simNo, GPSRealData rd) throws Exception {


        autoVoiceService.autoVoiceMain(rd);
        areaAlarmService.addAreaqueue(rd);

    }



}
