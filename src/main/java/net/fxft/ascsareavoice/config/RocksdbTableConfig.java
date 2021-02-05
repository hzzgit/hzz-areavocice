package net.fxft.ascsareavoice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/12/17 17:00
 */
@Configuration
public class RocksdbTableConfig {

    @Bean
    public RocksdbTableUtil RocksdbTableUtil(){
        return new RocksdbTableUtil();
    }
}
