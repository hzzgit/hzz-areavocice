package net.fxft.gatewaybusi.service.MapArea;

import com.ltmonitor.entity.*;

import com.ltmonitor.service.*;
import com.ltmonitor.util.*;
import com.ltmonitor.vo.PointLatLng;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.RowDataMap;
import net.fxft.common.log.AttrLog;
import net.fxft.common.util.BasicUtil;
import net.fxft.gatewaybusi.service.IRealDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

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
    @Autowired
    private IAlarmConfigService alarmConfigService;


    @Resource
    private IRealDataService realDataService;



    /**
     * 报警分析线程，单独开辟一个线程进行分析
     */
    private Thread analyzeThread;

    private volatile boolean continueAnalyze = true;

    private ConcurrentMap<String, AlarmItem> offsetRouteWarn = new ConcurrentHashMap();

    private ConcurrentMap<String, AlarmItem> onRouteWarn = new ConcurrentHashMap();
    // private Map routePointMap = new HashMap();
    /**
     * 当前的关键点报警
     */
    private ConcurrentMap<String, AlarmItem> keyPlaceAlarmMap = new ConcurrentHashMap<String, AlarmItem>();

    Map<String, GPSRealData> realDataMap = new HashMap<String, GPSRealData>();
    private ConcurrentMap<String, AlarmItem> areaAlarmMap = new ConcurrentHashMap<String, AlarmItem>();
    private ConcurrentMap<String, AlarmItem> overSpeedAlarmMap = new ConcurrentHashMap<String, AlarmItem>();

    private ConcurrentMap<String, Boolean> CrossMap = new ConcurrentHashMap<>();//用来判断进出围栏的情况，如果已经进入围栏则不再报警，直到出去之后

    private ConcurrentMap<String, List<Integer>> AreaConfigMap = new ConcurrentHashMap<>();//当前围栏报警的车辆配置信息

    //处理实时数据的队列
    private ConcurrentLinkedQueue<GPSRealData> AreaQueue = new ConcurrentLinkedQueue();

    //添加到队列
    public void addAreaqueue(GPSRealData rd) {
        if (AreaConfigMap.containsKey(rd.getSimNo())) {//如果存在这个围栏配置就加入队列进行运算
            AreaQueue.add(rd);
        }
    }

    @PostConstruct
    public void start() {

            analyzeThread = new Thread(new Runnable() {
                public void run() {
                    analyzeThreadFunc();
                }
            });
            analyzeThread.start();
            for (int i = 0; i < areaQueueThreadco; i++) {//开三个线程处理有围栏报警的设备队列
                new Thread(() -> {//处理围栏报警队列的线程
                    processorAreaAlarm();
                }).start();
            }

    }

    //处理围栏报警判断
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


    //十秒缓存一次围栏配置信息
    private void analyzeThreadFunc() {
        while (continueAnalyze) {
            try {
                AreaConfigThread();
                log.debug("围栏处理队列剩余"+AreaQueue.size());
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
            try {
                if (continueAnalyze) {
                    Thread.sleep(5000L);
                }
            } catch (InterruptedException e1) {
            }
        }
    }

    @PreDestroy
    public void stopService() {
        log.info("---begin stop AreaAlarmService---");
        continueAnalyze = false;
        if (analyzeThread == null)
            return;
        try {
            analyzeThread.interrupt();
//			analyzeThread.join(2000);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        log.info("---end stop AreaAlarmService---");

    }

    //缓存围栏配置信息
    private void AreaConfigThread() {
        AttrLog alog = AttrLog.get("缓存围栏配置信息");
        try {
            long s = System.currentTimeMillis();   //获取开始时间
            Map<String, Integer> areaBindingMap = new HashMap<String, Integer>();
            String sql="\t\tselect v.plateNo,b.areaId,v.simNo from MapAreaBinding b\n" +
                    "\t\tleft join vehicle v  on b.vehicleId=v.vehicleId\n" +
                    "\t\twhere 1=1  and b.bindType = 'platform' and v.deleted=false\n" +
                    "\t\t\tand b.configType !=3";

            List<RowDataMap> bindings = JdbcUtil.getDefault().sql(sql).queryWithMap();
            ConcurrentMap<String, List<Integer>> AreaConfigMap1 = new ConcurrentHashMap<>();
            if (ConverterUtils.isList(bindings)) {
                for (RowDataMap binding : bindings) {
                    String plateNo = ConverterUtils.toString(binding.get("plateNo"));
                    String simNo = ConverterUtils.toString(binding.get("simNo"));
                    int areaId = ConverterUtils.toInt(binding.get("areaId"));
                    areaBindingMap.put(plateNo + "_" + areaId, areaId);
                    List<Integer> integerList = new ArrayList<>();
                    if (AreaConfigMap1.containsKey(simNo)) {//如果存在就新增
                        integerList = AreaConfigMap1.get(simNo);
                    }
                    integerList.add(areaId);
                    AreaConfigMap1.put(simNo, integerList);
                }
                AreaConfigMap = AreaConfigMap1;
            }
            Set<String> keys = offsetRouteWarn.keySet();
            for (String key : keys) {
                if (areaBindingMap.containsKey(key) == false) {
                    //取消绑定，该报警结束;
                    AlarmItem ai = offsetRouteWarn.get(key);
                    if (ai != null && ai.getSimNo() != null) {
                        GPSRealData rd = this.realDataService.get(ai.getSimNo());
                        rd.setOffsetRouteAlarm(null);

                        log.error(rd.getPlateNo() + "路线偏移报警关闭:" + key);
                    }
                }
            }
            keys = this.areaAlarmMap.keySet();
            for (String key : keys) {
                if (areaBindingMap.containsKey(key) == false) {
                    //取消绑定，该报警结束;
                    AlarmItem ai = this.areaAlarmMap.get(key);
                    if (ai != null && ai.getSimNo() != null) {
                        areaAlarmMap.remove(key);
                        GPSRealData rd = this.realDataService.get(ai.getSimNo());
                        rd.setMapAreaAlarm(null);
                        log.error(rd.getPlateNo() + "区域报警关闭:" + key);
                    }
                }
            }
            keys = this.keyPlaceAlarmMap.keySet();
            for (String key : keys) {
                if (areaBindingMap.containsKey(key) == false) {
                    //取消绑定，该报警结束;
                    AlarmItem ai = this.keyPlaceAlarmMap.get(key);
                    if (ai != null && ai.getSimNo() != null) {
                        keyPlaceAlarmMap.remove(key);
                        GPSRealData rd = this.realDataService.get(ai.getSimNo());
                        if (AlarmRecord.TYPE_ARRIVE_NOT_ON_TIME.equals(ai.getAlarmType()))
                            rd.setArriveKeyPlaceAlarm(null);
                        else
                            rd.setLeaveKeyPlaceAlarm(null);

                        log.error(rd.getPlateNo() + "关键点报警关闭:" + key);
                    }
                }
            }
            keys = this.overSpeedAlarmMap.keySet();
            for (String key : keys) {
                if (areaBindingMap.containsKey(key) == false) {
                    //取消绑定，该报警结束;
                    AlarmItem ai = this.overSpeedAlarmMap.get(key);
                    if (ai != null && ai.getSimNo() != null) {
                        overSpeedAlarmMap.remove(key);
                        GPSRealData rd = this.realDataService.get(ai.getSimNo());
                        rd.setOverSpeedAlarm(null);
                        log.error(rd.getPlateNo() + "分段限速报警关闭:" + key);
                    }
                }
            }
            long e = System.currentTimeMillis(); //获取结束时间
            log.debug(alog.toString() + "用时：" + (e - s) + "ms");
        } catch (Exception e) {
            alog.log("出现异常", BasicUtil.exceptionMsg(e));
            log.error(alog.toString());
        }
    }

    public void analyze(List<Integer> AreaId, GPSRealData rd) {
        AttrLog alog = AttrLog.get("处理单个设备的围栏报警")
                .log("simNo", rd.getSimNo())
                .log("areaIds", AreaId);
        try {
            if (ConverterUtils.isList(AreaId)) {
                for (Integer areaId : AreaId) {
                    long s = System.currentTimeMillis();   //获取开始时间
                    String key = areaId + rd.getSimNo();
                    if (rd != null) {
                        rd.setOffsetRouteAlarm(null);
                        rd.setMapAreaAlarm(null);
                        rd.setArriveKeyPlaceAlarm(null);
                        rd.setLeaveKeyPlaceAlarm(null);
                        rd.setOverSpeedAlarm(null);
                        GPSRealData oldRd = realDataMap.get(key);
                        if (oldRd != null) {
                            if (oldRd.getLatitude() == rd.getLatitude()
                                    && oldRd.getLongitude() == rd.getLongitude()) {
                                return;
                            }
                        } else {
                            oldRd = new GPSRealData();
                        }
                        oldRd.setLatitude(rd.getLatitude());
                        oldRd.setLongitude(rd.getLongitude());
                        realDataMap.put(key, oldRd);
                        String hql = "from MapArea where areaId = ? and Deleted = false";
                        MapArea ec = (MapArea) this.mapAreaService.find(hql, areaId);
                        if (ec != null) {
                            ec.setStartDate(TimeUtils.todatetime(TimeUtils.dateTodetailStr(ec.getStartDate())));;
                            ec.setEndDate(TimeUtils.todatetime(TimeUtils.dateTodetailStr(ec.getEndDate())));;
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

        String key = rd.getPlateNo();
        AlarmItem alarmItem = overSpeedAlarmMap.get(key);

        if (seg == null) {
            //如果没在线路上，则不再分析分段超速
            if (alarmItem != null) {
                this.overSpeedAlarmMap.remove(key);
                CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                        AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_OFF, rd,
                        alarmItem.getAlarmId(), null);
                //关闭分段超速报警
                rd.setOverSpeedAlarm(null);
            }
            return;
        }
        log.error("路线名称:" + ec.getName() + ",线段Id:" + seg.getPointId());
        RouteSegment rs = this.isInLimitSpeedRouteSegment(seg.getPointId(), ec);
        if (rs == null)
            return;

        log.error("路线名称·:" + ec.getName() + ",分段:" + rs.getName() + "速度:"
                + rd.getVelocity());
        // 超速
        if (rd.getVelocity() > rs.getMaxSpeed()) {
            double alarmDelay = 0;
            if (alarmItem != null) {
                Date offsetTime = alarmItem.getAlarmTime();
                alarmDelay = DateUtil.getSeconds(offsetTime, rd.getSendTime());

            } else {
                alarmItem = new AlarmItem(rd, AlarmRecord.ALARM_FROM_PLATFORM,
                        AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE);
                alarmItem.setStatus(AlarmRecord.STATUS_NEW);
                this.overSpeedAlarmMap.put(key, alarmItem);
            }
            if (alarmDelay >= rs.getDelay()) {
                log.error("开始分段超速报警，路线名称:" + ec.getName() + ",分段:"
                        + rs.getName() + "速度:" + rd.getVelocity());
                insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                        AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, rd, ec.getName()
                                + ",分段名称:" + rs.getName());
                rd.setOverSpeedAlarm("路线名称:" + ec.getName() + ",分段:"
                        + rs.getName() + "速度:" + rd.getVelocity());
                if (AlarmRecord.STATUS_NEW.equals(alarmItem.getStatus())) {
                    //创建分段超速报警
                    CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_ON, rd,
                            seg.getEntityId(),
                            ec.getName() + ",分段名称:" + seg.getName());
                    alarmItem.setStatus(AlarmRecord.STATUS_OLD);
                    // this.overSpeedAlarmMap.put(key, rs);
                }
            }

        } else {
            if (alarmItem != null) {
                // 报警结束时，更新报警状态，移除内存
                this.overSpeedAlarmMap.remove(key);
                CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                        AlarmRecord.TYPE_OVER_SPEED_ON_ROUTE, TURN_OFF, rd,
                        seg.getEntityId(), null);
            }
            rd.setOverSpeedAlarm(null);
        }
    }

    /**
     * 路线偏移报警分析
     *
     * @param rd
     * @param ec
     * @param mp
     */
    private void AnalyzeOffsetRoute(GPSRealData rd, MapArea ec, PointLatLng mp) {
        Date start = new Date();
        String alarmType = AlarmRecord.TYPE_OFFSET_ROUTE;
        String alarmSource = AlarmRecord.ALARM_FROM_PLATFORM;
        int maxAllowedOffsetTime = ec.getOffsetDelay(); // 获得延时报警的延时值
        if (IsInTimeSpan(ec)) {
            // 是否在线路的某一段上
            LineSegment seg = IsInLineSegment(mp, ec);
            boolean isOnRoute = seg != null;
            log.error(rd.getPlateNo() + ",线路名称" + ec.getName() + "," + (seg == null ? "偏离" : "行驶在线路上") + rd.getSendTime());
            String alarmKey = rd.getPlateNo() + "_" + ec.getEntityId();
            AlarmItem offsetAlarm = (AlarmItem) offsetRouteWarn.get(alarmKey);
            AlarmItem onRouteAlarm = (AlarmItem) onRouteWarn.get(alarmKey);
            if (isOnRoute == false) {
                if (offsetAlarm == null) {
                    this.AnalyzeOverSpeed(rd, null, ec);
                    offsetAlarm = new AlarmItem(rd, alarmType, alarmSource);
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
                    this.insertAlarm(alarmSource, alarmType, rd, ec.getName());
                    rd.setOffsetRouteAlarm("偏离路线:" + ec.getName());
                    Date originTime = rd.getSendTime();
                    rd.setSendTime(offsetTime);
                    // 创建偏离报警
                    AlarmRecord sr = CreateRecord(
                            AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_OFFSET_ROUTE, TURN_ON, rd,
                            ec.getEntityId());
                    if (sr != null) {
                        sr.setStation(ec.getEntityId());
                        sr.setLocation(ec.getName());
                        alarmRecordService.saveOrUpdate(sr);
                    }
                    // 创建行驶在指定的线路上的记录
                    sr = CreateRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_ON_ROUTE, TURN_OFF, rd,
                            ec.getEntityId());
                    if (sr != null) {
                        sr.setStation(ec.getEntityId());
                        sr.setLocation(ec.getName());
                        alarmRecordService.saveOrUpdate(sr);
                    }
                    rd.setSendTime(originTime);
                    onRouteWarn.remove(alarmKey);
                }

            } else {

                AnalyzeOverSpeed(rd, seg, ec);//如果在线路上，就开始分析分段限速报警
                if (offsetAlarm != null) {
                    offsetAlarm.setStatus(AlarmRecord.STATUS_OLD);// 如果在线路上，说明偏离线路报警结束
                    log.info(rd.getPlateNo() + "回到线路上," + rd.getSendTime());
                    CreateWarnRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_OFFSET_ROUTE, TURN_OFF, rd,
                            ec.getEntityId(), null);
                    offsetRouteWarn.remove(alarmKey);
                }
                rd.setOffsetRouteAlarm(null);
                if (onRouteAlarm == null) {
                    onRouteAlarm = new AlarmItem(rd, AlarmRecord.TYPE_ON_ROUTE,
                            alarmSource);

                    onRouteWarn.put(alarmKey, onRouteAlarm);
                    AlarmRecord sr = CreateRecord(
                            AlarmRecord.ALARM_FROM_PLATFORM,
                            AlarmRecord.TYPE_ON_ROUTE, TURN_ON, rd,
                            ec.getEntityId());
                    if (sr != null) {
                        sr.setStation(ec.getEntityId());
                        sr.setLocation(ec.getName());
                        alarmRecordService.saveOrUpdate(sr);
                    }
                }
            }
        }

        double ts2 = DateUtil.getSeconds(start, new Date());
        if (ts2 > 0.2)
            log.info(rd.getPlateNo() + "," + ec.getName() + "路线偏移报警耗时:" + ts2);

    }

    /**
     * 创建报警记录
     */
    private void CreateWarnRecord(String OperateType, String alarmType,
                                  String warnState, GPSRealData rd, long areaId, String location) {
        AlarmRecord sr = CreateRecord(OperateType, alarmType, warnState, rd,
                areaId);
        if (sr != null) {
            if (location != null)
                sr.setLocation(location);
            alarmRecordService.saveOrUpdate(sr);
        }
    }

    //获取分段信息
    private RouteSegment isInLimitSpeedRouteSegment(long pointId, MapArea route) {
        long routeId = route.getEntityId();
        String hql = "from RouteSegment where routeId = ? and startSegId <= ? and endSegId >= ? ";

        RouteSegment rs = this.routeSegmentService.find(hql, new Object[]{
                routeId, pointId, pointId + 1});

        return rs;
    }

    private LineSegment IsInLineSegment(PointLatLng mp, MapArea ec) {
        List<LineSegment> segments = GetLineSegments(ec.getEntityId());
        LineSegment prevSegment = null;
        for (LineSegment seg : segments) {
            if (prevSegment != null) {
                PointLatLng p1 = new PointLatLng(prevSegment.getLongitude1(),
                        prevSegment.getLatitude1());
                PointLatLng p2 = new PointLatLng(seg.getLongitude1(),
                        seg.getLatitude1());

                boolean result = MapFixService.isPointOnRouteSegment(p1, p2,
                        mp, seg.getLineWidth());
                if (result)
                    return seg;
            }
            prevSegment = seg;
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
        String hsql = "from LineSegment where routeId = ? order by pointId ";
        List ls = lineSegmentService.query(hsql, new Object[]{routeId});
        return ls;
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
     * 判断进出区域的临界点
     *
     * @param ec
     * @param rd
     * @param isInArea
     */
    private void CrossBorder(MapArea ec, GPSRealData rd, boolean isInArea) {
        MapArea oldArea = getOldMapArea(rd.getPlateNo(), ec.getEntityId());

        if (isInArea) {
            if (this.alarmConfigService.isAlarmEnabled(AlarmRecord.TYPE_IN_AREA, AlarmRecord.ALARM_FROM_PLATFORM))
                rd.setMapAreaAlarm("进入区域:" + ec.getName());
        } else {
            if (this.alarmConfigService.isAlarmEnabled(AlarmRecord.TYPE_CROSS_BORDER, AlarmRecord.ALARM_FROM_PLATFORM))
                rd.setMapAreaAlarm("离开区域:" + ec.getName());
        }

        if (isInArea && oldArea == null) {
            insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_IN_AREA, rd, ec.getName());

            //如果是第一次进入，则创建进入围栏记录
            CreateAlarmRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_IN_AREA, TURN_ON, rd, ec);

            String key = rd.getPlateNo() + "_" + ec.getEntityId();
            AlarmItem ai = new AlarmItem(rd, AlarmRecord.TYPE_IN_AREA, AlarmRecord.ALARM_FROM_PLATFORM, ec);
            areaAlarmMap.put(key, ai);
        }

        //离开围栏
        if (isInArea == false && oldArea != null) {

            insertAlarm(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_CROSS_BORDER, rd, ec.getName());
            CreateAlarmRecord(AlarmRecord.ALARM_FROM_PLATFORM,
                    AlarmRecord.TYPE_IN_AREA, TURN_OFF, rd, ec);
            String key = rd.getPlateNo() + "_" + ec.getEntityId();
            areaAlarmMap.remove(key);// 离开围栏时，从内存中移除记录
        }

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
        AlarmRecord sr = CreateRecord(alarmSource, alarmType, alarmState, rd,
                ec.getEntityId());
        if (sr != null && ec != null) {
            sr.setStation(ec.getEntityId());
            sr.setLocation(ec.getName());
        }
        if (sr != null)
            alarmRecordService.saveOrUpdate(sr);
    }

    private AlarmRecord CreateRecord(String alarmSource, String alarmType,
                                     String alarmState, GPSRealData rd, long stationId) {
        String hsql = "from AlarmRecord rec where rec.plateNo = ? and rec.status = ? and rec.alarmSource = ? and rec.alarmType = ? and station = ?";
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
        this.newAlarmService.insertAlarm(alarmSource, alarmType, rd, "AreaAlarmService");
    }


    /**
     * 分析是否在报警区域内
     *
     * @param rd
     * @param ec
     */
    private void AnalyzeAreaAlarm(GPSRealData rd, MapArea ec) {

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

        if (MapArea.ROUTE.equals(ec.getAreaType())) {
            AnalyzeOffsetRoute(rd, ec, mp);
        } else {
            if (ec.getKeyPoint() == 1) {
                if (ec.getByTime())
                    monitorKeyPointArrvie(rd, ec, mp);// 进入指定的关键点报警
                else
                    monitorKeyPointLeave(rd, ec, mp); // 离开指定的关键点报警
            } else {
                boolean isbytime = true;
                if (ec.getByTime()) {//时间范围，如果不在时间范围那么就不进行围栏判断
                    if (!TimeUtils.isEffectiveDate(rd.getSendTime(), ec.getStartDate(), ec.getEndDate())) {
                        isbytime=false;
                    }
                }
                if (isbytime) {
                    String key = rd.getSimNo() + "_" + ec.getEntityId();
                    boolean arg = true;
                    boolean inArea = IsInArea(ec, mp);
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
                    CrossMap.put(key, inArea);
                }
            }
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
                    log.error("到达关键点:" + ec.getName());
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    keyPlaceAlarmMap.put(key, item);
                }
            }
            //rd.setArriveKeyPlaceAlarm(null);
        } else if (now.compareTo(ec.getEndDate()) > 0) {
            if (item == null) {
                boolean inEnclosure = IsInArea(ec, mp);
                if (inEnclosure == false) {
                    this.insertAlarm(alarmSource, alarmType, rd, ec.getName());
                    rd.setArriveKeyPlaceAlarm("关键点:" + ec.getName());
                } else {
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    item.setStatus(AlarmRecord.STATUS_OLD);
                    keyPlaceAlarmMap.put(key, item);
                    rd.setArriveKeyPlaceAlarm(null);
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

                    rd.setLeaveKeyPlaceAlarm("关键点:" + ec.getName());
                }
            } else if (item.getStatus().equals(AlarmRecord.STATUS_NEW)) {
                boolean inArea = IsInArea(ec, mp);
                if (inArea == false) {
                    item = new AlarmItem(rd, alarmType, alarmSource);
                    item.setStatus(AlarmRecord.STATUS_OLD);
                    keyPlaceAlarmMap.put(key, item);
                    rd.setArriveKeyPlaceAlarm(null);
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
        List<PointLatLng> points = GetPoints(ec.getPoints());

        if (MapArea.POLYGON.equals(ec.getAreaType()) && points.size() > 2) {
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
