package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.dto;

import com.ltmonitor.entity.GPSRealData;
import lombok.Data;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;

import java.util.Date;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2021/1/22 10:44
 */
@Data
public class QueueDto {
    private String simNo;
    private Date checkTime;
    private Takingphotosbytime takingphotosbytime;
    private GPSRealData gpsRealData;

}
