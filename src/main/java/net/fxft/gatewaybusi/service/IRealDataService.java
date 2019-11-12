package net.fxft.gatewaybusi.service;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.entity.VehicleData;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.gateway.util.KryoUtil;
import org.springframework.beans.factory.annotation.Autowired;

public interface IRealDataService {

    public GPSRealData getGpsRealData(String simNo);

    public GPSRealData get(String simNo);

    public VehicleData getVehicleData(String simNo);
}
