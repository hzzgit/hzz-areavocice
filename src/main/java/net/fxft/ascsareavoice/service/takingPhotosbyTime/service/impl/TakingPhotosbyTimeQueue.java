package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.util.StringUtil;
import com.netflix.ribbon.proxy.annotation.Var;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.TakephotoDao;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.DriverInfoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoBySimNoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytimeresult;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.RedisIsPhotoCache;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.dto.IsPhotoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.service.dto.QueueDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author ：hzz
 * @description：定时拍照队列处理类
 * @date ：2021/1/21 17:15
 */
@Service
@Slf4j
public class TakingPhotosbyTimeQueue {
    @Autowired
    private TemSendService temSendService;

    @Autowired
    private RealDataService realDataService;
    @Autowired
    private TakephotoDao takephotoDao;
    /**
     * 是否开启定时拍照功能
     */
    @Value("${istakingphoto:false}")
    private boolean istakingphoto;
    @Autowired
    private RedisIsPhotoCache redisIsPhotoCache;


    /**
     * 将需要下发拍照的信息加入到队列中
     */
    public void addQueue(String simNo, Takingphotosbytime takingphotosbytime, GPSRealData gpsRealData) {
        QueueDto queueDto = new QueueDto();
        queueDto.setSimNo(simNo);
        queueDto.setTakingphotosbytime(takingphotosbytime);
        queueDto.setGpsRealData(gpsRealData);
        istakingphotoQueue.add(queueDto);
    }

    /**
     * 处理运单围栏的独立队列
     */
    private ConcurrentLinkedQueue<QueueDto> istakingphotoQueue = new ConcurrentLinkedQueue();


    @PostConstruct
    private void init() {
        if (istakingphoto) {
            new Thread(() -> {
                while (true) {
                    try {

                        int qs = istakingphotoQueue.size();
                        if (qs > 100) {
                            log.debug("处理定时拍照队列排队等待应答数量:" + qs);
                        }
                        QueueDto rd = istakingphotoQueue.poll();

                        while (rd != null) {
                            sendTakingPhoto(rd);
                            rd = istakingphotoQueue.poll();
                        }
                    } catch (Exception ex) {
                        log.error("定时拍照队列处理线程异常", ex);
                    }

                    if (istakingphotoQueue.size() == 0) {
                        try {//每隔五秒检测一次队列处理线程
                            Thread.sleep(1000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }).start();

        }
    }


    /**
     * 下发拍照
     */
    private void sendTakingPhoto(QueueDto queueDto) {
        try {
            String simNo = queueDto.getSimNo();
            Takingphotosbytime takingphotosbytime = queueDto.getTakingphotosbytime();
            GPSRealData gpsRealData = queueDto.getGpsRealData();
            String channel = takingphotosbytime.getChannel();
            String[] channels = channel.split(";");
            DriverInfoDto driverInfoDto = takephotoDao.searchDriverInfo(gpsRealData.getVehicleId());
            String driverName=null;
            String certificationCode=null;
             if(driverInfoDto!=null){
                 driverName=driverInfoDto.getDriverName();
                 certificationCode=driverInfoDto.getCertificationCode();
             }
            //插入结果表，并获取主键
            Takingphotosbytimeresult takingphotosbytimeresult = takephotoDao.insertTakingphotosbytimeresult(gpsRealData.getVehicleId(), simNo, takingphotosbytime.getUserid(),
                    channels.length, gpsRealData.getLatitude(), gpsRealData.getLongitude(), gpsRealData.getSendTime(),
                    gpsRealData.getVelocity(),driverName, certificationCode,
                    takingphotosbytime.getId());
            String cmdIds = "";
            VehicleData vehicleData = realDataService.getVehicleData(simNo);
            String videoChannelNames = vehicleData.getVideoChannelNames();
            int videoChannelNum = vehicleData.getVideoChannelNum();
            //用来缓存设备配置的摄像头情况
            Map<Integer,Byte> channelMap=new HashMap<>();
            if(videoChannelNames!=null&&!"".equalsIgnoreCase(videoChannelNames.trim())){
                String[] split = videoChannelNames.split(";");
                for (String s : split) {
                    String[] split1 = s.split(",");
                    String chanid = split1[0];
                    channelMap.put(ConverterUtils.toInt(chanid), (byte) 0);
                }
            }else{//如果没有定义配置的摄像头
                for (int i = 1; i <=videoChannelNum ; i++) {
                    channelMap.put(i, (byte) 0);
                }
            }
            for (String c : channels) {
                Integer channelId = ConverterUtils.toInt(c);
                long cmdId=0;
                if (channelId > 0&&channelMap.containsKey(channelId)) {
                    //下发拍照指令，并获取到命令id
                     cmdId = temSendService.sendTakePhoto(simNo, channelId, takingphotosbytime.getUserid(), takingphotosbytime.getUsername());
                    cmdIds += cmdId + ";";
                }
                int commandtype=0;
                if(cmdId==0){
                    commandtype=2;
                }

                takephotoDao.insertTakingphotosbytimeDetail(takingphotosbytimeresult.getId(),
                        channelId, cmdId, takingphotosbytime.getId(), gpsRealData.getVehicleId(),commandtype);

            }
            redisIsPhotoCache.put(gpsRealData.getVehicleId(),takingphotosbytime.getId());
            log.debug("定时拍照:下发拍照并插入定时拍照记录表,内容为:simNo=" + simNo + ",通道=" + channel + ",命令id=" + cmdIds);
        } catch (Exception e) {
            log.error("定时拍照:下发拍照并插入定时拍照记录异常", e);
        }


    }
}


