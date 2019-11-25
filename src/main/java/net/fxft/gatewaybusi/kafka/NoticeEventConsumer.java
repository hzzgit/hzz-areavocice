package net.fxft.gatewaybusi.kafka;

import com.ltmonitor.entity.TerminalCommand;
import net.fxft.cloud.kafka.BaseKafkaConsumer;
import net.fxft.cloud.spring.AfterStartedRunner;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.EveryUnitKafkaHelper;
import net.fxft.gateway.event.IEventBody;
import net.fxft.gateway.event.IEveryUnitMsgProcessor;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.gateway.event.notice.CmdNotFoundChannleEvent;
import net.fxft.gateway.event.notice.CmdWriteFinishEvent;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.protocol.TransferMsgBuilder;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.gatewaybusi.service.impl.RealDataService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NoticeEventConsumer implements AfterStartedRunner {

    private static final Logger log = LoggerFactory.getLogger(NoticeEventConsumer.class);

    @Autowired
    private EveryUnitKafkaHelper everyUnitKafkaHelper;
    @Override
    public void run() throws Exception {
        everyUnitKafkaHelper.addIEveryUnitMsgProcessor(new IEveryUnitMsgProcessor() {
            @Override
            public void beforeKafkaShutdown() {

            }

            @Override
            public void pocessEventMsg(String key, EventMsg eventMsg, ConsumerRecord<String, byte[]> record) {
                pocessData(key, eventMsg, record);
            }
        });
    }


    public void pocessData(String key, EventMsg em, ConsumerRecord<String, byte[]> record) {
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
