package net.fxft.gatewaybusi.kafka;

import com.ltmonitor.jt808.protocol.T808Message;
import net.fxft.cloud.metrics.Tps;
import net.fxft.common.tpool.BlockedThreadPoolExecutor;
import net.fxft.common.util.ByteUtil;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.protocol.DeviceMsg;
import net.fxft.gateway.protocol.DeviceMsgBuilder;
import net.fxft.gateway.protocol.TransferMsg;
import net.fxft.gateway.protocol.TransferMsgBuilder;
import net.fxft.gateway.util.KryoUtil;
import net.fxft.gatewaybusi.IShutdownHook;
import net.fxft.gatewaybusi.service.IMessageProcessService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Component
public class StartKafkaComsumer implements IShutdownHook {

    private static final Logger log = LoggerFactory.getLogger(StartKafkaComsumer.class);
    @Autowired
    private IMessageProcessService messageProcessService;

    @Autowired
    private ConsumerFactory kafkaConsumerFactory;

    private String fromDeviceMsgTopic;

    private BlockedThreadPoolExecutor execPool = null;

    @Value("${kafkaComsumer.threadPool:10}")
    private int threadCount;

    private Tps processTps = new Tps();

    private LongAdder totalReceiveCount = new LongAdder();

    private volatile boolean stopped = false;
    private Thread kafkaThread;


    public void startListener() throws Exception {
        fromDeviceMsgTopic = UnitConfig.instance.getFromDeviceMsgTopic();
        String consumerGroupId = UnitConfig.instance.getConsumerGroupId();
        log.info("初始化KafkaComsumer！fromDeviceMsgTopic=" + fromDeviceMsgTopic +
                "; threadPoolCount=" + threadCount + "; consumerGroupId" + consumerGroupId);
        execPool = new BlockedThreadPoolExecutor(threadCount);
        startKafkaListener();
    }

    @PreDestroy
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void shutdownHook() {
        log.info("---begin stop StartKafkaComsumer---");
        stopped = true;
        try {
            kafkaThread.join(50_000);
        } catch (InterruptedException e) {
            log.error("", e);
        }
        log.info("---end stop StartKafkaComsumer---");
    }


    private void startKafkaListener() {
        kafkaThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String consumerGroupId = UnitConfig.instance.getConsumerGroupId();
                Consumer<String, byte[]> cos = kafkaConsumerFactory.createConsumer(consumerGroupId, "busi");
                cos.subscribe(Arrays.asList(UnitConfig.instance.getHighFromDeviceMsgTopic(), fromDeviceMsgTopic));
                while (true) {
                    try {
                        ConsumerRecords<String, byte[]> records = cos.poll(1000);
                        for (ConsumerRecord<String, byte[]> record : records) {
                            receive(record.value());
                        }
                    } catch (Exception e) {
                        log.error("kafka poll出错！", e);
                    }
                    if (stopped) {
                        try {
                            log.info("---开始停止KafkaListener！");
                            long l1 = System.currentTimeMillis();
                            execPool.shutdown();
                            try {
                                execPool.awaitTermination(50, TimeUnit.SECONDS);
                            } catch (InterruptedException e) {
                                log.error("", e);
                            }
                            long l2 = System.currentTimeMillis();
                            log.info("---KafkaListener已停止！耗时=" + (l2 - l1));
                        } finally {
                            break;
                        }
                    }
                }
            }
        }, "kafka_poll");
        kafkaThread.start();
    }


    private void receive(byte[] message) {
        execPool.submit(() -> {
            totalReceiveCount.increment();
            pocess(message);
            processTps.inc();
        });
    }

    private void pocess(byte[] message) {

        try {
            TransferMsg transferMsg = TransferMsgBuilder.decodeFromBytes(message);
            if (transferMsg.isDecodable()) {
                DeviceMsg dm = (DeviceMsg) transferMsg.getMsgObject();
                T808Message tm = DeviceMsgBuilder.buildFromDeviceMsg(dm);
                if (tm == null) {
                    log.debug("接收到非T808Message消息, 丢弃！dm=" + dm);
                    return;
                }
                messageProcessService.processMsg(tm);
            }
        } catch (Exception e) {
            log.error("kafka接收fromDeviceMsg处理出错！bytes=" + ByteUtil.byteToHexStr(message), e);
        }

    }

}