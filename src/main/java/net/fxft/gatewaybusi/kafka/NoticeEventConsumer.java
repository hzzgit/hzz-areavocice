package net.fxft.gatewaybusi.kafka;

import com.ltmonitor.entity.TerminalCommand;
import net.fxft.cloud.kafka.BaseKafkaConsumer;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.IEventBody;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.gateway.event.notice.CmdNotFoundChannleEvent;
import net.fxft.gateway.event.notice.CmdWriteFinishEvent;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.gatewaybusi.service.impl.RealDataService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NoticeEventConsumer extends BaseKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(NoticeEventConsumer.class);

    public void start() throws Exception {
        String topic = UnitConfig.instance.getNoticeEventTopic();
        String consumerGroupId = UnitConfig.instance.getConsumerGroupId();
        startListener(consumerGroupId, topic, 2);
        log.info("启动kafka事件监听！consumerGroupId=" + consumerGroupId + "; topic=" + topic);
    }

    @Override
    public void beforeShutdown() {
    }

    @Override
    public boolean isPausePoll() {
        return false;
    }

    @Override
    public boolean acceptData(String key) {
        return true;
    }

    @Override
    public void pocessData(String key, byte[] value, ConsumerRecord<String, byte[]> record) {
        EventMsg em = KryoUtil.clsbyte2object(value);
        log.debug("收到kafka事件！" + em);
        if (em.getEventType() == IEventBody.EventType_UpdateCache) {
            UpdateCacheEvent up = (UpdateCacheEvent) em.getEventBody();
            if (UpdateCacheEvent.CacheName_Vehicle.equalsIgnoreCase(up.getCacheName())) {
                RealDataService.updateVehiclearg.set(true);//开启更新车辆缓存的标志位
                log.debug("接收到其他服务的车辆信息修改通知" + up.getParams());
            }
        }
    }
}
