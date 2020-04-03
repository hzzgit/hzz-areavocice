package net.fxft.gatewaybusi;


import net.fxft.cloud.spring.SpringUtil;
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
        System.setProperty("nacos.logging.config", "classpath:log4j2-nacos.xml");
        ApplicationContext context = SpringApplication.run(GatewayBusiApplicationStart.class, args);
        SpringUtil.invokeAfterStartedRunner(context);
    }


}
