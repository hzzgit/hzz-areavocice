package net.fxft.gatewaybusi;


import net.fxft.cloud.spring.SpringUtil;
import net.fxft.common.tpool.NamedThreadPoolExecutor;
import net.fxft.gateway.device.DepManager;
import net.fxft.gateway.device.IDepManager;
import net.fxft.gateway.event.EveryUnitKafkaHelper;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.gateway.util.AppStartUtil;
import net.fxft.gateway.util.LogController;
import net.fxft.gatewaybusi.kafka.StartKafkaComsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ScheduledExecutorService;

//@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class, MybatisAutoConfiguration.class})
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.ltmonitor", "net.fxft.gatewaybusi"})
public class GatewayBusiApplicationStart {

    private static final Logger log = LoggerFactory.getLogger(GatewayBusiApplicationStart.class);
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        AppStartUtil.initNacosConfig();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    log.info("---开始停止GatewayBusiApplication---");
                    StartKafkaComsumer kafkaComsumer = context.getBean(StartKafkaComsumer.class);
                    if (kafkaComsumer != null) {
                        kafkaComsumer.shutdownHook();
                    }
                    log.info("---GatewayBusiApplication成功退出---");
                } catch (Exception e) {
                    log.error("GatewayBusiApplication退出异常！", e);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        context = SpringApplication.run(GatewayBusiApplicationStart.class, args);
        SpringUtil.invokeAfterStartedRunner(context);
    }

    @Bean
    public UnitConfig crateUnitConfig() {
        return new UnitConfig();
    }


    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService createScheduledExecutor() {
        return NamedThreadPoolExecutor.newScheduledThreadPool(20, "schePool");
    }

}
