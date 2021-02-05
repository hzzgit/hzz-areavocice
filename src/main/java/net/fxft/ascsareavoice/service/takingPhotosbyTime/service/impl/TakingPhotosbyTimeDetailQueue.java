package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import com.ltmonitor.entity.GPSRealData;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.cache.IsPhotoCache;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.TakephotoDao;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytimedetail;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytimeresult;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.service.dto.CmdIdDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.service.dto.QueueDto;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.tpool.AbstractBatchExecThreadPoolExecutor;
import net.fxft.common.tpool.BlockedThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author ：hzz
 * @description：定时拍照详情数据插入类
 * @date ：2021/1/21 17:15
 */
@Service
@Slf4j
public class TakingPhotosbyTimeDetailQueue {

    private AbstractBatchExecThreadPoolExecutor<Integer, Takingphotosbytimedetail> execPool;

    @Autowired
    private JdbcUtil jdbcUtil;

    public void addexecpool(Takingphotosbytimedetail req) {
        execPool.submit(req);
    }

    @PostConstruct
    private void init() {

        execPool = new AbstractBatchExecThreadPoolExecutor<Integer, Takingphotosbytimedetail>("定时拍照详情插入",
                2, 2000, 10, 2000) {
            @Override
            public Integer getQueueSplitKey(Takingphotosbytimedetail req) {
                return 0;
            }

            //这边就是到达时间和数量的时候会执行一次
            @Override
            public void batchRun(Integer splitKey, List<Takingphotosbytimedetail> reqlist) {
                long s = System.currentTimeMillis();   //获取开始时间
                jdbcUtil.insertList(reqlist).insertColumn(ColumnSet.all()).executeBatch(true);
                long e = System.currentTimeMillis(); //获取结束时间
                log.debug("插入定时拍照详情数据"+reqlist.size()+"条,用时：" + (e - s) + "ms");
            }
        };

    }

}

