package net.fxft.ascsareavoice.service.takingPhotosbyTime.cache;


import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.cache.dto.IsPhotoDto;
import net.fxft.ascsutils.config.rocksdb.RocksdbTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;


/**
 * @author ：hzz
 * @description：rocksdb缓存类
 * @date ：2021/1/21 13:50
 */
@Slf4j
@Service
public class IsPhotoCache {


    @Autowired
    private RocksdbTableUtil rocksdbTableUtil;

    private String rockTableName="isphotoCache";

    @PostConstruct
    public void Init(){
        rocksdbTableUtil.createTable(rockTableName);
    }

    /**
     * 这边是只要下发了拍照命令，那么就算它已经上传了照片，失败不考虑
     *
     * @param vehicleId
     * @param configid
     */
    public void put(Long vehicleId, int configid, Date checkTime) {
        String key = getKey(vehicleId, configid);
        IsPhotoDto isPhotoDto = new IsPhotoDto(IsPhotoDto.已下发,checkTime);
        rocksdbTableUtil.put(rockTableName,key, isPhotoDto);
        log.debug("定时拍照-照片上传设置key:" + key + ",value:" + isPhotoDto);
    }


    /**
     * 移除关闭或者过期的定时拍照配置的车辆配置
     *
     * @param vehicleId
     * @param configid
     */
    public void remove(Long vehicleId, int configid) {
        String key = getKey(vehicleId, configid);
        rocksdbTableUtil.remove(rockTableName,key);
    }

    /**
     * 获取到照片是否已经上传的缓存
     * @param vehicleId
     * @param configid
     */
    public IsPhotoDto get(Long vehicleId, int configid) {
        String key = getKey(vehicleId, configid);
        IsPhotoDto isPhotoDto = null;
        isPhotoDto= rocksdbTableUtil.get(rockTableName,key, IsPhotoDto.class);
        return isPhotoDto;
    }


    private String getKey(Long vehicleId, int configId) {
        String key1 = vehicleId + "-" + configId;
        return key1;
    }

}
