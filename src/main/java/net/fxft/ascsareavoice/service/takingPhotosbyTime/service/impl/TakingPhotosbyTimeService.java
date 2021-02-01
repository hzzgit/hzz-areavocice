package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.TakephotoDao;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoBySimNoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoByVehicleIdDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.RedisIsPhotoCache;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.dto.IsPhotoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author ：hzz
 * @description：定时拍照主线逻辑类
 * @date ：2021/1/21 17:15
 */
@Service
@Slf4j
public class TakingPhotosbyTimeService {


    @Autowired
    private TakephotoDao takephotoDao;

    @Autowired
    private RedisIsPhotoCache redisIsPhotoCache;

    @Autowired
    private RealDataService realDataService;
    @Autowired
    private TakingPhotosbyTimeQueue takingPhotosbyTimeQueue;
    /**
     * 是否开启定时拍照功能
     */
    @Value("${istakingphoto:false}")
    private boolean istakingphoto;

    /**
     * 启动定时拍照，一分钟检测一次
     */
    @PostConstruct
    private void init() {
        if (istakingphoto) {
            new Thread(() -> {
                while (true) {
                    try {
                       begin();
                    } catch (Exception e) {
                        log.error("定时拍照：主线程异常", e);
                    }

                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            //这边检测无用的配置项，并移除
            new Thread(() -> {
                while (true) {
                    remove();
                    try {
                        Thread.sleep(1800000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }


    /**
     * 移除无用的redis存储信息
     */
    private void remove() {
        try {
            List<TakingPhotoByVehicleIdDto> takingPhotoByVehicleIdDtos = takephotoDao.searchRemoveTakingVehicle();
            for (TakingPhotoByVehicleIdDto takingPhotoByVehicleIdDto : takingPhotoByVehicleIdDtos) {
                redisIsPhotoCache.remove(takingPhotoByVehicleIdDto.getVehicleId(), takingPhotoByVehicleIdDto.getId());
            }
        } catch (Exception e) {
            log.error("定时拍照:移除关闭或者超时的设备异常", e);
        }

    }

    private void begin() throws Exception {
        long s = System.currentTimeMillis();   //获取开始时间
        Map<Integer, Takingphotosbytime> integerTakingphotosbytimeMap = takephotoDao.searchTakingPhotoConfig();
        List<TakingPhotoBySimNoDto> takingPhotoBySimNoDtos = takephotoDao.searchTakingVehicle();
        if (integerTakingphotosbytimeMap != null && integerTakingphotosbytimeMap.size() > 0) {
            for (int i = 0; i < takingPhotoBySimNoDtos.size(); i++) {
                TakingPhotoBySimNoDto takingPhotoBySimNoDto = takingPhotoBySimNoDtos.get(i);
                try {
                    String simNo = takingPhotoBySimNoDto.getSimNo();//配置的simno，也就是要进行定时拍照检测的simNo
                    int id = takingPhotoBySimNoDto.getId();
                    if (integerTakingphotosbytimeMap.containsKey(id)) {
                        Takingphotosbytime takingphotosbytime = integerTakingphotosbytimeMap.get(id);
                        checkTakingPhoto(simNo, takingphotosbytime.getId(), takingphotosbytime);
                    }
                } catch (Exception e) {
                    log.error("定时拍照:simNo="+takingPhotoBySimNoDto.getSimNo()+"id="+takingPhotoBySimNoDto.getId()+"执行异常",e);
                }
            }
        }
        long e = System.currentTimeMillis(); //获取结束时间
        log.debug("定时拍照: 检测一次用时：" + (e - s) + "ms,条数" + takingPhotoBySimNoDtos.size());
    }


    /**
     * 检测是否要进行拍照
     */
    private void checkTakingPhoto(String simNo, int id, Takingphotosbytime takingphotosbytime) {
        VehicleData vehicleData = realDataService.getVehicleData(simNo);
        if (vehicleData != null) {
            //拍摄通道如果没有配置
            if (StringUtil.isNullOrEmpty(takingphotosbytime.getChannel())) {
                return;
            }
            //如果设备没有配置通道号，也直接跳过
            if(vehicleData.getVideoChannelNum()==0){
                return;
            }
            IsPhotoDto isPhotoDto = redisIsPhotoCache.get(vehicleData.getEntityId(), id);

            //这边判断是否在配置生效的有效期里面
          if(takingphotosbytime.getValidstarttime()!=null||takingphotosbytime.getValidendtime()!=null) {
              if (takingphotosbytime.getValidstarttime() == null && takingphotosbytime.getValidendtime().getTime() < System.currentTimeMillis()) {
                  return;
              }
              else if (takingphotosbytime.getValidendtime() == null && takingphotosbytime.getValidstarttime().getTime() > System.currentTimeMillis()) {
                  return;
              }
              else if (!TimeUtils.isEffectiveDate(new Date(),
                      takingphotosbytime.getValidstarttime(),
                      takingphotosbytime.getValidendtime())) {//如果不在有效期里面
                  return;
              }
          }

            //这边判断时间区间
            String starttime = takingphotosbytime.getStarttime();
            starttime = TimeUtils.dateToStr(new Date()) +" "+ starttime;
            String endtime = takingphotosbytime.getEndtime();
            endtime = TimeUtils.dateToStr(new Date()) +" "+ endtime;
            Date startdatetime = TimeUtils.todatetime(starttime);
            Date enddatetime = TimeUtils.todatetime(endtime);



            if(startdatetime.getTime()>enddatetime.getTime()){//假如开始时间大于结束时间，那么说明希望跨夜间
                boolean arg=false;
                if (TimeUtils.isEffectiveDate(new Date(),
                        startdatetime,
                        TimeUtils.getDatebyDAY(enddatetime,1))) {
                    arg=true;
                }
                if (TimeUtils.isEffectiveDate(new Date(),
                        TimeUtils.getDatebyDAY(startdatetime,-1),
                        enddatetime)) {
                    arg=true;
                }
                if(arg==false){
                    return;
                }
            }else{
                if (!TimeUtils.isEffectiveDate(new Date(),
                        startdatetime,
                        enddatetime)) {
                    return;
                }
            }


            if (isPhotoDto != null) {//这边不为null说明已经有下发过拍照命令
                //之前逻辑是如果没有上传就等待五分钟，
//                if (isPhotoDto.getStatus() == IsPhotoDto.已下发) {//如果只是已经下发，但是却没有上传照片，过五分钟之后继续下发
//                    boolean differ5minute = TimeUtils.differminute(isPhotoDto.getTime(), new Date(), 5);
//                    if (differ5minute) {//如果相差五分钟，并且之前已经下发确没有收到照片，那么就继续进行判断
//                        checkConfigBySimNo(vehicleData.getEntityId(), simNo, takingphotosbytime);
//                    }
//                } else {//如果之前已经收到了照片，那么判断的时间就必须读取配置了
//                    boolean differminute = TimeUtils.differminute(isPhotoDto.getTime(), new Date(), takingphotosbytime.getConfiginterval());
//                    if (differminute) {
//                        checkConfigBySimNo(vehicleData.getEntityId(), simNo, takingphotosbytime);
//                    }
//                }

                //现在的逻辑则是只要下发过，那么再下次就是按照间隔时间来下发
                boolean differminute = TimeUtils.differminute(isPhotoDto.getTime(), new Date(), takingphotosbytime.getConfiginterval());
                if (differminute) {
                    checkConfigBySimNo(vehicleData.getEntityId(), simNo, takingphotosbytime);
                }
            } else {//如果为null，那么就是都没有执行过，就开始初始判断
                checkConfigBySimNo(vehicleData.getEntityId(), simNo, takingphotosbytime);
            }


        }
    }

    /**
     * 根据simNo和配置判断是否已经满足自动拍照的要求
     *
     * @return
     */
    private boolean checkConfigBySimNo(long vehicleId, String simNo, Takingphotosbytime takingphotosbytime) {
        GPSRealData gpsRealData = realDataService.get(simNo);
        if(gpsRealData==null){
            return false;
        }
        if (gpsRealData.isOnline() == false ||
                TimeUtils.differminute(gpsRealData.getUpdateDate(), new Date(), 5)) {//离线判断，离线或者信号时间相差五分钟
            return false;
        }
        gpsRealData.setVehicleId(vehicleId);
        Long condition = takingphotosbytime.getConfigcondition();
        if (Takingphotosbytime.condition不限制 == condition) {//到这一步说明前面都已经判断完了
            takingPhotosbyTimeQueue.addQueue(simNo, takingphotosbytime, gpsRealData);
            return true;
        } else if (Takingphotosbytime.condition根据车速 == condition) {
            if (gpsRealData.getVelocity() >= takingphotosbytime.getSpeed()) {
                takingPhotosbyTimeQueue.addQueue(simNo, takingphotosbytime, gpsRealData);
                return true;
            }
        } else if (Takingphotosbytime.condition根据停车时长 == condition) {
            Date parkingTime = gpsRealData.getParkingTime();
            if (parkingTime != null) {//这边判断停车时长是否达到
                if (TimeUtils.differminute(parkingTime, new Date(), takingphotosbytime.getPackduration())) {
                    takingPhotosbyTimeQueue.addQueue(simNo, takingphotosbytime, gpsRealData);
                    return true;
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
        Date datebyDAY = TimeUtils.getDatebyDAY(new Date(), 1);
        System.out.println(datebyDAY);
    }


}


