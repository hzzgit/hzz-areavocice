package net.fxft.ascsareavoice.kafka;

import net.fxft.cloud.metrics.Tps;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.kafka.IKafkaSenderHelper;
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
