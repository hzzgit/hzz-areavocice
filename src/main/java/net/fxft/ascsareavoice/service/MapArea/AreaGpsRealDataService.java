package net.fxft.ascsareavoice.service.MapArea;

import net.fxft.ascsareavoice.service.MapArea.dto.AreagpsrealdataDto;
import net.fxft.ascsareavoice.service.MapArea.entiry.Areagpsrealdata;
import net.fxft.ascsutils.config.rocksdb.RocksdbTableUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author ：hzz
 * @description：用来计算车辆是否在当前围栏
 * @date ：2021/3/15 15:27
 */
@Service
public class AreaGpsRealDataService {
    @Autowired
    private AreaGpsRealDataQueue areaGpsRealDataQueue;

    private  final  String rockskey="AreaGpsRealData";

    @Autowired
    private RocksdbTableUtil rocksdbTableUtil;


    /**
     * 判断是否已经插入到rocksdb，代表着已经插入到存在围栏的表里面
     * @return
     */
    public boolean isexsitByRocksdb(long vehicleId,long areaId){
        boolean isexsitkey = rocksdbTableUtil.isexsitkey(rockskey, getkey(vehicleId, areaId));
        return isexsitkey;
    }

    /**
     * 删除掉已经插入到在围栏里面的车辆缓存
     * @param vehicleId
     * @param areaId
     */
    public void deleteKeyByRocksdb(long vehicleId,long areaId){
        rocksdbTableUtil.remove(rockskey,getkey(vehicleId,areaId));
    }

    public void putKeyByRocksdb(long vehicleId,long areaId){
        rocksdbTableUtil.put(rockskey,getkey(vehicleId,areaId));
    }

    /**
     * 初始化的时候创建下rocksdb的列簇
     */
    @PostConstruct
    private void init(){
        rocksdbTableUtil.createTable(rockskey);
    }

    /**
     * 用于判断车辆是否在围栏里面
     */
    public void checkAreaGpsRealData(  boolean inArea,long vehicleId,long areaId){
        AreagpsrealdataDto areagpsrealdataDto=new AreagpsrealdataDto();
        areagpsrealdataDto.setIsadd(inArea);
        Areagpsrealdata areagpsrealdata=new Areagpsrealdata();
        areagpsrealdata.setId(0);
        areagpsrealdata.setAreaid(areaId);
        areagpsrealdata.setVehicleid(vehicleId);
        areagpsrealdata.setCreatedate(new Date());
        areagpsrealdataDto.setAreagpsrealdata(areagpsrealdata);
        String getkey = getkey(vehicleId, areaId);
        //要不存在这个字段数据才进行插入,或者离开围栏也可以
        if(inArea==false||rocksdbTableUtil.isexsitkey(rockskey,getkey)==false){
            areaGpsRealDataQueue.addexecpool(areagpsrealdataDto);
        }

    }

    /**
     * 获取到rocksdb的Key值
     * @param vehicleId
     * @param areaId
     * @return
     */
    public  String getkey(long vehicleId,long areaId){
        String key=vehicleId+"_"+areaId;
        return key;
    }
}
