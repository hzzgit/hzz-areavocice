package net.fxft.gatewaybusi.kafka;

import net.fxft.cloud.metrics.Tps;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.util.KryoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.LongAdder;

@Service
public class KafkaMessageSender{

    private static final Logger log = LoggerFactory.getLogger(KafkaMessageSender.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;


//    @Value("${kafka.toDeviceMsgTopic}")
//    private String toDeviceMsgTopic;

    private Tps sendKafkaTps = new Tps();

    private LongAdder totalSendCount = new LongAdder();

    public Tps getSendKafkaTps() {
        return sendKafkaTps;
    }

    public long getTotalSendCount() {
        return totalSendCount.longValue();
    }


    public boolean sendEveryUnitEventMsg(EventMsg eventMsg) {
        try {
            byte[] bytes = KryoUtil.object2clsbyte(eventMsg);
            kafkaTemplate.send(UnitConfig.instance.getEveryUnitEventTopic(), bytes);
            log.debug("kafka发送EveryUnitEvent！" + eventMsg);
            totalSendCount.increment();
            sendKafkaTps.inc();
            return true;
        } catch (Exception e) {
            log.error("kafka发送EveryUnitEvent出错！", e);
            return false;
        }
    }
    public boolean sendAlarmEventMsg(EventMsg eventMsg, String simNo) {
        try {
            byte[] bytes = KryoUtil.object2clsbyte(eventMsg);
            kafkaTemplate.send(UnitConfig.instance.getAlarmEventTopic(),simNo, bytes);
            log.debug("kafka发送AlarmEvent！" + eventMsg);
            totalSendCount.increment();
            sendKafkaTps.inc();
            return true;
        } catch (Exception e) {
            log.error("kafka发送EveryUnitEvent出错！", e);
            return false;
        }
    }


    public void resetMonitorCount() {
        totalSendCount.reset();
    }
}
