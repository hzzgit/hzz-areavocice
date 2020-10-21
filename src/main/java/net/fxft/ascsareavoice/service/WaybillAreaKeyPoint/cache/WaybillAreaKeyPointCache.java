package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.AreaDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.OrderAreaDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.OrderDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache.DO.PointDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.AreaOrderDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.AreaPointDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.SimNoOrderDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.WaybillAreaKeyPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/10/21 11:00
 */
@Service
@Slf4j
public class WaybillAreaKeyPointCache {
    @Autowired
    private WaybillAreaKeyPointDao WaybillAreaKeyPointDao;

    /**
     * 是否开启运单围栏报警
     */
    @Value("${isWaybillAreaKeyPoint:false}")
    private boolean isWaybillAreaKeyPoint;

    /*simNo和orderId缓存*/
    private ConcurrentHashMap<String, Long> simNoOrderCache = new ConcurrentHashMap<>();
    /*orderId和订单区域配置缓存*/
    private ConcurrentHashMap<Long, OrderDO> orderAreaCache = new ConcurrentHashMap<>();
    /*areaId和区域配置点位信息缓存*/
    private ConcurrentHashMap<Long, AreaDO> AreaCache = new ConcurrentHashMap<>();


    public boolean isWaybillArea(String simNo){
        Boolean isexistPoint = false;//订单绑定的区域是否有点位
        if (simNoOrderCache.containsKey(simNo)) {
            Long orderid = simNoOrderCache.get(simNo);
            if (orderAreaCache.containsKey(orderid)) {
                OrderDO orderDO = orderAreaCache.get(orderid);
                List<OrderAreaDO> orderAreaDOS = orderDO.getOrderAreaDOS();
                for (OrderAreaDO orderAreaDO : orderAreaDOS) {
                    Long areaid = orderAreaDO.getAreaid();
                    if (AreaCache.containsKey(areaid)) {
                        isexistPoint = true;
                    }
                }
            }
        }
        return isexistPoint;

    }

    /**
     * 根据区域id获取到点位信息
     * @return
     */
    public AreaDO getAreaConfig(Long areaId){
        if (AreaCache.containsKey(areaId)) {
            return  AreaCache.get(areaId);
        }
        return  null;
    }

    /**
     * 根据simNO获取到最终要计算的区域关键点停车的信息
     */
    public OrderDO getConfigSimNo(String simNo) {
        Boolean isexistPoint = false;//订单绑定的区域是否有点位
        if (simNoOrderCache.containsKey(simNo)) {
            Long orderid = simNoOrderCache.get(simNo);
            if (orderAreaCache.containsKey(orderid)) {
                OrderDO orderDO = orderAreaCache.get(orderid);
                List<OrderAreaDO> orderAreaDOS = orderDO.getOrderAreaDOS();
                for (OrderAreaDO orderAreaDO : orderAreaDOS) {
                    Long areaid = orderAreaDO.getAreaid();
                    if (AreaCache.containsKey(areaid)) {
                        isexistPoint = true;
                    }
                }
                if (isexistPoint) {
                    orderDO.setOrderId(orderid);
                    return orderDO;
                }
            }
        }
        return null;
    }

    @PostConstruct
    private void init() {
        if(isWaybillAreaKeyPoint) {
            /*每隔20秒缓存一次*/
            new Thread(() -> {
                while (true) {
                    try {
                        long s = System.currentTimeMillis();   //获取开始时间
                        readAreaCache();
                        readorderAreaCache();
                        readordersimNoCache();
                        long e = System.currentTimeMillis(); //获取结束时间
                        log.debug("缓存运单围栏关键点停车用时：" + (e - s) + "ms");
                    } catch (Exception e) {
                        log.error("缓存关键点停车异常", e);
                        simNoOrderCache = new ConcurrentHashMap<>();
                        orderAreaCache = new ConcurrentHashMap<>();
                        AreaCache = new ConcurrentHashMap<>();
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


    /**
     * 缓存区域和点位的信息
     */
    private void readAreaCache() throws Exception {
        ConcurrentHashMap<Long, AreaDO> AreaCacheTemp = new ConcurrentHashMap<>();
        List<AreaPointDO> areaPointDOS = WaybillAreaKeyPointDao.searchAreaPoint();
        if (ConverterUtils.isList(areaPointDOS)) {
            for (AreaPointDO areaPointDO : areaPointDOS) {
                Long areaId = areaPointDO.getAreaId();
                Integer cfgradius = areaPointDO.getCfgradius();
                String name = areaPointDO.getName();
                Double latitude = areaPointDO.getLatitude();
                Double longitude = areaPointDO.getLongitude();
                String maptype = areaPointDO.getMaptype();
                Long pointid = areaPointDO.getPointid();
                AreaDO areaDO = new AreaDO();
                if (AreaCacheTemp.containsKey(areaId)) {
                    areaDO = AreaCacheTemp.get(areaId);
                }
                areaDO.setName(name);
                areaDO.setCfgradius(cfgradius);
                List<PointDO> pointDOS = areaDO.getPointDOS();
                pointDOS.add(new PointDO(latitude, longitude, maptype,pointid));
                AreaCacheTemp.put(areaId, areaDO);
            }
        }
        AreaCache = AreaCacheTemp;
    }

    /**
     * 缓存订单和区域的关系
     */
    private void readorderAreaCache() throws Exception {
        /*orderId和订单区域配置缓存*/
        ConcurrentHashMap<Long, OrderDO> orderAreaCacheTemp = new ConcurrentHashMap<>();
        List<AreaOrderDO> areaOrderDOS = WaybillAreaKeyPointDao.searchAreaOrder();
        if (ConverterUtils.isList(areaOrderDOS)) {
            for (AreaOrderDO areaOrderDO : areaOrderDOS) {
                Long orderid = areaOrderDO.getOrderid();
                Long areaid = areaOrderDO.getAreaid();
                Long userid = areaOrderDO.getUserid();
                Integer cfgparkdisplacedistance = areaOrderDO.getCfgparkdisplacedistance();
                Integer cfgparkdisplacetime = areaOrderDO.getCfgparkdisplacetime();
                Integer cfgparktime = areaOrderDO.getCfgparktime();
                OrderDO orderDO = new OrderDO();
                if (orderAreaCacheTemp.containsKey(orderid)) {
                    orderDO = orderAreaCacheTemp.get(orderid);
                }
                orderDO.setOrderId(orderid);
                orderDO.setUserId(userid);
                List<OrderAreaDO> orderAreaDOS = orderDO.getOrderAreaDOS();
                OrderAreaDO orderAreaDO = new OrderAreaDO(areaid, cfgparkdisplacedistance, cfgparkdisplacetime, cfgparktime);
                orderAreaDOS.add(orderAreaDO);
                orderAreaCacheTemp.put(orderid, orderDO);
            }
        }
        orderAreaCache = orderAreaCacheTemp;
    }


    /**
     * 缓存订单号和simNo的关系
     */
    private void readordersimNoCache() throws Exception {
        ConcurrentHashMap<String, Long> simNoOrderCacheTemp = new ConcurrentHashMap<>();

        List<SimNoOrderDO> simNoOrderDOS = WaybillAreaKeyPointDao.searchsimNoOrder();
        if (ConverterUtils.isList(simNoOrderDOS)) {
            for (SimNoOrderDO simNoOrderDO : simNoOrderDOS) {
                Long orderId = simNoOrderDO.getOrderId();
                String simNo = simNoOrderDO.getSimNo();
                simNoOrderCacheTemp.put(simNo, orderId);
            }
        }
        simNoOrderCache = simNoOrderCacheTemp;
    }


}
