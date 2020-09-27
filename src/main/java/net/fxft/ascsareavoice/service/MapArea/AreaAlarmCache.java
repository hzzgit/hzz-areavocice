package net.fxft.ascsareavoice.service.MapArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ConcurrentMap;

public class AreaAlarmCache {


    private static final Logger log = LoggerFactory.getLogger(AreaAlarmCache.class);
    private static final   String cachename="AreaAlarmCache.cache";




    //这边是读取文件的缓存
    public static  ConcurrentMap<String, Boolean> loadCache() {
        try {
            long s = System.currentTimeMillis(); //获取开始时间

            File f = new File(cachename);
            if (f.exists()) {
                FileInputStream fis = new FileInputStream(f);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object obj = ois.readObject();
                ois.close();
                ConcurrentMap<String, Boolean> CrossMap= (ConcurrentMap<String, Boolean>) obj;
                long e = System.currentTimeMillis(); //获取结束时间
                log.debug( "本地缓存读取时间为" + (e - s) + "ms");
                return CrossMap;
            }
        } catch (Exception e) {
            log.error("读取保单进出围栏缓存失败", e);
            return null;
        }

        return null;
    }


    public static void saveCache(ConcurrentMap<String, Boolean> CrossMap) {
        try {
            long l1 = System.currentTimeMillis();
            FileOutputStream fos = new FileOutputStream(cachename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(CrossMap);
            oos.flush();
            oos.close();
            long l2 = System.currentTimeMillis();
            log.debug( "保单本地缓存写入时间为" + (l2 - l1) + "ms");
        } catch (Exception e) {
            log.error("保存保单进出围栏缓存失败", e);
        }

    }

    public static  void deletecache(){
        File f = new File(cachename);
        if (f.exists()) {
            f.delete();
        }
    }

    public static void main(String[] args){


    }


}
