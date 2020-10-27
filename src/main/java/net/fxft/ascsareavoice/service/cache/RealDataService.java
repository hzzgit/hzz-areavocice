package net.fxft.ascsareavoice.service.cache;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.ascsareavoice.service.IRealDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class RealDataService implements IRealDataService {

    private static final Logger logger = LoggerFactory.getLogger(RealDataService.class);

    private ConcurrentMap<String, VehicleData> vehicleDataMap = new ConcurrentHashMap();

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JdbcUtil jdbcUtil;

    //用来监控刷新车辆静态缓存的队列，只要有车辆修改，或者是有外部车辆修改，都要加入该队列，并且进行刷新缓存
    public static AtomicBoolean updateVehiclearg = new AtomicBoolean(true);



    /**
     * 定时任务，20分钟检测一次是否没有刷新静态车辆信息,如果队列无数据，那么就刷新
     */
    @Scheduled(fixedRate = 1200000)
    public void refreshvehicledata() {
        RealDataService.updateVehiclearg.set(true);
    }

    @PostConstruct
    private void init() {
        new Thread(() -> {
            while (true) {
                if (RealDataService.updateVehiclearg.get()) {//如果队列里面有需要更新车辆缓存
                    RealDataService.updateVehiclearg.set(false);
                    try {
                        ConcurrentMap<String, VehicleData> vehicleDataMapTemp = new ConcurrentHashMap();
                        String sql = "select vehicleId,plateNo,simNo from vehicle where deleted=false";
                        List<VehicleData> vehicleData = jdbcUtil.sql(sql).query(VehicleData.class);
                        if (ConverterUtils.isList(vehicleData)) {
                            for (VehicleData vehicleDatum : vehicleData) {
                                vehicleDataMapTemp.put(vehicleDatum.getSimNo(), vehicleDatum);
                            }
                        }
                        vehicleDataMap=vehicleDataMapTemp;
                    } catch (Exception e) {
                        logger.error("车辆缓存报错", e);
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public GPSRealData getGpsRealData(String simNo) {
        try {
            GPSRealData rd = (GPSRealData) redisUtil.execute(jedis -> {
                GPSRealData rd1 = new GPSRealData();
                byte[] bytes = jedis.get(("rd:" + simNo).getBytes());
                if (bytes != null) {
                    rd1 = KryoUtil.byte2object(bytes, GPSRealData.class);
                } else {
                    rd1 = null;
                }
                return rd1;
            });
            return rd;
        } catch (Exception e) {
            logger.error("查询实时数据从redis中报错，返回空的值", e);
            return null;
        }
    }

    @Override
    public GPSRealData get(String simNo) {
        try {
            GPSRealData rd = (GPSRealData) redisUtil.execute(jedis -> {
                GPSRealData rd1 = new GPSRealData();
                byte[] bytes = jedis.get(("rd:" + simNo).getBytes());
                if (bytes != null) {
                    rd1 = KryoUtil.byte2object(bytes, GPSRealData.class);
                } else {
                    rd1 = null;
                }
                return rd1;
            });
            return rd;
        } catch (Exception e) {
            logger.error("查询实时数据从redis中报错，返回空的值", e);
            return null;
        }
    }


    @Override
    public VehicleData getVehicleData(String simNo) {
        VehicleData vehicleData = null;
        if(vehicleDataMap.containsKey(simNo)){
            vehicleData = vehicleDataMap.get(simNo);
        }
        return  vehicleData;

    }

}
