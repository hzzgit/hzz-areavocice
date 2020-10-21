package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.cache;

import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.service.DTO.SimNoOrderKeyPointDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ConcurrentMap;

public class WaybillAreaKeyPointautoCache {


    private static final Logger log = LoggerFactory.getLogger(WaybillAreaKeyPointautoCache.class);
    private static final   String cachename="WaybillAreaKeyPointautoCache.cache";




    //这边是读取文件的缓存
    public static  ConcurrentMap<String, SimNoOrderKeyPointDTO> loadCache() {
        try {
            long s = System.currentTimeMillis(); //获取开始时间

            File f = new File(cachename);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                ois.close();
                ConcurrentMap<String, SimNoOrderKeyPointDTO> CrossMap= (ConcurrentMap<String, SimNoOrderKeyPointDTO>) obj;
                long e = System.currentTimeMillis(); //获取结束时间
                log.debug( "本地缓存读取时间为" + (e - s) + "ms");
                return CrossMap;
            }
        } catch (Exception e) {
            log.error("读取保单进出围栏关键点停车缓存失败", e);
            return null;
        }

        return null;
    }


    public static void saveCache(ConcurrentMap<String, SimNoOrderKeyPointDTO> CrossMap) {
        try {
            long l1 = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(cachename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(CrossMap);
            oos.flush();
            oos.close();
            long l2 = System.currentTimeMillis();
            log.debug( "保单关键点停车本地缓存写入时间为" + (l2 - l1) + "ms");
        } catch (Exception e) {
            log.error("保存保单进出围栏关键点停车缓存失败", e);
        }

    }

    public static  void deletecache(){
        File f = new File(cachename);
        if (f.exists()) {
            f.delete();
        }
    }


}
