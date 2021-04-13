package net.fxft.ascsareavoice.service.MapArea;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.util.DateUtil;
import com.ltmonitor.util.StringUtil;
import net.fxft.ascsareavoice.kafka.KafkaMessageSender;
import net.fxft.ascsareavoice.kafka.StartKafkaComsumer;
import net.fxft.ascsareavoice.ltmonitor.entity.*;
import net.fxft.ascsareavoice.ltmonitor.service.*;
import net.fxft.ascsareavoice.ltmonitor.util.Constants;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;
import net.fxft.ascsareavoice.ltmonitor.vo.PointLatLng;
import net.fxft.ascsareavoice.service.AutoVoice.impl.AutoVoiceQueueService;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.RowDataMap;
import net.fxft.common.log.AttrLog;
import net.fxft.common.util.BasicUtil;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.alarm.AreaAlarmEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

/**
 * 电子围栏报警服务
 * 原理：不断轮询区域，判断是否有车辆进入或者离开，生成报警和报警进出的记录
 *
 * @author DELL
 */
@Service("areaAlarmService")
public class AreaAlarmService implements IAreaAlarmService {

    protected Logger log = LoggerFactory.getLogger(getClass());

    public static String TURN_ON = "1";
    public static String TURN_OFF = "0";
    @Autowired
    private IMapAreaService mapAreaService;

    @Autowired
    private AreaGpsRealDataService areaGpsRealDataService;

    @Value("${areaQueueThreadco:3}")
    private int areaQueueThreadco;

    @Autowired
    private IAlarmRecordService alarmRecordService;
    @Autowired
    private ILineSegmentService lineSegmentService;
    @Autowired
    private IRouteSegmentService routeSegmentService;

    @Autowired
    private INewAlarmService newAlarmService;

    @Value("${areaalarm}")
    private boolean areaalarm;
    /**
     * 报警分析线程，单独开辟一个线程进行分析
     */
    private Thread analyzeThread;

    private volatile boolean continueAnalyze = true;

    /**
     *路线偏移缓存
     */

    private ConcurrentMap<String, AlarmItem> offsetRouteWarn = new ConcurrentHashMap();

    /**
     *在路线上的缓存
     */
    private ConcurrentMap<String, AlarmItem> onRouteWarn = new ConcurrentHashMap();
    // private Map routePointMap = new HashMap();
    /**
     * 当前的关键点报警，key为rd.getPlateNo() + "_" + ec.getEntityId() + "_" + alarmType;
     * 平台基本不会使用关键点报警，因为这个和圆形围栏是一个意思
     */
    private ConcurrentMap<String, AlarmItem> keyPlaceAlarmMap = new ConcurrentHashMap<String, AlarmItem>();

    /**
     * 用于缓存上个实时数据点位信息，key为simnO
     * 暂时已经不适用，后续如果有使用可以再次启用
     */
    Map<String, GPSRealData> realDataMap = new HashMap<String, GPSRealData>();
    private ConcurrentMap<String, AlarmItem> areaAlarmMap = new ConcurrentHashMap<String, AlarmItem>();
    // private ConcurrentMap<String, AlarmItem> overSpeedAlarmMap = new ConcurrentHashMap<String, AlarmItem>();//分段限速报警

    /**
     * 用来判断进出围栏的情况，如果已经进入围栏则不再报警，直到出去之后
     * key为simNo + 围栏主键 的组合
     */
    private ConcurrentMap<String, Boolean> CrossMap = new ConcurrentHashMap<>();

    /**
     * 缓存每个simNo对应的围栏配置主键
     */
    private ConcurrentMap<String, List<Integer>> AreaConfigMap = new ConcurrentHashMap<>();//当前围栏报警的车辆配置信息

    /**
     * 处理围栏计算的队列，主要存实时数据，也就是收到的每个点的信息
     */
    private ConcurrentLinkedQueue<GPSRealData> AreaQueue = new ConcurrentLinkedQueue();

    /**
     * 缓存每个围栏主键对应的围栏配置
     */
    private ConcurrentMap<Integer, MapArea> AllArea = new ConcurrentHashMap<>();//用来缓存所有的围栏信息

    /**
     * 缓存每个线路对应的路段信息，线路计算平台并没有使用，只使用了终端的线路围栏报警
     */
    private ConcurrentMap<Long, List<LineSegment>> linesMap = new ConcurrentHashMap<>();//用来缓存线路对应的拐点信息

    /**
     * 当前车辆点位的时间情况，主要用于过滤时间异常点
     */
    private ConcurrentMap<String, Date> AreaTimeMap = new ConcurrentHashMap<>();

    /**
     * 这边是下发给终端的语音播报命令队列，主要是用于下发离开线路语音播报的逻辑
     * 平台并没有配置平台的线路报警，逻辑是可以使用的
     */
    @Autowired
    private AutoVoiceQueueService autoVoiceQueueService;


    //添加到队列
    public void addAreaqueue(GPSRealData rd) {
        if (areaalarm) {
            //如果存在这个围栏配置就加入队列进行运算
            if (AreaConfigMap.containsKey(rd.getSimNo())) {
                boolean isargTme = false;
                if (TimeUtils.isdifferminute(rd.getSendTime(), new Date(), 1200)) {//如果超过20小时直接干掉
                    return;
                }
                if (AreaTimeMap.containsKey(rd.getSimNo())) {//如果存在那么就要去比对时间
                    Date areaTime = AreaTimeMap.get(rd.getSimNo());
                    if (rd.getSendTime().getTime() > areaTime.getTime()) {//如果这个大于这个原来的时间
                        isargTme = true;
                        AreaTimeMap.put(rd.getSimNo(), rd.getSendTime());
                    } else {//如果小于这个时间，但是却没有差距很大，那么也
                        if (TimeUtils.isdifferminute(rd.getSendTime(), areaTime, 30) && !TimeUtils.isdifferminute(rd.getSendTime(), new Date(), 5)) {//如果小于的和记录的差距在30分钟以上
                            isargTme = true;
                            AreaTimeMap.put(rd.getSimNo(), rd.getSendTime());
                        } else {
                            isargTme = false;
                        }
                    }
                } else {//如果不存在，那么就直接算作第一个时间点，时间不存在
                    isargTme = true;
                    AreaTimeMap.put(rd.getSimNo(), rd.getSendTime());
                }
                if (isargTme) {
                    //如果以上判断都通过，那么就加入到围栏计算队列当中
                    AreaQueue.add(rd);
                }
            } else {
                AreaTimeMap.remove(rd.getSimNo());
            }
        }
    }


    @PostConstruct
    public void start() {
        if (areaalarm) {


            ConcurrentMap<String, Boolean> stringBooleanConcurrentMap = AreaAlarmCache.loadCache();
            if (stringBooleanConcurrentMap != null) {
                CrossMap = stringBooleanConcurrentMap;
            }

            analyzeThread = new Thread(new Runnable() {
                public void run() {
                    analyzeThreadFunc();
                }
            });
            analyzeThread.start();
//            for (int i = 0; i < areaQueueThreadco; i++) {//开三个线程处理有围栏报警的设备队列
//
//            }
            new Thread(() -> {//处理围栏报警队列的线程
                processorAreaAlarm();
            }).start();


            new Thread(() -> {
                while (true) {
                    AreaAlarmCache.saveCache(CrossMap);
                    try {
                        Thread.sleep(30000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    /**
     *处理每个定位点的围栏进出报警
     */

    private void processorAreaAlarm() {
        int times = 0;
        while (true) {
            try {
                if (times > 0 && times % 10 == 0) {
                    times = 0;
                    int qs = AreaQueue.size();
                    if (qs > 100) {
                        log.debug("处理围栏报警队列" + AreaQueue + "排队等待应答数量:" + qs);
                    }
                }
                GPSRealData rd = AreaQueue.poll();
                int k = 0;
                while (rd != null) {
                    if (AreaConfigMap.containsKey(rd.getSimNo())) {
                        log.debug("开始判断围栏报警,simNo" + rd.getSimNo());
                        List<Integer> AreaId = AreaConfigMap.get(rd.getSimNo());
                        analyze(AreaId, rd);//进行围栏报警处理
                    }
                    rd = AreaQueue.poll();
                }
            } catch (Exception ex) {
                log.error("围栏报警队列处理线程异常", ex);
            }
            times++;
            if (AreaQueue.size() == 0) {
                try {//每隔五秒检测一次队列处理线程
                    Thread.sleep(5000);
                } catch (InterruptedException e1) {
                }
            }
        }
    }


    /**
     * 十秒缓存一次围栏配置信息
     */

    private void analyzeThreadFunc() {
        while (continueAnalyze) {
            try {
                AreaConfigThread();
                log.debug("围栏处理队列剩余" + AreaQueue.size());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            try {
                if (continueAnalyze) {
                    Thread.sleep(10000L);
                }
            } catch (InterruptedException e1) {
            }
        }
    }

    @PreDestroy
    public void stopService() {
        log.info("---begin stop AreaAlarmService---");
        continueAnalyze = false;
        StartKafkaComsumer.ispausepool = true;
        if (analyzeThread == null)
            return;
        try {
            analyzeThread.interrupt();
            while (AreaQueue.size() > 0) {
                log.debug("围栏处理队列中还有" + AreaQueue.size() + "条，等待处理完再关闭");
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("---end stop AreaAlarmService---");

    }

    /**
     *缓存围栏配置信息
     */

    private void AreaConfigThread() {
        AttrLog alog = AttrLog.get("缓存围栏配置信息20200925版本,围栏兼容机构的模式");
        try {
            long s = System.currentTimeMillis();   //获取开始时间
            //  Map<String, Integer> areaBindingMap = new HashMap<String, Integer>();
            String sql = " select b.owner,b.areaId,v.simNo from MapAreaBinding b  " +
                    "  left join vehicle v  on b.vehicleId=v.vehicleId  " +
                    "  where 1=1  and b.bindType = 'platform' and v.deleted=false  " +
                    " and b.configType !=3 " +
                    " union  select null owner,md.configId areaId,v.simNo from mapareabydep md left join vehicle v on md.depId =v.depId\n" +
                    "where v.deleted=false  and md.deleted=false ";
            List<RowDataMap> bindings = new ArrayList<>();

            try {
                bindings = JdbcUtil.getDefault().sql(sql).queryWithMap();
            } catch (Exception e) {
                log.error("围栏未改成新版本，使用旧版本读取方式");
                sql = " select b.owner,b.areaId,v.simNo from MapAreaBinding b  " +
                        "  left join vehicle v  on b.vehicleId=v.vehicleId  " +
                        "  where 1=1  and b.bindType = 'platform' and v.deleted=false  " +
                        " and b.configType !=3 ";
                bindings = JdbcUtil.getDefault().sql(sql).queryWithMap();
            }
            ConcurrentMap<String, List<Integer>> AreaConfigMap1 = new ConcurrentHashMap<>();
            if (ConverterUtils.isList(bindings)) {
                for (RowDataMap binding : bindings) {
                    //  String plateNo = ConverterUtils.toString(binding.get("plateNo"));
                    String simNo = ConverterUtils.toString(binding.get("simNo"));
                    String owner = binding.getStringValue("owner");
                    int areaId = ConverterUtils.toInt(binding.get("areaId"));
                    // areaBindingMap.put(plateNo + "_" + areaId, areaId);
                    List<Integer> integerList = new ArrayList<>();
                    if (AreaConfigMap1.containsKey(simNo)) {//如果存在就新增
                        integerList = AreaConfigMap1.get(simNo);
                    }
                    integerList.add(areaId);
                    AreaConfigMap1.put(simNo, integerList);
                    String key = simNo + "_" + areaId;
                    if (!StringUtil.isNullOrEmpty(owner) && "clw".equalsIgnoreCase(owner) && !CrossMap.containsKey(key)) {
                        CrossMap.put(key, true);
                    }
                }
                AreaConfigMap = AreaConfigMap1;

            }

            String hql = "select * from MapArea where deleted=false ";
            List<MapArea> query = JdbcUtil.getDefault().sql(hql).query(MapArea.class);
            ConcurrentHashMap<Integer, MapArea> allMap = new ConcurrentHashMap<>();
            if (ConverterUtils.isList(query)) {
                for (MapArea mapArea : query) {
                    Integer AreaId = Math.toIntExact(mapArea.getEntityId());
                    if (!allMap.containsKey(AreaId)) {
                        allMap.put(AreaId, mapArea);
                    }
                }
                AllArea = allMap;
            }

            String hsql = " select l.* from LineSegment l left join maparea a \n" +
                    "on l.routeId =a.areaId where a.deleted=false \n" +
                    "order by l.pointId ";

            List<LineSegment> ls = JdbcUtil.getDefault().sql(hsql).query(LineSegment.class);
            ConcurrentMap<Long, List<LineSegment>> linesMap1 = new ConcurrentHashMap<>();
            if (ConverterUtils.isList(ls)) {
                linesMap1 = ls.stream().sorted(Comparator.comparing(LineSegment::getPointId)).collect(Collectors.groupingByConcurrent(LineSegment::getRouteId));
                linesMap = linesMap1;
            }

            long e = System.currentTimeMillis(); //获取结束时间
            log.debug(alog.toString() + "用时：" + (e - s) + "ms");
        } catch (Exception e) {
            alog.log("出现异常", BasicUtil.exceptionMsg(e));
            log.error(alog.toString());
        }
    }

    /**
     * 主逻辑，将受到的定位信息进行围栏报警判断
     * @param AreaId
     * @param rd
     */
    public void analyze(List<Integer> AreaId, GPSRealData rd) {
        AttrLog alog = AttrLog.get("处理单个设备的围栏报警")
                .log("simNo", rd.getSimNo())
                .log("areaIds", AreaId);
        try {
            if (ConverterUtils.isList(AreaId)) {
                for (Integer areaId : AreaId) {
//                    if ("012345678911".equalsIgnoreCase(rd.getSimNo())) {
//                        System.out.println(1);
//                    }
                    long s = System.currentTimeMillis();   //获取开始时间
                    String key = areaId + rd.getSimNo();
                    if (rd != null) {
//                        rd.setOffsetRouteAlarm(null);
//                        rd.setMapAreaAlarm(null);
//                        rd.setArriveKeyPlaceAlarm(null);
//                        rd.setLeaveKeyPlaceAlarm(null);
//                        rd.setOverSpeedAlarm(null);
                        GPSRealData oldRd = realDataMap.get(key);
                        if (oldRd != null) {
                            if (oldRd.getLatitude() == rd.getLatitude()
                                    && oldRd.getLongitude() == rd.getLongitude()) {
                                //TODO 这个地方是为了判断是否一直在一个地方
                                // return;
                            }
                        } else {
                            oldRd = new GPSRealData();
                        }
                        oldRd.setLatitude(rd.getLatitude());
                        oldRd.setLongitude(rd.getLongitude());
                        realDataMap.put(key, oldRd);
                        MapArea ec = AllArea.get(areaId);
                        if (ec != null) {
//                            ec.setStartDate(TimeUtils.todatetime(TimeUtils.dateTodetailStr(ec.getStartDate())));
//                            ;
//                            ec.setEndDate(TimeUtils.todatetime(TimeUtils.dateTodetailStr(ec.getEndDate())));
//                            ;
                            //areaMap.put(areaId, ec);
                            this.AnalyzeAreaAlarm(rd, ec);
                        }
                        long e = System.currentTimeMillis(); //获取结束时间
                        log.debug(alog.toString() + "用时" + (e - s) + "ms");
                    }
                }
            }
        } catch (Exception e) {
            alog.log("出现异常", BasicUtil.exceptionMsg(e));
            log.error(alog.toString());
        }
    }

    private Boolean IsInTimeSpan(MapArea br) {
        /**
         * String str = new Date().toString("HH:mm"); Date now = new Date();
         * Date start = Date.Parse(new Date().ToString("yyyy-MM-dd ") +
         * br.StartTime); Date end = Date.Parse(new
         * Date().ToString("yyyy-MM-dd ") + br.EndTime);
         *
         * return now.CompareTo(start) >= 0 && now.CompareTo(end) <= 0 ;
         */
        return true;
    }

    /**
     * 分段限速时，分析超速
     *
     * @param rd
     * @param seg
     */
    private void AnalyzeOverSpeed(GPSRealData rd, LineSegment seg, MapArea ec) {

        //  String key = rd.getSimNo();
        // AlarmItem alarmItem = overSpeedAlarmMap.get(key);

//        if (seg == null) {
//            //如果没在线路上，则不再分析分段超速
//            if (alarmItem != null) {
//                this.overSpeedAlarmMap.remove(key);
//                CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
//                        AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_OFF, rd,
//                        alarmItem.getAlarmId(), null);
//                //关闭分段超速报警
////                rd.setOverSpeedAlarm(null);
//            }
//            return;
//        }
        log.debug("路线名称:" + ec.getName() + ",线段Id:" + seg.getPointId());
        double maxSpeed = seg.getMaxSpeed();
//        RouteSegment rs = this.isInLimitSpeedRouteSegment(seg.getPointId(), ec);
//        if (rs == null)
//            return;

        log.debug("路线名称·:" + ec.getName() + ",分段:" + seg.getName() + "速度:"
                + rd.getVelocity());
        // 超速
        if (seg.getLimitSpeed() && rd.getVelocity() > maxSpeed) {
            log.debug("开始分段超速报警，路线名称:" + ec.getName() + ",分段:"
                    + seg.getName() + "速度:" + rd.getVelocity());
            insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, rd,
                    getAreaType(ec.getAreaType()) + ec.getName() + ",分段名称:" + seg.getName());
            //   if (AlarmRecord.STATUS_NEW.equals(alarmItem.getStatus())) {
            //创建分段超速报警
//                    CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
//                            AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_ON, rd,
//                            seg.getEntityId(),
//                            ec.getName() + ",分段名称:" + seg.getName());
            //   alarmItem.setStatus(AlarmRecord.STATUS_OLD);
            // this.overSpeedAlarmMap.put(key, rs);
            //   }

        }
        //    else {
        //    if (alarmItem != null) {
        // 报警结束时，更新报警状态，移除内存
        //      this.overSpeedAlarmMap.remove(key);
        //      CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
        //      AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_OFF, rd,
        //      seg.getEntityId(), null);
        //     }
        //     rd.setOverSpeedAlarm(null);
        //   }
    }

    /**
     * 路线偏移报警分析
     *
     * @param rd
     * @param ec
     * @param mp
     */
    private void AnalyzeOffsetRoute(GPSRealData rd, MapArea ec, PointLatLng mp) {
        try {
            Date start = new Date();
            // String alarmType = AlarmRecord.TYPE_CROSS_BORDER;
            String alarmSource = AlarmRecord.ALARM_FROM_PLATFORM;
            int maxAllowedOffsetTime = ec.getOffsetDelay(); // 获得延时报警的延时值

            // 是否在线路的某一段上
            LineSegment seg = IsInLineSegment(mp, ec);
            boolean isOnRoute = seg != null;
            log.debug(rd.getSimNo() + ",线路名称" + ec.getName() + "," +
                    (seg == null ? "偏离" : "行驶在线路上") +
                    ",时间:" + TimeUtils.dateTodetailStr(rd.getSendTime()));
            String alarmKey = rd.getSimNo() + "_" + ec.getEntityId();
            AlarmItem offsetAlarm = (AlarmItem) offsetRouteWarn.get(alarmKey);
            AlarmItem onRouteAlarm = (AlarmItem) onRouteWarn.get(alarmKey);
            if (isOnRoute == false) {//如果离开了线路,那么就报警,并且移除进入线路的缓存,加入到离开线路的缓存
                if (offsetAlarm == null) {
                    this.AnalyzeOverSpeed(rd, null, ec);
                    offsetAlarm = new AlarmItem(rd, AlarmRecord.TYPE_OFFSET_ROUTE, alarmSource);
                    // 开始报警
                    offsetRouteWarn.put(alarmKey, offsetAlarm);
                    offsetAlarm.setStatus("");

                }
                Date offsetTime = offsetAlarm.getAlarmTime();
                double ts = DateUtil.getSeconds(offsetTime, rd.getSendTime());
                //判断是否超过延时允许值
                if (ts >= maxAllowedOffsetTime
                        && offsetAlarm.getStatus().equals("")) {
                    offsetAlarm.setStatus(AlarmRecord.STATUS_NEW); //报警开始
                    if (checkisarea(ec.getAlarmType(), isOnRoute)) {
                        this.insertAlarm(alarmSource, AlarmRecord.TYPE_OFFSET_ROUTE, rd, getAreaType(ec.getAreaType()) + ec.getName());
                    }
                    if (checkisareatodriver(ec.getAlarmType(), isOnRoute)) {//是否下发给驾驶员
                        autoVoiceQueueService.addSendQueue("您已离开线路:" + ec.getName(), rd.getSimNo());
                    }
                    //                    rd.setOffsetRouteAlarm("偏离路线:" + ec.getName());
                    //这下面是创建报警记录
//                        Date originTime = rd.getSendTime();
//                        rd.setSendTime(offsetTime);
//                        // 创建偏离报警
//                        AlarmRecord sr = CreateRecord(
//                                AlarmRecord.ALARM_FROM_PLATFORM,
//                                AlarmRecord.TYPE_OFFSET_ROUTE, TURN_ON, rd,
//                                ec.getEntityId());
//                        if (sr != null) {
//                            sr.setStation(ec.getEntityId());
//                            sr.setLocation(ec.getName());
//                            alarmRecordService.saveOrUpdate(sr);
//                        }
//                        // 创建行驶在指定的线路上的记录
//                        sr = CreateRecord(AlarmRecord.ALARM_FROM_PLATFORM,
//                                AlarmRecord.TYPE_ON_ROUTE, TURN_OFF, rd,
//                                ec.getEntityId());
//                        if (sr != null) {
//                            sr.setStation(ec.getEntityId());
//                            sr.setLocation(ec.getName());
//                            alarmRecordService.saveOrUpdate(sr);
//                        }
//                        rd.setSendTime(originTime);
                    onRouteWarn.remove(alarmKey);
                }

            } else {//如果进入了线路,
                AnalyzeOverSpeed(rd, seg, ec);//如果在线路上，就开始分析分段限速报警
                if (offsetAlarm != null) {
                    offsetAlarm.setStatus(AlarmRecord.STATUS_OLD);// 如果在线路上，说明偏离线路报警结束
                    log.debug(rd.getSimNo() + "回到线路上," + rd.getSendTime());
                    CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_ON_ROUTE, TURN_OFF, rd,
                            ec.getEntityId(), null);
                    offsetRouteWarn.remove(alarmKey);
                }
                if (onRouteAlarm == null) {
                    onRouteAlarm = new AlarmItem(rd, AlarmRecord.TYPE_ON_ROUTE,
                            alarmSource);
//                        if (checkisarea(ec.getAlarmType(), isOnRoute)) {//这边判断下发给平台
//                            this.insertAlarm(alarmSource, AlarmRecord.TYPE_ON_ROUTE, rd,
//                                    getAreaType(ec.getAreaType()) + ec.getName() + ",路段:" + seg.getName());
//                        }
//                        if(checkisareatodriver(ec.getAlarmType(),isOnRoute)){//是否下发给驾驶员
//                            autoVoiceQueueService.addSendQueue("您已进入线路:"+ ec.getName() + ",路段:" + seg.getName(),rd.getSimNo());
//                        }
                    onRouteWarn.put(alarmKey, onRouteAlarm);

                }
            }


            double ts2 = DateUtil.getSeconds(start, new Date());
            if (ts2 > 0.2)
                log.debug(rd.getPlateNo() + "," + ec.getName() + "路线偏移报警耗时:" + ts2);
        } catch (Exception e) {
            log.error("平台线路报警处理错误", e);
        }

    }

    /**
     * 创建报警记录
     */
    private void CreateWarnRecord(String OperateType, String alarmType,
                                  String warnState, GPSRealData rd, long areaId, String location) {
//        AlarmRecord sr = CreateRecord(OperateType, alarmType, warnState, rd,
//                areaId);
//        if (sr != null) {
//            if (location != null)
//                sr.setLocation(location);
//            alarmRecordService.saveOrUpdate(sr);
//        }
    }

    //获取分段信息
    private RouteSegment isInLimitSpeedRouteSegment(long pointId, MapArea route) {
        long routeId = route.getEntityId();
        String hql = "select * from RouteSegment where routeId = ? and startSegId <= ? and endSegId >= ? ";

        RouteSegment rs = this.routeSegmentService.find(hql, new Object[]{
                routeId, pointId, pointId + 1});

        return rs;
    }

    private LineSegment IsInLineSegment(PointLatLng mp, MapArea ec) {
        List<LineSegment> segments = GetLineSegments(ec.getEntityId());
        LineSegment prevSegment = null;
        if (segments != null) {
            for (LineSegment seg : segments) {
                if (prevSegment != null) {
                    PointLatLng p1 = new PointLatLng(prevSegment.getLongitude1(),
                            prevSegment.getLatitude1());
                    PointLatLng p2 = new PointLatLng(seg.getLongitude1(),
                            seg.getLatitude1());

//                boolean result = MapFixService.isPointOnRouteSegment(p1, p2,
//                        mp, seg.getLineWidth());
                    //TODO 这边改成使用整条路线的线宽
                    boolean result = MapFixService.isPointOnRouteSegment(p1, p2,
                            mp, ec.getLineWidth());
                    if (result)
                        return prevSegment;
                }
                prevSegment = seg;
            }
        }
        return null;
    }


    /**
     * 获取线路的所有线段
     *
     * @param routeId
     * @return
     */
    private List<LineSegment> GetLineSegments(long routeId) {
        if (linesMap.containsKey(routeId)) {
            List<LineSegment> ls = linesMap.get(routeId);
            return ls;
        } else {
            return null;
        }
    }

    private MapArea getOldMapArea(String plateNo, long areaId) {
        MapArea oldEc = null;
        String key = plateNo + "_" + areaId;
        if (areaAlarmMap.containsKey(key)) {
            AlarmItem ai = areaAlarmMap.get(key);
            return ai.getMapArea();
        }
        return oldEc;
    }

    /**
     * 判断如果确实进出了围栏，那么就触发围栏报警kafka通知到报警中心
     *
     * @param ec
     * @param rd
     * @param isInArea
     */
    private void CrossBorder(MapArea ec, GPSRealData rd, boolean isInArea) {
        // MapArea oldArea = getOldMapArea(rd.getPlateNo(), ec.getEntityId());

//        if (isInArea) {
//            if (this.alarmConfigService.isAlarmEnabled(AlarmRecord.TYPE_IN_AREA, AlarmRecord.ALARM_FROM_PLATFORM))
//                rd.setMapAreaAlarm("进入区域:" + ec.getName());
//        } else {
//            if (this.alarmConfigService.isAlarmEnabled(AlarmRecord.TYPE_CROSS_BORDER, AlarmRecord.ALARM_FROM_PLATFORM))
//                rd.setMapAreaAlarm("离开区域:" + ec.getName());
//        }

        if (isInArea && checkisarea(ec.getAlarmType(), isInArea)) {

            insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_IN_AREA, rd, getAreaType(ec.getAreaType()) + ec.getName());

            //如果是第一次进入，则创建进入围栏记录
//            CreateAlarmRecord(AlarmRecord.ALARM_FROM_PLATFORM,
//                    AlarmRecord.TYPE_IN_AREA, TURN_ON, rd, ec);

//            String key = rd.getPlateNo() + "_" + ec.getEntityId();
//            AlarmItem ai = new AlarmItem(rd, AlarmRecord.TYPE_IN_AREA, AlarmRecord.ALARM_FROM_PLATFORM, ec);
            //areaAlarmMap.put(key, ai);
        }

        //离开围栏
        if (isInArea == false && checkisarea(ec.getAlarmType(), isInArea)) {
            insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_CROSS_BORDER, rd, getAreaType(ec.getAreaType()) + ec.getName());
//            CreateAlarmRecord(AlarmRecord.ALARM_FROM_PLATFORM,
//                    AlarmRecord.TYPE_IN_AREA, TURN_OFF, rd, ec);
            String key = rd.getPlateNo() + "_" + ec.getEntityId();
            //   areaAlarmMap.remove(key);// 离开围栏时，从内存中移除记录
        }

    }

    private String getAreaType(String areaType) {
        String name = "";
        if (MapArea.POLYGON.equalsIgnoreCase(areaType)) {
            name = "多边形:";
        } else if (MapArea.RECT.equalsIgnoreCase(areaType)) {
            name = "矩形:";
        } else if (MapArea.CIRCLE.equalsIgnoreCase(areaType)) {
            name = "圆形:";
        } else if (MapArea.ROUTE.equalsIgnoreCase(areaType)) {
            name = "线路:";
        } else if (MapArea.DIVISION.equalsIgnoreCase(areaType)) {
            name = "行政区域:";
        } else if (MapArea.MARKER.equalsIgnoreCase(areaType)) {
            name = "标记:";
        }

        return name;
    }

    //用来判断配置的围栏是进去的还是出去的
    private boolean checkisarea(String alarmtype, boolean isInArea) {
        boolean arg = false;
        if (!StringUtil.isNullOrEmpty(alarmtype)) {//这个地方是判断是进出围栏还是怎么的
            if (isInArea) {
                if (alarmtype.indexOf("进区域报警给平台") > -1) {
                    arg = true;
                }
            } else {
                if (alarmtype.indexOf("出区域报警给平台") > -1) {
                    arg = true;
                }
            }
        }
        return arg;
    }

    //用来判断配置的围栏是进去的还是出去的,是否下发给驾驶员
    private boolean checkisareatodriver(String alarmtype, boolean isInArea) {
        boolean arg = false;
        if (!StringUtil.isNullOrEmpty(alarmtype)) {//这个地方是判断是进出围栏还是怎么的
            if (isInArea) {
                if (alarmtype.indexOf("进区域报警给驾驶员") > -1) {
                    arg = true;
                }
            } else {
                if (alarmtype.indexOf("出区域报警给驾驶员") > -1) {
                    arg = true;
                }
            }
        }
        return arg;
    }


    public static void main(String[] args) {
        System.out.println(new AreaAlarmService().checkisarea("出区域报警给平台,", false));
    }

    /**
     * 创建围栏进出记录
     *
     * @param alarmSource
     * @param alarmType
     * @param alarmState
     * @param rd
     * @param ec
     */
    private void CreateAlarmRecord(String alarmSource, String alarmType,
                                   String alarmState, GPSRealData rd, MapArea ec) {
//        AlarmRecord sr = CreateRecord(alarmSource, alarmType, alarmState, rd,
//                ec.getEntityId());
//        if (sr != null && ec != null) {
//            sr.setStation(ec.getEntityId());
//            sr.setLocation(ec.getName());
//        }
//        if (sr != null)
//            alarmRecordService.saveOrUpdate(sr);
    }

    private AlarmRecord CreateRecord(String alarmSource, String alarmType,
                                     String alarmState, GPSRealData rd, long stationId) {
        String hsql = "select * from AlarmRecord rec where rec.plateNo = ? and rec.status = ? and rec.alarmSource = ? and rec.alarmType = ? and station = ?";
        // 创建报警记录
        AlarmRecord sr = (AlarmRecord) alarmRecordService.find(hsql,
                new Object[]{rd.getPlateNo(), AlarmRecord.STATUS_NEW,
                        alarmSource, alarmType, stationId});

        if (sr == null) {
            if (TURN_OFF.equals(alarmState))
                return null;

            sr = new AlarmRecord();
            sr.setVehicleId(rd.getVehicleId());
            sr.setPlateNo(rd.getPlateNo());
            sr.setStartTime(rd.getSendTime());
            sr.setStatus(AlarmRecord.STATUS_NEW);
            sr.setEndTime(new Date());
            sr.setLatitude(rd.getLatitude());
            sr.setLongitude(rd.getLongitude());
            sr.setVelocity(rd.getVelocity());
        } else {
            sr.setEndTime(new Date());
            double minutes = DateUtil.getSeconds(sr.getStartTime(),
                    sr.getEndTime()) / 60;
            sr.setTimeSpan(minutes);
            if (alarmState.equals(TURN_OFF)) {
                sr.setStatus(AlarmRecord.STATUS_OLD);
                sr.setEndTime(rd.getSendTime());

                sr.setLatitude1(rd.getLatitude());
                sr.setLongitude1(rd.getLongitude());
            } else
                return null;
        }

        sr.setAlarmSource(alarmSource);
        sr.setAlarmType(alarmType);
        return sr;
    }

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    /**
     * 生成区域报警记录
     *
     * @param alarmType
     * @param alarmSource
     * @param rd
     */
    private void insertAlarm(String alarmSource, String alarmType,
                             GPSRealData rd, String areaName) {
        rd.setLocation("电子围栏:" + areaName);
        Alarm alarm = this.newAlarmService.insertAlarm(alarmSource, alarmType, rd, "AreaAlarmService");
        AreaAlarmEvent areaAlarmEvent = new AreaAlarmEvent();
        BeanUtils.copyProperties(alarm, areaAlarmEvent);
        areaAlarmEvent.setDescr(areaName);
        EventMsg em = new EventMsg();
        em.setEventBody(areaAlarmEvent);
        em.loadDefaultDevMsgAttr();
        kafkaMessageSender.sendAreaAlarmEventMsg(em, rd.getSimNo());
    }


    /**
     * 分析是否在报警区域内
     *
     * @param rd
     * @param ec
     */
    private void AnalyzeAreaAlarm(GPSRealData rd, MapArea ec) {

        try {
            double lat = rd.getLatitude();
            double lng = rd.getLongitude();

            PointLatLng mp = null;
            PointLatLng pointForGoogle = null;
            PointLatLng pointForBaidu = null;

            if (StringUtil.isNullOrEmpty(ec.getPoints()))
                return;

            //根据区域的地图类型，将GPS终端的坐标转成对应的地图类型，进行比对判断
            if (Constants.MAP_BAIDU.equals(ec.getMapType())) {
                if (pointForBaidu == null) {
                    pointForBaidu = MapFixService
                            .fix(lat, lng, Constants.MAP_BAIDU);
                }
                mp = pointForBaidu;
            } else if (Constants.MAP_GPS.equals(ec.getMapType())) {
                mp = new PointLatLng(lng, lat);
            } else {
                if (pointForGoogle == null) {
                    pointForGoogle = MapFixService.fix(lat, lng,
                            Constants.MAP_GOOGLE);
                }
                mp = pointForGoogle;
            }

            boolean isbytime = true;
            if (ec.getByTime()) {//时间范围，如果不在时间范围那么就不进行围栏判断
                try {
                    if (!TimeUtils.isEffectiveDate(rd.getSendTime(), ec.getStartDate(), ec.getEndDate())) {
                        isbytime = false;
                    }
                } catch (Exception e) {
                    isbytime = false;
                }
            }
            if (isbytime) {
                if (MapArea.ROUTE.equals(ec.getAreaType())) {
                    AnalyzeOffsetRoute(rd, ec, mp);
                } else {
                    if (ec.getKeyPoint() == 1) {//这边计算关键点，但是不准确而且和圆是一样的
                        if (ec.getByTime())
                            monitorKeyPointArrvie(rd, ec, mp);// 进入指定的关键点报警
                        else
                            monitorKeyPointLeave(rd, ec, mp); // 离开指定的关键点报警
                    } else {//这边计算进区域
                        String key = rd.getSimNo() + "_" + ec.getEntityId();
                        boolean arg = true;
                        boolean inArea = IsInArea(ec, mp);//这边进行计算是否在区域内
                        areaGpsRealDataService.checkAreaGpsRealData(inArea,rd.getVehicleId(),ec.getEntityId());
                        if (CrossMap.containsKey(key)) {
                            if (inArea == CrossMap.get(key)) {//如果进去和出去和之前报警的相同，那么就不产生报警
                                arg = false;
                            }
                        } else {//如果不存在，也就是第一次的时候不报警，直到他触发相反情况
                            arg = false;
                        }
                        if (arg) {
                            CrossBorder(ec, rd, inArea);
                        }
                        log.debug("当前车辆的围栏处理情况为，simno=" + rd.getSimNo() + ",sendTime=" + rd.getSendTime() + ",name=" + ec.getName() + ",之前是否在围栏内=" + CrossMap.get(key) + ",现在是否在围栏内=" + inArea);
                        CrossMap.put(key, inArea);
                    }
                }
            } else {
                log.debug("当前车辆的围栏处理情况为，simno=" + rd.getSimNo() + ",sendTime=" + rd.getSendTime() + ",name=" + ec.getName() + ",不在时间范围内=" + TimeUtils.dateTodetailStr(ec.getStartDate()) + "-" + TimeUtils.dateTodetailStr(ec.getEndDate()));
            }
        } catch (Exception e) {
            log.error("处理围栏报错" + rd.getSimNo() + "+" + ec.getName(), e);
        }
    }

    // 分析是否按时到达关键点
    private void monitorKeyPointArrvie(GPSRealData rd, MapArea ec,
                                       PointLatLng mp) {
        Date now = new Date();
        String alarmType = AlarmRecord.TYPE_ARRIVE_NOT_ON_TIME;
        String alarmSource = AlarmRecord.ALARM_FROM_PLATFORM;
        String key = rd.getPlateNo() + "_" + ec.getEntityId() + "_" + alarmType;
        AlarmItem item = keyPlaceAlarmMap.get(key);

        if (now.compareTo(ec.getEndDate()) <= 0) {
            if (item == null) {
                boolean isInArea = IsInArea(ec, mp);
                if (isInArea) {
                    log.debug("到达关键点:" + ec.getName());
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    keyPlaceAlarmMap.put(key, item);
                }
            }
            //rd.setArriveKeyPlaceAlarm(null);
        } else if (now.compareTo(ec.getEndDate()) > 0) {
            if (item == null) {
                boolean inEnclosure = IsInArea(ec, mp);
                if (inEnclosure == false) {
                    this.insertAlarm(alarmSource, alarmType, rd, getAreaType(ec.getAreaType()) + ec.getName());
//                    rd.setArriveKeyPlaceAlarm("关键点:" + ec.getName());
                } else {
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    item.setStatus(AlarmRecord.STATUS_OLD);
                    keyPlaceAlarmMap.put(key, item);
//                    rd.setArriveKeyPlaceAlarm(null);
                }
            }
        }

    }

    /**
     * 关键点进入或者离开报警
     *
     * @param rd
     * @param ec
     * @param mp
     */
    private void monitorKeyPointLeave(GPSRealData rd, MapArea ec, PointLatLng mp) {
        Date now = new Date();
        String alarmSource = AlarmRecord.ALARM_FROM_PLATFORM;
        String alarmType = AlarmRecord.TYPE_LEAVE_NOT_ON_TIME;
        String key = rd.getPlateNo() + "_" + ec.getEntityId() + "_" + alarmType;
        AlarmItem item = keyPlaceAlarmMap.get(key);
        if (now.compareTo(ec.getStartDate()) < 0
                || now.compareTo(ec.getEndDate()) > 0) {
            if (item == null) {
                // 判断是否到达
                boolean inArea = IsInArea(ec, mp);
                if (inArea) {
                    this.insertAlarm(alarmSource, alarmType, rd, ec.getName());
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    keyPlaceAlarmMap.put(key, item);
//                    rd.setLeaveKeyPlaceAlarm("关键点:" + ec.getName());
                }
            } else if (item.getStatus().equals(AlarmRecord.STATUS_NEW)) {
                boolean inArea = IsInArea(ec, mp);
                if (inArea == false) {
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    item.setStatus(AlarmRecord.STATUS_OLD);
                    keyPlaceAlarmMap.put(key, item);
//                    rd.setArriveKeyPlaceAlarm(null);
                }
            }
        }

    }

    /**
     * 判断坐标点是否在区域当中
     *
     * @param ec
     * @param mp
     * @return
     */
    private boolean IsInArea(MapArea ec, PointLatLng mp) {

        String points1 = ec.getPoints();
        if (MapArea.DIVISION.equalsIgnoreCase(ec.getAreaType())) {//如果是行政区域
            String[] pointsquyu = points1.split("\\|");
            for (String s : pointsquyu) {
                List<PointLatLng> points = GetPoints(s);
                if(points.size()>2){
                    if (MapFixService.IsInPolygon(mp, points)) {
                        return true;
                    }
                }
            }
        } else {
            List<PointLatLng> points = GetPoints(points1);
            //如果是多边形，或者是行政区域
            if (MapArea.POLYGON.equalsIgnoreCase(ec.getAreaType()) && points.size() > 2) {
                if (MapFixService.IsInPolygon(mp, points)) {
                    Date end = new Date();
                    return true;
                }
            } else if (MapArea.CIRCLE.equals(ec.getAreaType()) && points.size() > 0) {
                PointLatLng pl = points.get(0);
                double radius = ec.getRadius();
                if (MapFixService.IsInCircle(mp, pl, radius))
                    return true;
            } else if (MapArea.RECT.equals(ec.getAreaType()) && points.size() > 1) {
                PointLatLng p1 = points.get(0);
                PointLatLng p2 = points.get(2);

                if (MapFixService.IsInRect(mp.lng, mp.lat, p1.lng, p1.lat, p2.lng,
                        p2.lat))
                    return true;
            }
        }
        return false;
    }


    private List<PointLatLng> GetPoints(String strPoints) {
        List<PointLatLng> results = new ArrayList<PointLatLng>();

        String[] strPts = strPoints.split(";");
        for (String strPt : strPts) {
            if (StringUtil.isNullOrEmpty(strPt) == false) {
                String[] strPoint = strPt.split(",");
                if (strPoint.length == 2) {
                    PointLatLng pl = new PointLatLng(
                            Double.parseDouble(strPoint[0]),
                            Double.parseDouble(strPoint[1]));
                    results.add(pl);
                }
            }
        }
        return results;
    }


    

}
