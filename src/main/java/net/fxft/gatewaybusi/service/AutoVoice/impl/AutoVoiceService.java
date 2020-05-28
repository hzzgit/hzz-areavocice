package net.fxft.gatewaybusi.service.AutoVoice.impl;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.util.ConverterUtils;
import com.ltmonitor.util.StringUtil;
import com.ltmonitor.util.TimeUtils;
import net.fxft.common.log.AttrLog;
import net.fxft.common.util.BasicUtil;
import net.fxft.gatewaybusi.po.autovoice.AutoVoiceConfigPO;
import net.fxft.gatewaybusi.po.autovoice.AutoVoicePO;
import net.fxft.gatewaybusi.po.autovoice.AutoVoiceRealPO;
import net.fxft.gatewaybusi.service.AutoVoice.IAutoVoiceService;
import net.fxft.gatewaybusi.service.impl.RealDataService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//语音播放主线程处理的服务层
@Service
public class AutoVoiceService implements IAutoVoiceService {

    private static final Logger log = LoggerFactory.getLogger(AutoVoiceService.class);

    //用来记录上一个点的设备的acc状态，以及定位的时间,用来进行做对比
    private Map<String, AutoVoiceRealPO> autoRealMap = new ConcurrentHashMap<>();
    //获取实时数据的类
    @Autowired
    private RealDataService realDataService;

    @Autowired
    private AutoVoiceConfigService autoVoiceConfigService;

    //数据发送队列线程
    @Autowired
    private AutoVoiceQueueService autoVoiceQueueService;

    @Value("${autoVoice}")
    private boolean autoVoice;

    @PostConstruct
    public void init() {

        if (autoVoice) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        initrealdata();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        checkoffline();
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    //用来检测是否有离线和熄火的情况
    private void checkoffline() {
        AttrLog alog = AttrLog.get("[语音播报]检测在线设备缓存是否离线");
        try {
            if (autoRealMap != null && autoRealMap.size() > 0) {
                autoRealMap.forEach((p, v) -> {
                    if (v.isIsacc()) {//acc是开的时候才判断是否关闭
                        GPSRealData rd = realDataService.getGpsRealData(p);
                        if(rd!=null) {
                            if (rd.isOnline() == false) {//如果有一个下线了，这边也要变成false
                                v.setIsacc(false);
                                v.setConfigTime(new HashMap<>());
                            } else {
                                if (!isacc(rd.getStatus())) {//且必须acc关才更新
                                    v.setIsacc(false);
                                    v.setConfigTime(new HashMap<>());
                                }
                            }
                        }else{
                            log.debug("语音播报中移除缓存simno"+p);
                            v.setIsacc(false);
                            v.setConfigTime(new HashMap<>());
                        }
                    }
                });
            }
        } catch (Exception e) {
            alog.log("出现异常", BasicUtil.exceptionMsg(e));
        } finally {
            log.debug(alog.toString());

        }
    }

    //用户检测是否有新的配置加入，
    private void initrealdata() {
        AttrLog alog = AttrLog.get("同步实时播报的实时数据的历史情况");
        try {
            Map<String, AutoVoicePO> allconfig = autoVoiceConfigService.getAutovoiceconfig();
            if (allconfig != null && allconfig.size() > 0) {
                allconfig.forEach((p, v) -> {
                    if (!autoRealMap.containsKey(p)) {//不存在那么就写入一个离线
                            AutoVoiceRealPO autoVoiceRealPO = new AutoVoiceRealPO();
                            autoVoiceRealPO.setIsacc(false);
                            autoVoiceRealPO.setOnlineDate(new Date());
                            autoVoiceRealPO.setConfigTime(new HashMap<>());
                            autoRealMap.put(p, autoVoiceRealPO);
                    }
                });
            }
        } catch (Exception e) {
            alog.log("出现异常", BasicUtil.exceptionMsg(e));
        } finally {
            log.debug(alog.toString());
        }
    }

    //根据截取到的实时数据进行一系列的处理，主要处理逻辑
    public void autoVoiceMain(GPSRealData rd) {
        if (autoVoice) {
            AttrLog alog = AttrLog.get("语音播报主方法")
                    .log("GPSRealData", rd.toString());
            long s=System.currentTimeMillis();   //获取开始时间

            try {
                String simNo = rd.getSimNo();
                if (autoVoiceConfigService.isAutoVoice(simNo)) {//必须存在这个配置，才可以继续下一步的操作
                    AutoVoicePO autoVoicePO = autoVoiceConfigService.getAutoVoice(simNo);//获取到这个设备的语音播报配置情况
                    Date startTime = autoVoicePO.getStartTime();//配置生效的开始时间
                    Date endTime = autoVoicePO.getEndTime();//配置生效的结束时间，
                    if (autoVoicePO.getIsuse() == 1 && TimeUtils.isEffectiveDate(new Date(), startTime, endTime)) {//必须是启动了这个才能够生效
                        List<AutoVoiceConfigPO> autoVoiceConfigPOS = autoVoicePO.getAutoVoiceConfigPOS();
                        Date onlineDate = rd.getUpdateDate();//获取到定位的时间
                        boolean online = rd.isOnline();

                        String status = rd.getStatus();
                        boolean isacc = false;
                        if (online) {
                            isacc = isacc(status);//获取到当前设备的acc状态，
                        }
                        if (autoRealMap.containsKey(simNo)) {//如果有存在，那么就要进行时间的对比并进行语音播报
                            AutoVoiceRealPO autoVoiceRealPO = new AutoVoiceRealPO();
                            autoVoiceRealPO = autoRealMap.get(simNo);
                            if (isacc) {//如果这个点位的acc是开的
                                boolean isaccold = autoVoiceRealPO.isIsacc();//原来的acc状态
                                if (isaccold == false) {//如果原来的acc状态是关的，那么就代表是第一次点火
                                    //因为这次acc是开，且上次acc是关，所以进行一次时间的更新，用于之后的持续时间判断
                                    AutoVoiceRealPO autoVoiceRealPO2 = new AutoVoiceRealPO();
                                    autoVoiceRealPO2.setIsacc(true);
                                    autoVoiceRealPO2.setOnlineDate(onlineDate);
                                    autoRealMap.put(simNo, autoVoiceRealPO2);
                                    boolean arg = false;
                                    if (ConverterUtils.isList(autoVoiceConfigPOS)) {
                                        List<AutoVoiceConfigPO> autoVoiceConfigPOList=new ArrayList<>();
                                        for (AutoVoiceConfigPO autoVoiceConfigPO : autoVoiceConfigPOS) {
                                            if (autoVoiceConfigPO.getType() == 1) {
                                                arg = true;
                                                autoVoiceConfigPOList.add(autoVoiceConfigPO);
                                            }
                                        }
                                        if (arg) {//如果有要求第一次生效，那么这个时候代表着判断通过，就进行语音播报
                                            if(ConverterUtils.isList(autoVoiceConfigPOList)){
                                                for (AutoVoiceConfigPO autoVoiceConfigPO : autoVoiceConfigPOList) {
                                                    autoVoiceQueueService.addSendQueue(autoVoiceConfigPO.getSendContent(), simNo);
                                                }
                                            }
                                        }
                                    }
                                } else {//如果是开的。说明要进行计时操作，当多少时间之后才会进行一次判断是否达到了语音播报的情况
                                    if (autoVoiceRealPO.isIsacc()) {//且上一次acc是开的情况下，才进行持续时间的播报操作
                                        if (ConverterUtils.isList(autoVoiceConfigPOS)) {
                                            AutoVoiceRealPO autoVoiceRealPO3 = autoRealMap.get(simNo);
                                            Map<Integer, Date> configTime = autoVoiceRealPO3.getConfigTime();
                                            List<Integer> cunzaiid = new ArrayList<>();
                                            for (AutoVoiceConfigPO autoVoiceConfigPO : autoVoiceConfigPOS) {
                                                if (autoVoiceConfigPO.getType() == 2) {
                                                    int sendInterval = autoVoiceConfigPO.getSendInterval();//获取到时间间隔的分钟数
                                                    if (!configTime.containsKey(autoVoiceConfigPO.getId())) {//如果不存在这个时间播放的配置，那么就根据第一次上线时间判断
                                                        if (TimeUtils.isdifferminute(autoVoiceRealPO.getOnlineDate(), onlineDate, sendInterval)) {//如果时间间隔在配置的时间间隔之内，那么就下发语音播报
                                                            autoVoiceQueueService.addSendQueue(autoVoiceConfigPO.getSendContent(), simNo);
                                                            configTime.put(autoVoiceConfigPO.getId(), new Date());
                                                            autoVoiceRealPO3.setConfigTime(configTime);
                                                        }
                                                    } else {//如果存在这个播放的配置，那么就根据上一次这个播放配置的时间进行判断
                                                        Date lasttime = configTime.get(autoVoiceConfigPO.getId());
                                                        if (TimeUtils.isdifferminute(lasttime, onlineDate, sendInterval)) {//如果时间间隔在配置的时间间隔之内，那么就下发语音播报
                                                            autoVoiceQueueService.addSendQueue(autoVoiceConfigPO.getSendContent(), simNo);
                                                            configTime.put(autoVoiceConfigPO.getId(), new Date());
                                                            autoVoiceRealPO3.setConfigTime(configTime);
                                                        }
                                                    }
                                                    cunzaiid.add(autoVoiceConfigPO.getId());
                                                }
                                            }
                                            if (configTime != null && configTime.size() > 0) {
                                                List<Integer> removeId = new ArrayList<>();
                                                configTime.forEach((p, v) -> {
                                                    if (!cunzaiid.contains(p)) {
                                                        removeId.add(p);
                                                    }
                                                });
                                                if (ConverterUtils.isList(removeId)) {
                                                    for (Integer integer : removeId) {
                                                        configTime.remove(integer);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                            } else {//如果acc是关的，那么也进行更新记录
                                autoVoiceRealPO = new AutoVoiceRealPO();
                                autoVoiceRealPO.setIsacc(isacc);
                                autoVoiceRealPO.setOnlineDate(new Date());
                                autoVoiceRealPO.setConfigTime(new HashMap<>());
                                autoRealMap.put(simNo, autoVoiceRealPO);
                            }
                        } else {//如果不存在，说明这个设备是第一次进行记录这个语音播报
                            if (isacc == false) {//acc关了才进行记录
                                AutoVoiceRealPO autoVoiceRealPO = new AutoVoiceRealPO();
                                autoVoiceRealPO.setIsacc(isacc);
                                autoVoiceRealPO.setOnlineDate(new Date());
                                autoRealMap.put(simNo, autoVoiceRealPO);
                            }
                        }
                    }
                    if(autoVoicePO.getIsuse() == 0){//如果没有启动也移除
                        autoRealMap.remove(simNo);
                    }
                }else{
                    autoRealMap.remove(simNo);
                }
            } catch (Exception e) {
                alog.log("出现异常", BasicUtil.exceptionMsg(e));
            } finally {
                log.debug(alog.toString());
            }
        }
    }

    /**
     * 判断acc开关
     *
     * @param status
     * @return
     */
    protected boolean isacc(String status) {
        StringBuilder sb = new StringBuilder();
        boolean arg = false;
        if (StringUtil.isNullOrEmpty(status) == false) {
            char[] ch = status.toCharArray();
            if (ch.length == 32) {
                int m = 31;
                int c = ch[m - 0] - 48;
                if (c == 1) {
                    arg = true;
                } else {
                    arg = false;
                }
            }
        }
        return arg;
    }

    public static void main(String[] args) {

        String status="513";
        StringBuilder sb = new StringBuilder();
        boolean arg = false;
        if (StringUtil.isNullOrEmpty(status) == false) {
            char[] ch = status.toCharArray();
            if (ch.length == 32) {
                int m = 31;
                int c = ch[m - 0] - 48;
                if (c == 1) {
                    arg = true;
                } else {
                    arg = false;
                }
            }
        }
        System.out.println(arg);

    }


}
