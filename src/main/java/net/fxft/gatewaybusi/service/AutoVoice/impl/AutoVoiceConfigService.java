package net.fxft.gatewaybusi.service.AutoVoice.impl;


import com.ltmonitor.entity.TerminalCommand;
import com.ltmonitor.entity.VehicleData;
import com.ltmonitor.service.ITerminalCommandService;
import com.ltmonitor.service.JT808Constants;
import com.ltmonitor.util.ConverterUtils;
import com.ltmonitor.util.TimeUtils;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.RowDataMap;
import net.fxft.common.log.AttrLog;
import net.fxft.common.util.BasicUtil;
import net.fxft.gateway.util.SimNoUtil;
import net.fxft.gatewaybusi.po.autovoice.AutoVoiceConfigPO;
import net.fxft.gatewaybusi.po.autovoice.AutoVoicePO;
import net.fxft.gatewaybusi.service.impl.RealDataService;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//语音自动播放配置信息及下发命令的服务类
@Service
public class AutoVoiceConfigService {

    private static final Logger logger = LoggerFactory.getLogger(AutoVoiceConfigService.class);

    @Autowired
    private JdbcUtil jdbcUtil;

    @Autowired
    private RedisUtil redisUtil;
    /**
     * 命令表的service
     */
    @Autowired
    private ITerminalCommandService terminalCommandService;
    /**
     * 车辆缓存的服务注入
     */
    @Autowired
    private RealDataService dm;

    @Value("${autoVoice:false}")
    private boolean autoVoice;
    //语音播报的各车辆配置表信息缓存
    private static Map<String, AutoVoicePO> autovoiceconfig = new ConcurrentHashMap();


    //根据simNo获取到这个设备当前的语音播放配置信息
    public AutoVoicePO getAutoVoice(String simNo) {
        return this.autovoiceconfig.get(simNo);
    }

    //用来判断这辆车是否有播报的配置信息
    public boolean isAutoVoice(String simNo){
       return this.autovoiceconfig.containsKey(simNo);
    }

    public Map<String, AutoVoicePO> getAutovoiceconfig(){
        return  this.autovoiceconfig;
    }


    //插入到命令表，并进行语音播放
    public void sendAutoVoice(String textContent, String simNo) {
        AttrLog alog = AttrLog.get("发送语音播报命令")
                .log("simNo", simNo)
                .log("textContent", textContent);
        try {
            TerminalCommand tc = new TerminalCommand();
            tc.setCmdType(JT808Constants.CMD_SEND_TEXT);
            int[] bitValues = new int[8];
            bitValues[3] = 1;
            String strDisplay = "终端TTS播读,";
            // 0x80”不用标准的。"0xc0"
            strDisplay = strDisplay.substring(0, strDisplay.length() - 1);
            StringBuilder strBit = new StringBuilder();
            ArrayUtils.reverse(bitValues);
            for (int bit : bitValues) {
                strBit.append(bit);
            }
            byte bitValue = Byte.valueOf(strBit.toString(), 2); // 将位数组转换为字节值

            if (bitValues[2] == 1) {
                bitValue = (byte) 0x80;
            }
            tc.setCmd("" + bitValue);
            tc.setCmdData(bitValue + ";" + textContent);
            tc.setRemark(strDisplay);
            VehicleData ve = dm.getVehicleData(simNo);//获取到车辆信息
            SendCommand(tc, ve);
        } catch (Exception ex) {
            logger.error("插入到命令表进行语音播放错误", ex);
            alog.log("出现异常", BasicUtil.exceptionMsg(ex));
        } finally {
            logger.debug(alog.toString());
        }
    }

    //语音播放发送命令
    protected void SendCommand(TerminalCommand tc, VehicleData vd) {
        tc.setPlateNo(vd.getPlateNo());
        String SimNo = vd.getSimNo();
        SimNo = SimNoUtil.toSimNo12(SimNo);
        tc.setSimNo(SimNo);
        tc.setVehicleId(vd.getEntityId());
        tc.setUserId(1);
        tc.setOwner("系统管理员");
        Integer cmdType = tc.getCmdType();
        String strCmd = Integer.toHexString(cmdType);
        while (strCmd.length() < 4)
            strCmd = "0" + strCmd;
        strCmd = "0x" + strCmd;
        this.terminalCommandService.save(tc);
    }



    //读取语音播报命令配置信息,每分钟同步一次
    @PostConstruct
    @Scheduled(fixedRate = 30000)
    private void readAutoVoiceConfig() {
        if(autoVoice) {
            AttrLog alog = AttrLog.get("刷新读取语音播报配置信息根据车辆进行缓存");
            try {
                String sql = "select c1.id as configid,c1.startTime,c1.endTime,c1.isuse,c1.sendContent,c1.type,c1.sendInterval,v1.plateNo,v1.SimNo,v1.vehicleId,v1.depId from ( " +
                        "select v.vehicleid,a.startTime ,a.endTime, a.isuse,b.sendContent,b.type,b.sendInterval,b.id from autovoice a,autovoiceconfig b ,autovoicebyvehicle v where a.id=b.autovoiceId and a.id=v.autovoiceId  and a.isuse=1) c1 left join vehicle v1 on c1.vehicleId=v1.vehicleId  where v1.deleted =false " +
                        "union  " +
                        "select c1.id as configid,c1.startTime,c1.endTime,c1.isuse,c1.sendContent,c1.type,c1.sendInterval,v1.plateNo,v1.SimNo,v1.vehicleId,v1.depId from ( " +
                        "select v.depId,a.startTime ,a.endTime, a.isuse,b.sendContent,b.type,b.sendInterval,b.id from autovoice a,autovoiceconfig b ,autovoicebydep v where a.id=b.autovoiceId and a.id=v.autovoiceId and a.isuse=1 ) c1 left join vehicle v1 on c1.depId=v1.depId  where v1.deleted =false";
                List<RowDataMap> result = jdbcUtil.sql(sql).queryWithMap();
                Map<String, AutoVoicePO> autovoiceconfig = new ConcurrentHashMap();
                if (ConverterUtils.isList(result)) {
                    for (RowDataMap rowDataMap : result) {
                        Long vehicleId = rowDataMap.getLongValue("vehicleId");
                        String simNo = rowDataMap.getStringValue("simNo");
                        Date startTime = TimeUtils.date(rowDataMap.getStringValue("startTime"));
                        Date endTime = TimeUtils.date(rowDataMap.getStringValue("endTime"));
                        String plateNo = rowDataMap.getStringValue("plateNo");
                        int configid = rowDataMap.getIntegerValue("configid");
                        Long depId = rowDataMap.getLongValue("depId");
                        int isuse = rowDataMap.getIntegerValue("isuse");//是否启用
                        int type = rowDataMap.getIntegerValue("type");//选择类型，1、ACC开之后立即、2、ACC开之后等待
                        int sendInterval = rowDataMap.getIntegerValue("sendInterval");//持续多久，在type=2的时候才生效
                        String sendContent = rowDataMap.getStringValue("sendContent");//播报内容
                        AutoVoicePO autoVoicePO = new AutoVoicePO();
                        if (autovoiceconfig.containsKey(simNo)) {//如果这个车辆的配置信息是存在的那么就进行更新操作，
                            autoVoicePO = autovoiceconfig.get(simNo);
                            List<AutoVoiceConfigPO> autoVoiceConfigPOS = autoVoicePO.getAutoVoiceConfigPOS();
                            AutoVoiceConfigPO autoVoiceConfigPO = new AutoVoiceConfigPO();
                            autoVoiceConfigPO.setSendContent(sendContent);
                            autoVoiceConfigPO.setSendInterval(sendInterval);
                            autoVoiceConfigPO.setType(type);
                            autoVoiceConfigPO.setId(configid);
                            autoVoiceConfigPO.setStartTime(startTime);
                            autoVoiceConfigPO.setEndTime(endTime);
                            autoVoiceConfigPO.setIsuse(isuse);
                            autoVoiceConfigPOS.add(autoVoiceConfigPO);
                            autoVoicePO.setAutoVoiceConfigPOS(autoVoiceConfigPOS);
                        } else {
                            autoVoicePO.setVehicleId(vehicleId);
                            autoVoicePO.setSimNo(simNo);
                            autoVoicePO.setStartTime(startTime);
                            autoVoicePO.setEndTime(endTime);
                            autoVoicePO.setPlateNo(plateNo);
                            autoVoicePO.setDepId(depId);
                            autoVoicePO.setIsuse(isuse);
                            List<AutoVoiceConfigPO> autoVoiceConfigPOS = new ArrayList<>();
                            AutoVoiceConfigPO autoVoiceConfigPO = new AutoVoiceConfigPO();
                            autoVoiceConfigPO.setSendContent(sendContent);
                            autoVoiceConfigPO.setSendInterval(sendInterval);
                            autoVoiceConfigPO.setType(type);
                            autoVoiceConfigPO.setId(configid);
                            autoVoiceConfigPO.setStartTime(startTime);
                            autoVoiceConfigPO.setEndTime(endTime);
                            autoVoiceConfigPO.setIsuse(isuse);
                            autoVoiceConfigPOS.add(autoVoiceConfigPO);
                            autoVoicePO.setAutoVoiceConfigPOS(autoVoiceConfigPOS);
                        }
                        autovoiceconfig.put(simNo, autoVoicePO);
                    }
                }
                this.autovoiceconfig = autovoiceconfig;

            } catch (Exception e) {
                alog.log("出现异常", BasicUtil.exceptionMsg(e));
            } finally {
                logger.debug(alog.toString());

            }
        }
    }


}
