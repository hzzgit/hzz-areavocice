package net.fxft.ascsareavoice.service.MapArea;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.MapArea.dto.AreagpsrealdataDto;
import net.fxft.ascsareavoice.service.MapArea.entiry.Areagpsrealdata;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.tpool.AbstractBatchExecThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：hzz
 * @description：用来统计当前围栏车辆信息的入库程序
 * @date ：2021/1/21 17:15
 */
@Service
@Slf4j
public class AreaGpsRealDataQueue {

    private AbstractBatchExecThreadPoolExecutor<Integer, AreagpsrealdataDto> execPool;

    @Autowired
    private AreaGpsRealDataService areaGpsRealDataService;

    @Autowired
    private JdbcUtil jdbcUtil;

    public void addexecpool(AreagpsrealdataDto req) {
        execPool.submit(req);
    }

    @PostConstruct
    private void init() {


        //这个线程主要是保存当前围栏内有哪些车辆
        execPool = new AbstractBatchExecThreadPoolExecutor<Integer, AreagpsrealdataDto>("围栏实时位置处理线程",
                2, 2000, 10, 2000) {
            @Override
            public Integer getQueueSplitKey(AreagpsrealdataDto req) {
                return 0;
            }


            @Override
            public void batchRun(Integer splitKey, List<AreagpsrealdataDto> reqlist) {

                try {
                    //这边是将刚进去围栏的车辆主键保存起来
                    List<Areagpsrealdata> addList = new ArrayList<>();
                    Map<String, Byte> isaddMap = new HashMap<>();
                    List<Areagpsrealdata> deleteList = new ArrayList<>();
                    for (AreagpsrealdataDto areagpsrealdataDto : reqlist) {
                        Areagpsrealdata areagpsrealdata = areagpsrealdataDto.getAreagpsrealdata();
                        if (areagpsrealdataDto.isIsadd()) {
                            String key = getKey(areagpsrealdata.getVehicleid(), areagpsrealdata.getAreaid());
                            if (!isaddMap.containsKey(key)) {
                                addList.add(areagpsrealdata);
                                isaddMap.put(key, (byte) 1);
                            }

                        }
                    }
                    long s = System.currentTimeMillis();   //获取开始时间
                    //这边是将刚进入围栏的车辆信息保存到数据库
                    if (addList != null && addList.size() > 0) {
                        try {
                            jdbcUtil.insertList(addList).insertColumn(ColumnSet.all()).executeBatch(true);

                            for (Areagpsrealdata areagpsrealdata : addList) {
                                areaGpsRealDataService.putKeyByRocksdb(areagpsrealdata.getVehicleid(), areagpsrealdata.getAreaid());
                            }
                        } catch (Exception e) {
                            log.error("批量插入异常,转为单条插入", e);
                            for (Areagpsrealdata areagpsrealdata : addList) {
                                try {
                                    jdbcUtil.insert(areagpsrealdata).insertColumn(ColumnSet.all()).execute();
                                    areaGpsRealDataService.putKeyByRocksdb(areagpsrealdata.getVehicleid(), areagpsrealdata.getAreaid());
                                } catch (Exception ex) {
                                    log.error("单条插入异常", ex);
                                }
                            }
                        }
                    }
                    //这边是将离开围栏的车辆主键删除掉，这样这张表永远只有在围栏里面的车辆主键信息
                    for (AreagpsrealdataDto areagpsrealdataDto : reqlist) {
                        Areagpsrealdata areagpsrealdata = areagpsrealdataDto.getAreagpsrealdata();
                        if (areagpsrealdataDto.isIsadd()) {
                        } else {
                            boolean arg = areaGpsRealDataService.isexsitByRocksdb(areagpsrealdata.getVehicleid(), areagpsrealdata.getAreaid());
                            if (arg) {//存在rocksdb就才进行删除操作
                                deleteList.add(areagpsrealdata);
                            }
                            areaGpsRealDataService.deleteKeyByRocksdb(areagpsrealdata.getVehicleid(), areagpsrealdata.getAreaid());
                        }
                    }
                    if (deleteList != null && deleteList.size() > 0) {
                        try {
                            jdbcUtil.deleteList(deleteList).whereColumn(Areagpsrealdata.F_vehicleid, Areagpsrealdata.F_areaid).executeBatch(true);
                        } catch (Exception e) {
                            log.error("批量删除失败", e);
                        }
                    }
                    long e = System.currentTimeMillis(); //获取结束时间
                    log.debug("围栏实时位置处理数据" + reqlist.size() + "条,用时：" + (e - s) + "ms");
                } catch (Exception ex) {
                    log.error("围栏实时位置处理数据异常", ex);
                }
            }
        };

    }


    private String getKey(long vehicleId, long areaId) {
        String key = vehicleId + "_" + areaId;
        return key;
    }

}


