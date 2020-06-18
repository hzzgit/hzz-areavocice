package net.fxft.ascsareavoice.dataprocess;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.service.AutoVoice.IAutoVoiceService;
import net.fxft.ascsareavoice.service.MapArea.AreaAlarmService;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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