package net.fxft.ascsareavoice.service;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.entity.VehicleData;

public interface IRealDataService {

    public GPSRealData getGpsRealData(String simNo);

    public GPSRealData get(String simNo);

    public VehicleData getVehicleData(String simNo);
}
