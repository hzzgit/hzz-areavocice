package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.service;

import com.ltmonitor.entity.GPSRealData;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.kafka.KafkaMessageSender;
import net.fxft.ascsareavoice.ltmonitor.entity.Alarm;
import net.fxft.ascsareavoice.ltmonitor.service.INewAlarmService;
import net.fxft.ascsareavoice.ltmonitor.service.MapFixService;
import net.fxft.ascsareavoice.ltmonitor.util.Constants;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;
import net.fxft.ascsareavoice.ltmonitor.vo.PointLatLng;
import net.fxft.ascsareavoice.service.GpsFliter.GPSFilter;
import net.fxft.ascsareavoice.service.GpsFliter.GpsInfo;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.AreaDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.OrderAreaDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.OrderDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.PointDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.WaybillAreaKeyPointCache;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.WaybillAreaKeyPointautoCache;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.enumconfig.WaybillAreaKeyPointEnum;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.service.DTO.SimNoOrderKeyPointDTO;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.alarm.AreaAlarmEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * @author ：hzz
 * @description：运单围栏关键点停车关键点停车计算逻辑
 * @date ：2020/10/21 11:00
 */
@Service
@Slf4j
public class WaybillAreaKeyPointService {


    /**
     * 是否开启运单围栏关键点停车关键点停车报警
     */
    @Value("${isWaybillAreaKeyPoint:false}")
    private boolean isWaybillAreaKeyPoint;


    //运单围栏的点位圆圈半径
    @Value("${WaybillArearadius:200}")
    private int WaybillArearadius;

    /**
     * 处理运单围栏关键点停车的独立队列
     */
    private ConcurrentLinkedQueue<GPSRealData> AreaQueue = new ConcurrentLinkedQueue();


    /**
     * 当前车辆的停车关键点报警的记录
     */
    private ConcurrentMap<String, SimNoOrderKeyPointDTO> simNoKeyPointRecord = new ConcurrentHashMap<>();

    @Autowired
    private WaybillAreaKeyPointCache waybillAreaKeyPointCache;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private INewAlarmService newAlarmService;


    /**
     * 移除掉已经没有配置的simNo
     */

    public void removeRecordbysimNo(ConcurrentHashMap<String, List<Long>> simNoOrderCache,
                                    ConcurrentHashMap<Long, OrderDO> orderAreaCache) {
        try {
            int count = 0;
            Set<Map.Entry<String, SimNoOrderKeyPointDTO>> entries = simNoKeyPointRecord.entrySet();
            Iterator<Map.Entry<String, SimNoOrderKeyPointDTO>> iterator = entries.iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SimNoOrderKeyPointDTO> next = iterator.next();
                String key = next.getKey();
                String[] split = key.split("-");
                String simNo = split[0];
                if (!simNoOrderCache.containsKey(simNo)) {//如果不包含那么就移除
                    simNoKeyPointRecord.remove(key);
                    count = count + 1;
                } else {
                    long orderid = ConverterUtils.toLong(split[1]);
                    List<Long> orderIds = simNoOrderCache.get(simNo);
                    /*如果这个订单已经不包含在这个设备中，那么也移除*/
                    if (!orderIds.contains(orderid)) {
                        simNoKeyPointRecord.remove(key);
                        count = count + 1;
                    } else {
                        /*这边判断这个订单中的区域是否已经不再和这个订单绑定*/
                        long areaid = ConverterUtils.toLong(split[2]);
                        if (!orderAreaCache.containsKey(orderid)) {
                            simNoKeyPointRecord.remove(key);
                            count = count + 1;
                        } else {
                            OrderDO orderDO = orderAreaCache.get(orderid);
                            List<OrderAreaDO> orderAreaDOS = orderDO.getOrderAreaDOS();
                            boolean isarg = true;//判断是否移除
                            if (ConverterUtils.isList(orderAreaDOS)) {
                                for (OrderAreaDO orderAreaDO : orderAreaDOS) {
                                    long areaid1 = orderAreaDO.getAreaid();
                                    if (areaid == areaid1) {
                                        isarg = false;
                                    }
                                }
                            }
                            if (isarg) {
                                simNoKeyPointRecord.remove(key);
                                count = count + 1;
                            }
                        }
                    }
                }
            }
            log.debug("检测移除无用的simNo的缓存,移除的数量为:" + count);
        } catch (Exception e) {
            log.error("检测移除无用的simNo的缓存异常", e);
        }
    }

    /**
     * 定时写入缓存，初始化读取缓存
     */
    private void checkfilecache() {
        if (isWaybillAreaKeyPoint) {
            ConcurrentMap<String, SimNoOrderKeyPointDTO> stringBooleanConcurrentMap = WaybillAreaKeyPointautoCache.loadCache();
            if (stringBooleanConcurrentMap != null) {
                simNoKeyPointRecord = stringBooleanConcurrentMap;
            }
            /*这边以超高频率进行缓存写入文件*/
            new Thread(() -> {
                while (true) {
                    WaybillAreaKeyPointautoCache.saveCache(simNoKeyPointRecord);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


    @PreDestroy
    private void destory() {
        WaybillAreaKeyPointautoCache.saveCache(simNoKeyPointRecord);
    }


//    private ConcurrentHashMap<String, simNoTEST> stringsimNoTESTConcurrentHashMap = new ConcurrentHashMap<>();
//
//    @PostConstruct
//    /* 开启可以模拟车辆移动*/
//    private void test() {
//        new Thread(() -> {
//            while (true) {
//                ConcurrentHashMap<String, simNoTEST> stringsimNoTESTConcurrentHashMaptemp = new ConcurrentHashMap<>();
//                List<simNoTEST> query = JdbcUtil.getDefault().sql("select * from keypoint_simnotest").query(simNoTEST.class);
//                for (simNoTEST simNoTEST : query) {
//                    stringsimNoTESTConcurrentHashMaptemp.put(simNoTEST.getSimNo(), simNoTEST);
//                }
//                stringsimNoTESTConcurrentHashMap = stringsimNoTESTConcurrentHashMaptemp;
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    /**
     * 加入队列
     *
     * @param rd
     */
    public void addAreaqueue(GPSRealData rd) {
        if (isWaybillAreaKeyPoint) {
            if (rd.getSendTime() == null || rd.getLongitude() <= 0 || rd.getLatitude() <= 0 ||
                    TimeUtils.isdifferminute(rd.getSendTime(), new Date(), 1200)) {
                return;
            }
//            if (stringsimNoTESTConcurrentHashMap.containsKey("040231521285")) {
//                simNoTEST simNoTEST = stringsimNoTESTConcurrentHashMap.get("040231521285");
//                rd.setLongitude(simNoTEST.getLongitude());
//                rd.setLatitude(simNoTEST.getLatitude());
//                rd.setSimNo("040231521285");
//                rd.setVehicleId(1);
//            }
            if (!waybillAreaKeyPointCache.isWaybillArea(rd.getSimNo())) {
                return;
            }
            GpsInfo instance = GpsInfo.getInstance(rd);
            List<Map<String, Object>> maps = GPSFilter.gpsFilter(instance, ConverterUtils.toString(rd.getVehicleId()));
            if (maps != null && maps.size() > 0) {
                for (Map<String, Object> map : maps) {
                    int type = ConverterUtils.toInt(map.get("type"));
                    if (type == 0) {//这边只需要正常时间的的点
                        Object data = map.get("data");
                        GpsInfo gpsInfo = (GpsInfo) data;
                        AreaQueue.add(gpsInfo.gpsinfotoGpsRealData());
                    }

                }
            }
        }
    }


    @PostConstruct
    private void processorAreaAlarm() {
        if (isWaybillAreaKeyPoint) {

            checkfilecache();

            new Thread(() -> {
                while (true) {
                    try {
                        int qs = AreaQueue.size();
                        if (qs > 100) {
                            log.debug("处理运单围栏关键点停车报警队列" + AreaQueue + "排队等待应答数量:" + qs);
                        }
                        GPSRealData rd = AreaQueue.poll();
                        int k = 0;
                        while (rd != null) {
                            log.debug("开始计算运单围栏关键点停车报警,simNo" + rd.getSimNo());
                            try {
                                analyze(rd);
                            } catch (Exception e) {
                                log.error("计算运单围栏关键点停车异常,rd：" + rd.toString(), e);
                            }
                            rd = AreaQueue.poll();
                        }
                    } catch (Exception ex) {
                        log.error("围栏关键点停车报警队列处理线程异常", ex);
                    }

                    if (AreaQueue.size() == 0) {
                        try {//每隔五秒检测一次队列处理线程
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                        }
                    }
                }
            }).start();

        }
    }


    /**
     * 开始处理计算运单围栏关键点停车
     *
     * @param rd
     */
    private void analyze(GPSRealData rd) throws Exception {
        String simNo = rd.getSimNo();
        List<OrderDO> configSimNos = waybillAreaKeyPointCache.getConfigSimNo(simNo);
        if (!ConverterUtils.isList(configSimNos)) {
            return;
        }
        //时间的判断直接在sql查询的时候解决
        for (OrderDO configSimNo : configSimNos) {
            Long orderId = configSimNo.getOrderId();//订单id
            Long userId = configSimNo.getUserId();
            List<OrderAreaDO> orderAreaDOS = configSimNo.getOrderAreaDOS();
            if (ConverterUtils.isList(orderAreaDOS)) {
                for (OrderAreaDO orderAreaDO : orderAreaDOS) {
                    Long areaid = orderAreaDO.getAreaid();//区域id
                    /*停车解除的位移大小，单位米*/
                    Integer cfgparkdisplacedistance = orderAreaDO.getCfgparkdisplacedistance();
                    /* 停车时长，单位秒*/
                    Integer cfgparktime = orderAreaDO.getCfgparktime();

                    /*进入关键点停车超时报警*/
                    Integer cfgparkdisplacetime = orderAreaDO.getCfgparkdisplacetime();
                    AreaDO areaConfig = waybillAreaKeyPointCache.getAreaConfig(areaid);
                    if (areaConfig == null) {//如果点位没有配置，那么就直接结束
                        return;
                    }

                    String name = areaConfig.getName();
                    List<PointDO> pointDOS = areaConfig.getPointDOS();
                    if (ConverterUtils.isList(pointDOS)) {
                        for (PointDO pointDO : pointDOS) {
                            Double latitude = pointDO.getLatitude();
                            Double longitude = pointDO.getLongitude();
                            String maptype = pointDO.getMaptype();
                            Long pointid = pointDO.getPointid();
                            int cfgradius = pointDO.getCfgradius();
                            if (cfgradius == 0) {
                                cfgradius = WaybillArearadius;
                            }
                            PointLatLng mp = new PointLatLng();
                            //根据区域的地图类型，将GPS终端的坐标转成对应的地图类型，进行比对判断
                            if (Constants.MAP_BAIDU.equalsIgnoreCase(maptype)) {
                                mp = MapFixService.fix(rd.getLatitude(), rd.getLongitude(), Constants.MAP_BAIDU);
                            } else if (Constants.MAP_GOOGLE.equalsIgnoreCase(maptype)) {
                                mp = MapFixService.fix(rd.getLatitude(), rd.getLongitude(),
                                        Constants.MAP_GOOGLE);
                            } else {
                                mp = new PointLatLng(rd.getLongitude(), rd.getLatitude());
                            }
                            PointLatLng pl = new PointLatLng(longitude, latitude);
                            /*判断是否进入了点位的半径圆*/
                            Boolean inArea = MapFixService.IsInCircle(mp, pl, cfgradius);
                            SimNoOrderKeyPointDTO simNoKeyPointDTO = new SimNoOrderKeyPointDTO();//用来记录当前围栏车辆的情况
                            String key = getkey(simNo, orderId, areaid, pointid);
                            if (simNoKeyPointRecord.containsKey(key)) {
                                simNoKeyPointDTO = simNoKeyPointRecord.get(key);
                            }
                            synchronized (simNoKeyPointDTO) {
                                /*判断停车标志位*/
                                boolean ispark = false;
                                if (simNoKeyPointDTO.getLongitude() == null || simNoKeyPointDTO.getLatitude() == null) {//如果经纬度有一个是null。，那就不进行停车点比对
                                    //记录第一个点的经纬度

                                } else {//如果已经有经纬度了
                                    /*只有在触发了进入关键点停车报警之后或者进入了围栏才进行以下计算*/
                                    if (simNoKeyPointDTO.isInAlarm() || inArea) {
                                        /*上一个点是否在停车中*/
                                        boolean isparkNow = simNoKeyPointDTO.isIsparkNow();
                                        if (isparkNow) {
                                            double mi = MapFixService.GetDistanceByMeter(simNoKeyPointDTO.getLongitude(),
                                                    simNoKeyPointDTO.getLatitude()
                                                    , rd.getLongitude(), rd.getLatitude());
                                            /*两点距离在配置的解除停车位移以下都算是停住了*/
                                            if (mi <= cfgparkdisplacedistance) {
                                                ispark = true;
                                            } else if (simNoKeyPointDTO.getRemoveParkingCount() < 1) {
                                                ispark = true;
                                                /*离开停车位移的触发次数加1*/
                                                int removeParkingCount = simNoKeyPointDTO.getRemoveParkingCount();
                                                simNoKeyPointDTO.setRemoveParkingCount(removeParkingCount + 1);
                                            } else {

                                            }
                                        } else {
                                            /* 如果不在停车态，那么只有完全相同的经纬度才可以进入停车状态*/
                                            if (simNoKeyPointDTO.getLongitude().equals(rd.getLongitude())
                                                    && simNoKeyPointDTO.getLatitude().equals(rd.getLatitude())) {
                                                ispark = true;
                                                simNoKeyPointDTO.setIsparkNow(true);
                                            }
                                        }
                                    }
                                }
                                if (ispark == false) {//没有停车才记录，否则不改变
                                    /*这边记录下进入围栏的第一个时间，但不一定是停车时间*/
                                    if (simNoKeyPointDTO.isInAlarm()) {//如果之前是已经触发了进入关键点停车报警，那么就代表着这边要触发离开关键点停车报警
                                        long gettwodatediff = TimeUtils.gettwodatediff(
                                                simNoKeyPointDTO.getParkBeginTime(), rd.getSendTime());
                                        String descr = getdesrc(userId, orderId, areaid, pointid, gettwodatediff, name);
                                        crossAreaAlarm(rd, name, descr);//触发离开关键点停车报警
                                    }
                                    simNoKeyPointDTO.setParkBeginTime(rd.getSendTime());
                                    simNoKeyPointDTO.setInAlarm(false);
                                    simNoKeyPointDTO.setIsparkNow(false);
                                    simNoKeyPointDTO.setIsparkTimeOut(false);
                                    simNoKeyPointDTO.setRemoveParkingCount(0);
                                    simNoKeyPointDTO.setLongitude(rd.getLongitude());
                                    simNoKeyPointDTO.setLatitude(rd.getLatitude());
                                } else {
                                    /*只有在进入围栏的情况下才会有关键点停车报警和停车超时报警*/
                                    if (inArea) {
                                        if (simNoKeyPointDTO.isInAlarm() == false) {//只有没有触发过进入围栏才会报警
                                            /*计算下当前时间和之前记录的停车时间相差是否达到停车要求*/
                                            long gettwodatediff = TimeUtils.gettwodatediff(
                                                    simNoKeyPointDTO.getParkBeginTime(), rd.getSendTime());
                                            /*如果停车时间超过了配置的,且在围栏内，则触发进入关键点停车报警*/
                                            if (cfgparktime != null && cfgparktime > 0 && gettwodatediff >= cfgparktime) {
                                                String descr = getdesrc(userId, orderId, areaid, pointid, gettwodatediff, name);
                                                inAreaAlarm(rd, name, descr);
                                                simNoKeyPointDTO.setInAlarm(true);
                                                // simNoKeyPointDTO.setInAlarmOnce(true);
                                                /* 这个地方是触发了关键点停车报警之后,记录下来新的停车时长*/
                                                simNoKeyPointDTO.setParkBeginTime(rd.getSendTime());
                                            }
                                        } else {
                                            /*这边也是只有没触发过停车超时报警，才会触发*/
                                            if (simNoKeyPointDTO.isIsparkTimeOut() == false) {
                                                /*如果是触发过的，就继续计算停车超时*/
                                                long gettwodatediff = TimeUtils.gettwodatediff(
                                                        simNoKeyPointDTO.getParkBeginTime(), rd.getSendTime());
                                                /*超时时间不为空且不为0，且大于停车时长*/
                                                if (cfgparkdisplacetime != null && cfgparkdisplacetime > 0 && gettwodatediff >= cfgparkdisplacetime) {
                                                    String descr = getdesrc(userId, orderId, areaid, pointid, gettwodatediff, name);
                                                    inAreaAlarmParkIngTimeOut(rd, name, descr);
                                                    simNoKeyPointDTO.setIsparkTimeOut(true);
                                                    //  simNoKeyPointDTO.setIsparkTimeOutOnce(true);
                                                }
                                            }
                                        }
                                    }
                                }
                                /*缓存起来这辆车orderid和pointid的计算缓存*/
                                if (inArea || simNoKeyPointDTO.isInAlarm()) {
                                    /*只有当进入围栏或者是已经触发了关键点停车报警*/
                                    simNoKeyPointRecord.put(key, simNoKeyPointDTO);
                                } else {
                                    /*如果离开了围栏，且报警都结束了，那么就移除缓存*/
                                    simNoKeyPointRecord.remove(key);
                                }
                            }
                        }
                    }
                }
            }
        }

    }


    private String getdesrc(Long userId, Long orderId, Long areaId, Long pointid, long parktime, String name) {
        String descr = userId + ";" + orderId + ";" + areaId + ";" + pointid + ";" + parktime + ";" + name;
        return descr;
    }

    /**
     * 获取到记录车辆和订单和关键点的缓存配置的Key
     *
     * @param simNo
     * @param orderid
     * @param areaid
     * @param pointId
     * @return
     */
    private String getkey(String simNo, Long orderid, Long areaid, Long pointId) {
        return simNo + "-" + orderid + "-" + areaid + "-" + pointId;
    }


    private void inAreaAlarm(GPSRealData rd, String areaName, String descr) {
        String alarmsource = WaybillAreaKeyPointEnum.进入运单关键点停车报警.getAlarmSource();
        String alarmType = WaybillAreaKeyPointEnum.进入运单关键点停车报警.getAlarmType();
        insertAlarm(alarmsource, alarmType, rd, areaName, descr);
    }


    private void inAreaAlarmParkIngTimeOut(GPSRealData rd, String areaName, String descr) {
        String alarmsource = WaybillAreaKeyPointEnum.进入运单关键点停车超时报警.getAlarmSource();
        String alarmType = WaybillAreaKeyPointEnum.进入运单关键点停车超时报警.getAlarmType();
        insertAlarm(alarmsource, alarmType, rd, areaName, descr);
    }


    private void crossAreaAlarm(GPSRealData rd, String areaName, String descr) {
        String alarmsource = WaybillAreaKeyPointEnum.离开运单关键点停车报警.getAlarmSource();
        String alarmType = WaybillAreaKeyPointEnum.离开运单关键点停车报警.getAlarmType();
        insertAlarm(alarmsource, alarmType, rd, areaName, descr);
    }

    /**
     * 发送运单围栏关键点停车关键点停车报警
     *
     * @param alarmSource
     * @param alarmType
     * @param rd
     * @param areaName
     */
    private void insertAlarm(String alarmSource, String alarmType,
                             GPSRealData rd, String areaName, String descr) {
        log.info("发送运单围栏关键点停车,simNo:" + rd.getSimNo() + ",desc:" + descr);

        rd.setLocation("运单围栏关键点停车:" + areaName);
        Alarm alarm = this.newAlarmService.insertAlarm(alarmSource, alarmType, rd, "AreaAlarmService");
        AreaAlarmEvent areaAlarmEvent = new AreaAlarmEvent();
        BeanUtils.copyProperties(alarm, areaAlarmEvent);
        areaAlarmEvent.setDescr(descr);
        EventMsg em = new EventMsg();
        em.setEventBody(areaAlarmEvent);
        em.loadDefaultDevMsgAttr();
        log.debug("触发运单围栏关键点停车相关报警,内容:" + areaAlarmEvent.toString());
        kafkaMessageSender.sendAreaAlarmEventMsg(em, rd.getSimNo());
    }

}
