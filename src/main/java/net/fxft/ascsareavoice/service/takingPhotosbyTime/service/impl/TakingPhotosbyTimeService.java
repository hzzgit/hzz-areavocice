package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.TakephotoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author ：hzz
 * @description：定时拍照主线逻辑类
 * @date ：2021/1/21 17:15
 */
@Service
@Slf4j
public class TakingPhotosbyTimeService {

    @Autowired
    private TemSendService temSendService;

    @Autowired
    private TakephotoDao takephotoDao;

    /**
     * 是否开启定时拍照功能
     */
    @Value("${istakingphoto:false}")
    private boolean istakingphoto;

    /**
     * 启动定时拍照，一分钟检测一次
     */
    private void init(){
        if(istakingphoto){
            new Thread(()->{
                while (true){
                    try {
                        begin();
                    } catch (Exception e) {
                        log.error("定时拍照：主线程异常",e);
                    }

                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
    private void begin() throws  Exception{

    }
}


