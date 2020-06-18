package net.fxft.ascsareavoice.service.AutoVoice.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class AutoVoiceQueueService {


    private static final Logger logger = LoggerFactory.getLogger(AutoVoiceQueueService.class);

    //处理下发播报的队列
    private ConcurrentLinkedQueue<String> sendVoiceQueue = new ConcurrentLinkedQueue();

    @Autowired
    private AutoVoiceConfigService autoVoiceService;


    private Thread downThread;
    //初始化上行、下行、补发队列
    @PostConstruct
    public void startTransfer()
    {
        //发送语音播报队列的线程启动
        if (downThread == null) {
            downThread = new Thread(new Runnable() {
                public void run() {
                    downMessageThreadFunc();
                }
            }, "downThread");
            downThread.start();
        }
    }



    //这边是下行要发送给上行车辆的连接
    private void downMessageThreadFunc() {
        int times = 0;
        while (true) {
            try {
                if (times > 0 && times % 10 == 0) {
                    times = 0;
                    int qs= sendVoiceQueue.size();
                    if (qs > 100) {
                        logger.debug("语音播报队列" + sendVoiceQueue + "排队等待应答数量:" + qs);
                    }
                }
                String data = sendVoiceQueue.poll();
                int k = 0;
                while (data != null && !"".equals(data)&&!"null".equals(data)) {
                    logger.debug("开始发送语音播报");
                    String textContent = data.split("_")[0];
                    String simNo = data.split("_")[1];
                    autoVoiceService.sendAutoVoice(textContent, simNo);//发送语音播报命令
                    data = sendVoiceQueue.poll();
                }
            } catch (Exception ex) {
                logger.error("语音播报线程异常", ex);
            }
            times++;
            if(sendVoiceQueue.size() == 0) {
                try {//每隔五秒检测一次队列处理线程
                        Thread.sleep(5000);
                } catch (InterruptedException e1) {
                }
            }
        }
    }



    public void addSendQueue(String textContent,String simNo) {
        String data=textContent+"_"+simNo;
        sendVoiceQueue.add(data);
    }
}
