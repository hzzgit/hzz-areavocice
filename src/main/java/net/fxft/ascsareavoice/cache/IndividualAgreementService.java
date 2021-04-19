package net.fxft.ascsareavoice.cache;


import com.ltmonitor.util.StringUtil;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.RowDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆协议配置服务
 *
 * @author admin
 */

@Service
public class IndividualAgreementService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(IndividualAgreementService.class);

    @Autowired
    private JdbcUtil jdbc;

    /** 更新缓存标志 */
    private boolean updateCacheFlag = false;



    /**
     * 车辆协议配置缓存 simNo,协议编号
     */
    private Map<String, String> individualAgreementCache = new ConcurrentHashMap<>();

    /**
     * 获取协议编号
     * @param simNo
     * @return
     */
    public String getIndividualAgreementCode(String simNo) {
        return individualAgreementCache.get(simNo);
    }

    /**
     * 判断车辆是否符合指定协议
     * @param simNo
     * @param individualAgreementCode 协议编号 eg:yuebiao
     * @return
     */
    public boolean isIndividualAgreement(String simNo,String individualAgreementCode) {
        String individualAgreement = getIndividualAgreementCode(simNo);
        if (!StringUtil.isNullOrEmpty(individualAgreement) && individualAgreementCode.equals(individualAgreement)){
                return true;
        }
        return false;
    }

    /**
     * 每隔2秒检查是否更新缓存
     */
    @Scheduled(fixedDelay = 2000)
    private void resetCacheSchedule(){
        if (updateCacheFlag){
            updateCacheFlag =false;
            initIndividualAgreementCache();
        }
    }



    @Override
    public void run(String... args) {
        try {
            initIndividualAgreementCache();
        } catch (Exception e) {
            logger.error("加载车辆协议配置缓存出错",e);
        }
    }

    /**
     * 加载车辆协议配置缓存
     * @throws Exception
     */
    public void initIndividualAgreementCache(){
        logger.info(">>>>>>>>>>>>加载车辆协议配置缓存 start<<<<<<<<<<<<<<");
        long l1 = System.currentTimeMillis();
        try {
            Map<String,String> individualAgreementCache = new ConcurrentHashMap<>();
            //加载禁用报警配置缓存
            String vehicleSql = "SELECT v.simNo,v.protocol_version protocolVersion  FROM vehicle v where v.deleted = false and  protocol_version is not null ";

            List<RowDataMap> vehicleList = jdbc.sql(vehicleSql).setNotPrint().queryWithMap();
            vehicleList.forEach(rowDataMap -> {
                String simNo = rowDataMap.getStringValue("simNo","");
                String code = rowDataMap.getStringValue("protocolVersion","");
                individualAgreementCache.put(simNo,code);
            });

            this.individualAgreementCache = individualAgreementCache;

        } catch (Exception e) {
            logger.error("加载车辆协议配置缓存出错",e);
        }
        long l2 = System.currentTimeMillis();

        logger.info(">>>>>>>>>>>>加载车辆协议配置缓存 end 耗时:"+(l2 - l1)+"ms<<<<<<<<<<<<<<");

    }

    /**
     * 更新缓存
     */
    public void updateCache(){
        this.updateCacheFlag = true;
    }

    /**
     * 更新缓存
     */
    public void updateCache(String params){
        updateCache();
    }



}
