package net.fxft.gatewaybusi;

import net.fxft.cloud.spring.AfterStartedRunner;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.gateway.util.TraceLogger;
import net.fxft.gatewaybusi.kafka.NoticeEventConsumer;
import net.fxft.gatewaybusi.kafka.StartKafkaComsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AfterStarted implements AfterStartedRunner {
    
    private static final Logger log = LoggerFactory.getLogger(AfterStarted.class);

    @Autowired
    private StartKafkaComsumer startKafkaComsumer;


    @Override
    public void run() throws Exception {

        log.info("启动围栏报警和语音播报服务");
        startKafkaComsumer.startListener();
    }


}
