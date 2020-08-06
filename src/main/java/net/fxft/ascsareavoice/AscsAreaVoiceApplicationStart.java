package net.fxft.ascsareavoice;


import net.fxft.cloud.spring.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

//@SpringBootApplication(exclude = {JpaRepositoriesAutoConfiguration.class, MybatisAutoConfiguration.class})
@SpringBootApplication
@EnableScheduling
public class AscsAreaVoiceApplicationStart {

    private static final Logger log = LoggerFactory.getLogger(AscsAreaVoiceApplicationStart.class);
    private static ApplicationContext context = null;

    public static void main(String[] args) {
        System.setProperty("nacos.logging.config", "classpath:log4j2-nacos.xml");
        ApplicationContext context = SpringApplication.run(AscsAreaVoiceApplicationStart.class, args);
        SpringUtil.invokeAfterStartedRunner(context);
    }


}
