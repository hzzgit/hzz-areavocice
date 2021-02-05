package net.fxft.ascsareavoice.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.cache.dto.IsPhotoDto;
import net.fxft.common.util.JacksonUtil;
import org.rocksdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/12/14 17:38
 */
@Slf4j
public class RocksdbTableUtil {


    private RocksDB rocksDB = null;

    //用于操作数据库加key的值
    List<ColumnFamilyHandle> columnFamilyHandles = new ArrayList<>();
    //用于创建数据库或者删除
    List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();
    //用于存放key对应的表操作使用的类
    ConcurrentMap<String, ColumnFamilyHandle> columnFamilyHandleMap = new ConcurrentHashMap<>();

    public RocksdbTableUtil() {
        open();
    }


    /**
     * 重整Map缓存表操作的类
     */
    private void reconstitutionMap() {
        ConcurrentMap<String, ColumnFamilyHandle> columnFamilyHandleMapTemp = new ConcurrentHashMap<>();
        if (columnFamilyDescriptors != null && columnFamilyDescriptors.size() > 0) {
            for (int i = 0; i < columnFamilyDescriptors.size(); i++) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandles.get(i);
                String tablename = new String(columnFamilyDescriptors.get(i).columnFamilyName());
                if (!columnFamilyHandleMapTemp.containsKey(tablename)) {
                    columnFamilyHandleMapTemp.put(tablename, columnFamilyHandle);
                }
            }
            columnFamilyHandleMap = columnFamilyHandleMapTemp;
        }
    }

    public void open() {
        try {
            String property = System.getProperty("user.dir");
            String dbPath = property + File.separator + "rocks";
            dbPath = "D:\\test\\rocksdb";
//            String dbPath = "/ascs/rocks";
            Options options = new Options();
            options.setCreateIfMissing(true);
            List<byte[]> cfs = RocksDB.listColumnFamilies(options, dbPath);
            if (cfs.size() > 0) {
                for (byte[] cf : cfs) {
                    columnFamilyDescriptors.add(new ColumnFamilyDescriptor(cf, new ColumnFamilyOptions()));
                }
            } else {
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(RocksDB.DEFAULT_COLUMN_FAMILY, new ColumnFamilyOptions()));
            }
            DBOptions dbOptions = new DBOptions();
            dbOptions.setCreateIfMissing(true);
            rocksDB = RocksDB.open(dbOptions, dbPath, columnFamilyDescriptors, columnFamilyHandles);
            reconstitutionMap();
        } catch (Exception e) {
            log.error("创建rocksdb异常", e);
        }
    }

    /**
     * 删除表
     *
     * @param tableName
     */
    public void dropTable(String tableName) {
        tableName = tableName.toLowerCase();
        Integer integer = isexsitTable(tableName);
        if (integer != null) {
            try {

                rocksDB.dropColumnFamily(columnFamilyHandles.get(integer));
                columnFamilyHandles.remove(integer.intValue());
                columnFamilyDescriptors.remove(integer.intValue());
                columnFamilyHandleMap.remove(tableName);
                log.debug("删除rocksdb表名为:" + tableName);
            } catch (RocksDBException e) {
                log.error("删除rocksdb的表异常,表名:" + tableName, e);
            }
        } else {
            log.error("表不存在无法删除,表名:" + tableName);
        }
    }

    /**
     * 创建表
     *
     * @param tableName
     */
    public void createTable(String tableName) {
        try {
            tableName = tableName.toLowerCase();
            Integer integer = isexsitTable(tableName);
            if (integer == null) {
                ColumnFamilyHandle columnFamilyHandle = rocksDB.createColumnFamily(
                        new ColumnFamilyDescriptor(tableName.getBytes(), new ColumnFamilyOptions()));
                columnFamilyDescriptors.add(new ColumnFamilyDescriptor(tableName.getBytes(), new ColumnFamilyOptions()));
                columnFamilyHandles.add(columnFamilyHandle);
                columnFamilyHandleMap.put(tableName, columnFamilyHandle);
                log.debug("创建rocksdb表名为:" + tableName);
            }
        } catch (RocksDBException e) {
            log.error("创建rocksdb的表异常,表名:" + tableName, e);
        }

    }

    /**
     * 判断是否存在该表,并获取该表对应的删除标志
     *
     * @return
     */
    private Integer isexsitTable(String tableName) {
        tableName = tableName.toLowerCase();
        Integer co = null;
        for (int i = 0; i < columnFamilyDescriptors.size(); i++) {
            if (new String(columnFamilyDescriptors.get(i).columnFamilyName()).equals(tableName)) {
                co = i;
                break;
            }
        }
        return co;
    }

    public void put(String tableName, String key) {
        try {
            tableName = tableName.toLowerCase();
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                rocksDB.put(columnFamilyHandle, key.getBytes(), new byte[]{});
            } else {
                log.debug(tableName + "表不存在,key存储失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("添加rocksDB:key值数据异常", e);
        }
    }

    public void putStr(String tableName, String key, String value) {
        try {
            tableName = tableName.toLowerCase();
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                rocksDB.put(columnFamilyHandle, key.getBytes(), value.getBytes());
            } else {
                log.debug(tableName + "表不存在,key存储失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("添加rocksDB:key值数据异常", e);
        }
    }

    public String getStr(String tableName, String key) {
        tableName = tableName.toLowerCase();
        String object = null;
        try {
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                byte[] bytes = new byte[0];
                bytes = rocksDB.get(columnFamilyHandle, key.getBytes());
                if (bytes != null) {
                    object = new String(bytes);
                }
            } else {
                log.debug(tableName + "表不存在,key获取失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("获取rocksDB:key值数据异常", e);
        }
        return object;
    }

    public void put(String tableName, String key, Object value) {
        try {
            tableName = tableName.toLowerCase();
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                rocksDB.put(columnFamilyHandle, key.getBytes(), JSON.toJSONString(value).getBytes());
            } else {
                log.debug(tableName + "表不存在,key存储失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("添加rocksDB:key值数据异常", e);
        }
    }


    public void remove(String tableName, String key) {
        try {
            tableName = tableName.toLowerCase();
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                rocksDB.delete(columnFamilyHandle, key.getBytes());
            } else {
                log.debug(tableName + "表不存在,key存储失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("添加rocksDB:key值数据异常", e);
        }
    }

    /**
     * 是否存在这个key
     *
     * @param tableName
     * @param key
     * @return
     */
    public boolean isexsitkey(String tableName, String key) {
        tableName = tableName.toLowerCase();
        boolean arg = false;
        try {
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                byte[] bytes = new byte[0];
                bytes = rocksDB.get(columnFamilyHandle, key.getBytes());
                if (bytes != null) {
                    arg = true;
                }
            }
        } catch (RocksDBException e) {
            log.error("获取rocksDB:key值数据异常", e);
        }
        return arg;
    }


    public <T> T get(String tableName, String key, Class<T> cls) {
        tableName = tableName.toLowerCase();
        T object = null;
        try {
            if (columnFamilyHandleMap.containsKey(tableName)) {
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                byte[] bytes = new byte[0];
                bytes = rocksDB.get(columnFamilyHandle, key.getBytes());
                if (bytes != null) {
                    object = (T) JSON.parseObject(new String(bytes), cls);
                }
            } else {
                log.debug(tableName + "表不存在,key获取失败" + key);
            }
        } catch (RocksDBException e) {
            log.error("获取rocksDB:key值数据异常", e);
        }
        return object;
    }


    /**
     * 批量获取一张表的所有key的内容
     *
     * @param tableName
     * @param keys
     * @param cls
     */
    public <T> Map<String, T> multiGet(String tableName, List<String> keys, Class<T> cls) {
        tableName = tableName.toLowerCase();
        Map<String, T> datas = new HashMap<>();
        try {
            if (columnFamilyHandleMap.containsKey(tableName)) {
                List<byte[]> keysbyte = new ArrayList<byte[]>();
                for (String key : keys) {
                    keysbyte.add(key.getBytes());
                }
                ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
                List<ColumnFamilyHandle> handleList = new ArrayList<>();
                handleList.add(columnFamilyHandle);
                Map<byte[], byte[]> map = rocksDB.multiGet(handleList, keysbyte);
                for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                    T t = JSON.parseObject(new String(entry.getValue()), cls);
                    datas.put(new String(entry.getKey()), t);
                }

            }
        } catch (RocksDBException e) {
            log.error("获取rocksDB:key值数据异常", e);
        }
        return datas;
    }


    /**
     * 迭代整个库的key和value
     *
     * @param tableName
     * @param iteratorConsumer
     */
    public void ConsumeQuery(String tableName, Consumer<RocksIterator> iteratorConsumer) {
        tableName = tableName.toLowerCase();
        if (columnFamilyHandleMap.containsKey(tableName)) {
            ColumnFamilyHandle columnFamilyHandle = columnFamilyHandleMap.get(tableName);
            RocksIterator iter = rocksDB.newIterator(columnFamilyHandle);
            iteratorConsumer.accept(iter);
//            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
//                System.out.println(new String(iter.key()) + ":" + new String(iter.value()));
//            }

        }
    }


    public void dropAllTable() {
        columnFamilyHandleMap.forEach((p, v) -> {
            if (!"default".equalsIgnoreCase(p)) {
                dropTable(p);
            }
        });
    }

    /**
     * 关闭rockDB连接
     */
    private void close() {
        rocksDB.close();
        //用于操作数据库加key的值
        columnFamilyHandles = new ArrayList<>();
        //用于创建数据库或者删除
        columnFamilyDescriptors = new ArrayList<>();
        //用于存放key对应的表操作使用的类
        columnFamilyHandleMap = new ConcurrentHashMap<>();
    }

    public void restart() {
        close();
        open();
    }

    public static void main(String[] args) throws InterruptedException {
        RocksdbTableUtil rocksdbTableUtil = new RocksdbTableUtil();
        rocksdbTableUtil.ConsumeQuery("isphotoCache", iter -> {
            for (iter.seekToFirst(); iter.isValid(); iter.next()) {
                String s = new String(iter.key());
                System.out.println(s);
                IsPhotoDto isphotoCache = rocksdbTableUtil.get("isphotoCache", s, IsPhotoDto.class);
                System.out.println(isphotoCache);
            }
        });
    }
}
