package net.fxft.ascsareavoice.service.WaybillArea;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.vo.WaybillAreaMainVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/8/5 14:26
 */
@Slf4j
@Service
public class WaybillAreaCache {


    private ConcurrentHashMap<String, List<WaybillAreaMainVo>> waybillareacache = new ConcurrentHashMap<>();

    /**
     * 是否开启运单围栏报警
     */
    @Value("${isWaybillArea:false}")
    private boolean isWaybillArea;


    @Autowired
    private WaybillAreaDao waybillAreaDao;

    /**
     * 是否有这辆车的运单围栏配置
     *
     * @return
     */
    public boolean isWaybillArea(String simNo) {
        return waybillareacache.containsKey(simNo);
    }

    /**
     * 返回这辆车对应的运单围栏配置
     *
     * @param simNo
     * @return
     */
    public List<WaybillAreaMainVo> searchbysimNo(String simNo) {
        List<WaybillAreaMainVo> waybillAreaMainVo = null;
        if (waybillareacache.containsKey(simNo)) {
            waybillAreaMainVo = waybillareacache.get(simNo);
        }
        return waybillAreaMainVo;
    }


    @PostConstruct
    private void init() {
        if (isWaybillArea) {
            new Thread(() -> {
                while (true) {
                    try {
                        long s = System.currentTimeMillis();   //获取开始时间
                        ConcurrentHashMap<String, List<WaybillAreaMainVo>> searchwaybillarea = waybillAreaDao.searchwaybillarea();
                        waybillAreaDao.searchwaybillareapoint(searchwaybillarea);
                        waybillareacache = searchwaybillarea;
                        long e = System.currentTimeMillis(); //获取结束时间
                        log.debug("用时：" + (e - s) + "ms");
                        log.info("缓存运单围栏成功用时：" + (e - s) + "ms");
                    } catch (Exception e) {
                        log.error("进行运单围栏缓存异常", e);
                    }
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }


}
