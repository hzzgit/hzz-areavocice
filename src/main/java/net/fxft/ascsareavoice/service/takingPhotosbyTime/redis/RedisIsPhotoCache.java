package net.fxft.ascsareavoice.service.takingPhotosbyTime.redis;


import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.dto.IsPhotoDto;
import net.fxft.cloud.redis.RedisUtil;
import net.fxft.common.util.JacksonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ：hzz
 * @description：redis缓存点
 * @date ：2021/1/21 13:50
 */
@Slf4j
@Service
public class RedisIsPhotoCache {

    @Autowired
    private RedisUtil redisUtil;

    //记录已经拍照的key
    private final String key = "isPhoto:";

    /**
     * 如果照片上来，那么就更新拍照时间以及状态
     *
     * @param vehicleId
     * @param configid
     */
    public void put(Long vehicleId, int configid) {
        String key = getKey(vehicleId, configid);
        IsPhotoDto isPhotoDto = new IsPhotoDto(IsPhotoDto.已下发);
        redisUtil.execute(jedis -> {
            jedis.set(key, JacksonUtil.toJsonString(isPhotoDto));
        });
        log.debug("定时拍照-照片上传设置key:" + key + ",value:" + isPhotoDto);
    }


    /**
     * 移除关闭或者过期的定时拍照配置的车辆配置
     * @param vehicleId
     * @param configid
     */
    public void remove(Long vehicleId, int configid){
        String key = getKey(vehicleId, configid);
        redisUtil.execute(jedis -> {
            jedis.del(key);
        });

    }

    /**
     * @param vehicleId
     * @param configid
     */
    public IsPhotoDto get(Long vehicleId, int configid) {
        String key = getKey(vehicleId, configid);
        IsPhotoDto isPhotoDto = null;
        Boolean exists = (Boolean) redisUtil.execute(jedis -> {
            return jedis.exists(key);
        });
        if (exists) {
            String jsonStr = (String) redisUtil.execute(jedis -> {
                return jedis.get(key);
            });
            isPhotoDto = JacksonUtil.parseJsonString(jsonStr, IsPhotoDto.class);
        }
        return isPhotoDto;
    }


    private String getKey(Long vehicleId, int configId) {
        String key1 = key + vehicleId + "-" + configId;
        return key1;
    }

}
