package net.fxft.ascsareavoice.ltmonitor.service.impl;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.service.IBaseService;
import org.springframework.stereotype.Service;

@Service("gpsRealDataService")
public class GPSRealDataService extends BaseService<Long, GPSRealData> implements IBaseService<Long, GPSRealData> {

    public GPSRealDataService() {
        super(GPSRealData.class);
    }
}
