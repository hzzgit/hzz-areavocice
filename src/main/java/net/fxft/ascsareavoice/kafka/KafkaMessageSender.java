package net.fxft.ascsareavoice.kafka;

import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.jt808.tool.Tools;
import net.fxft.ascsareavoice.service.j808.service.T808Manager;
import net.fxft.cloud.metrics.Tps;
import net.fxft.common.util.BasicUtil;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.kafka.IKafkaSenderHelper;
import net.fxft.gateway.protocol.DevMsgAttr;
import net.fxft.gateway.protocol.DeviceMsg;
import net.fxft.gateway.util.TraceLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.LongAdder;

@Service
public class KafkaMessageSender {

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    @Autowired
    private IKafkaSenderHelper kafkaSender;


//    @Value("${kafka.toDeviceMsgTopic}")
//    private String toDeviceMsgTopic;

    private Tps sendKafkaTps = new Tps();

    private LongAdder totalSendCount = new LongAdder();


    public boolean Send808Message(T808Message tm, String toDevTopic, String toChannelId) {
        try {
            if (tm == null) {
                return false;
            }
            if (tm.getSimNo() == null) {
                log.error("Send808Message的simNo不能为null！");
                return false;
            }
            if (tm.getHeader().getMessageSerialNo() == 0) {
                tm.getHeader().setMessageSerialNo((short) T808Manager.getSerialNo());
            }
            DevMsgAttr msgAttr = tm.getDevMsgAttr();
            if (msgAttr == null) {
                msgAttr = new DevMsgAttr();
                msgAttr.loadDefaultValue();
                tm.setDevMsgAttr(msgAttr);
            }
            if (msgAttr.getToDevTopic() == null) {
                msgAttr.setToDevTopic(toDevTopic);
                msgAttr.setChannelId(toChannelId);
            }
            if (BasicUtil.isEmpty(toDevTopic)) {
                log.error("kafka发送ToDeviceMsg失败， 设备没有连接！simNo=" + tm.getSimNo() +
                        "; msgType=" + Tools.msgTypeToHexString(tm.getMessageType()));
                return false;
            }
            kafkaSender.sendDeviceMsg(msgAttr.getToDevTopic(), tm);
//            DeviceMsg msg = DeviceMsgBuilder.buildFromT808Message(0, 0, tm);
//            kafkaTemplate.send(msgAttr.getToDevTopic(), TransferMsgBuilder.build(msg).toTransferBytes());
            if (TraceLogger.isTrace(tm.getSimNo())) {
                log.debug("kafka发送ToDeviceMsg！" + tm);
            }
            totalSendCount.increment();
            sendKafkaTps.inc();
            return true;
        } catch (Exception e) {
            log.error("kafka发送ToDeviceMsg出错！", e);
            return false;
        }
    }


    public boolean Send808Message(DeviceMsg dm, String toDevTopic, String toChannelId) {
        try {
            if (dm == null) {
                return false;
            }
            if (dm.getSimNo() == null) {
                log.error("Send808Message的simNo不能为null！");
                return false;
            }
            if (dm.getMsgSerialNo() == 0) {
                dm.setMsgSerialNo(T808Manager.getSerialNo());
            }
            DevMsgAttr msgAttr = dm.getDevMsgAttr();
            if (msgAttr == null) {
                msgAttr = new DevMsgAttr();
                msgAttr.loadDefaultValue();
                dm.setDevMsgAttr(msgAttr);
            }
            if (msgAttr.getToDevTopic() == null) {
                msgAttr.setToDevTopic(toDevTopic);
                msgAttr.setChannelId(toChannelId);
            }
            if (BasicUtil.isEmpty(toDevTopic)) {
                log.error("kafka发送ToDeviceMsg失败， 设备没有连接！simNo=" + dm.getSimNo() +
                        "; msgType=" + Tools.msgTypeToHexString(dm.getMessageType()));
                return false;
            }
            kafkaSender.sendDeviceMsg(msgAttr.getToDevTopic(), dm);
//            kafkaTemplate.send(msgAttr.getToDevTopic(), TransferMsgBuilder.build(dm).toTransferBytes());
            if (TraceLogger.isTrace(dm.getSimNo())) {
                log.debug("kafka发送ToDeviceMsg！" + dm);
            }
            totalSendCount.increment();
            sendKafkaTps.inc();
            return true;
        } catch (Exception e) {
            log.error("kafka发送ToDeviceMsg出错！", e);
            return false;
        }
    }


    public boolean sendAreaAlarmEventMsg(EventMsg eventMsg,String simNo) {
        try {
            kafkaSender.sendAlarmEventMsg(eventMsg,simNo);
            log.debug("kafka发送围栏报警！" + eventMsg);
            totalSendCount.increment();
            sendKafkaTps.inc();
            return true;
        } catch (Exception e) {
            log.error("kafka发送围栏报警！", e);
            return false;
        }
    }

    public void resetMonitorCount() {
        totalSendCount.reset();
    }
}
