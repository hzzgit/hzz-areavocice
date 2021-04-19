package net.fxft.ascsareavoice.kafka;

import net.fxft.ascsareavoice.cache.IndividualAgreementService;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl.TakingPhotosbyTimeService;
import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.IEventBody;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.gateway.kafka.eventmsg.IEveryUnitMsgProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeEventConsumer implements IEveryUnitMsgProcessor {

    private static final Logger log = LoggerFactory.getLogger(NoticeEventConsumer.class);

    @Autowired
    private IndividualAgreementService individualAgreementService;

    @Override
    public void pocessMsg(String s, EventMsg eventMsg, ConsumerRecord<String, byte[]> consumerRecord) {
        if (eventMsg.getEventType() == IEventBody.EventType_UpdateCache) {
            UpdateCacheEvent up = (UpdateCacheEvent) eventMsg.getEventBody();
            if (UpdateCacheEvent.CacheName_Vehicle.equalsIgnoreCase(up.getCacheName())) {
                RealDataService.updateVehiclearg.set(true);//开启更新车辆缓存的标志位
                individualAgreementService.updateCache();
                log.debug("接收到其他服务的车辆信息修改通知" + up.getParams());
            } else if ("takingphotosbytime".equalsIgnoreCase(up.getCacheName())) {
                synchronized (TakingPhotosbyTimeService.updatTakingPhotoarg) {
                    TakingPhotosbyTimeService.updatTakingPhotoarg.set(true);//开启更新车辆缓存的标志位
                    log.debug("接收到其他服务的定时拍照配置修改通知" + up.getParams());
                }
            }

        }
    }

    @Override
    public void beforeShutdown() {

    }
}
