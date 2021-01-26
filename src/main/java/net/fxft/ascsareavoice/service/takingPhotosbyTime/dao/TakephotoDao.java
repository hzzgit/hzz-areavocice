package net.fxft.ascsareavoice.service.takingPhotosbyTime.dao;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.DriverInfoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoBySimNoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoByVehicleIdDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytimedetail;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytimeresult;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：hzz
 * @description：定时拍照缓存类
 * @date ：2021/1/21 17:16
 */
@Service
@Slf4j
public class TakephotoDao {

    @Autowired
    private JdbcUtil jdbcUtil;


    /**
     * 查讯所有的定时拍照的配置表
     *
     * @return
     */
    public Map<Integer, Takingphotosbytime> searchTakingPhotoConfig() {
        Map<Integer, Takingphotosbytime> data = new HashMap<>();
        String sql = "select * from takingphotosbytime where deleted =false and  isuse=1 ";
        List<Takingphotosbytime> query = jdbcUtil.sql(sql).query(Takingphotosbytime.class);
        if (ConverterUtils.isList(query)) {
            for (Takingphotosbytime takingphotosbytime : query) {
                data.put(takingphotosbytime.getId(), takingphotosbytime);
            }
        }
        return data;
    }


    /**
     * 查询配置的车对应配置
     */
    public List<TakingPhotoBySimNoDto> searchTakingVehicle() throws Exception {
        long s = System.currentTimeMillis();   //获取开始时间
        String sql = "SELECT\n" +
                "\tc1.id ,v1.SimNo \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\tv.vehicleId \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebyvehicle v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.id = v.mainid \n" +
                "\t\tAND a.deleted = FALSE and a.validendtime >sysdate() \n" +
                "\t\tAND v.deleted = false and a.isuse = 1 \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.vehicleId = v1.vehicleId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE UNION\n" +
                "SELECT\n" +
                "\tc1.id, v1.SimNo \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tv.depId,\n" +
                "\t\ta.id \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebydep v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 and  a.id = v.mainid \n" +
                "\t\tAND a.isuse = 1 \n" +
                "\t\tAND a.deleted = FALSE  and a.validendtime >sysdate()  \n" +
                "\t\tAND v.deleted = FALSE \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.depId = v1.depId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE";

        List<TakingPhotoBySimNoDto> query = jdbcUtil.sql(sql).query(TakingPhotoBySimNoDto.class);
        long e = System.currentTimeMillis(); //获取结束时间
        log.debug("查询定时拍照配置以及对应车辆的列表用时" + (e - s) + "ms");
        return query;
    }

    /**
     * 查询需要移除配置的车辆信息
     */
    public List<TakingPhotoByVehicleIdDto> searchRemoveTakingVehicle() throws Exception {
        long s = System.currentTimeMillis();   //获取开始时间
        String sql = "SELECT\n" +
                "\tc1.id ,v1.vehicleId \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\tv.vehicleId \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebyvehicle v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.id = v.mainid \n" +
                "\t\tAND ( a.deleted >0\n" +
                "\t\t  or  a.isuse = 0   or a.validendtime <sysdate() ) \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.vehicleId = v1.vehicleId \n" +
                " UNION\n" +
                "SELECT\n" +
                "\tc1.id, v1.vehicleId \n" +
                " FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tv.depId,\n" +
                "\t\ta.id \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebydep v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 and  a.id = v.mainid \n" +
                "\t\tAND ( a.deleted >0 \n" +
                "\t\t or  a.isuse = 0  or a.validendtime <sysdate() ) \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.depId = v1.depId ";

        List<TakingPhotoByVehicleIdDto> query = jdbcUtil.sql(sql).query(TakingPhotoByVehicleIdDto.class);
        long e = System.currentTimeMillis(); //获取结束时间
        log.debug("查询定时拍照配置需要移除的对应车辆的列表用时" + (e - s) + "ms");
        return query;
    }


    /**
     * 插入到结果表，
     */
    public Takingphotosbytimeresult insertTakingphotosbytimeresult(long vehicleId, String simNo, long userId,
                                                                   int photonum, double latitude,
                                                                   double longitude, Date sendTime, double speed,
                                                                   String driverName, String certificate, long configid) {
        Takingphotosbytimeresult takingphotosbytimeresult = new Takingphotosbytimeresult();
        takingphotosbytimeresult.setVehicleid(vehicleId);
        takingphotosbytimeresult.setSimno(simNo);
        takingphotosbytimeresult.setUserid(userId);
        takingphotosbytimeresult.setPhotonum(photonum);
        takingphotosbytimeresult.setLatitude(latitude);
        takingphotosbytimeresult.setLongitude(longitude);
        takingphotosbytimeresult.setSendtime(sendTime);
        takingphotosbytimeresult.setSpeed(speed);
        takingphotosbytimeresult.setDrivername(driverName);
        takingphotosbytimeresult.setCertificate(certificate);
        takingphotosbytimeresult.setConfigid(configid);
        jdbcUtil.insert(takingphotosbytimeresult).insertColumn(ColumnSet.all()).execute(true);
        return takingphotosbytimeresult;

    }


    /**
     * 插入到详情表
     */
    public void insertTakingphotosbytimeDetail(long resultId, int channelid,
                                               long commandid, long configid, long vehicleId, int commandtype) {

        Takingphotosbytimedetail takingphotosbytimedetail = new Takingphotosbytimedetail();
        takingphotosbytimedetail.setResultid(resultId);
        takingphotosbytimedetail.setChannelid(channelid);
        takingphotosbytimedetail.setCommandid(commandid);
        takingphotosbytimedetail.setCommandtype(commandtype);
        takingphotosbytimedetail.setConfigid(configid);
        takingphotosbytimedetail.setVehicleid(vehicleId);
        jdbcUtil.insert(takingphotosbytimedetail).insertColumn(ColumnSet.all()).execute();


    }


    /**
     * 查询车辆最后的司机
     *
     * @param vehicleId
     * @return
     */
    public DriverInfoDto searchDriverInfo(long vehicleId) {
        String sql = "select certificationCode,driverName from  drivercardrecordonly where vehicleId=? and isvalid=0";
        DriverInfoDto driverInfoDto = jdbcUtil.sql(sql).addIndexParam(vehicleId).queryFirst(DriverInfoDto.class);
        return driverInfoDto;
    }


    public static void main(String[] args) {
        String sql = "SELECT\n" +
                "\tc1.id ,v1.vehicleId \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\tv.vehicleId \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebyvehicle v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.id = v.mainid \n" +
                "\t\tAND ( a.deleted = true \n" +
                "\t\t or v.deleted = true " +
                " or a.isuse = 0   or a.validendtime <=sysdate() ) \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.vehicleId = v1.vehicleId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE UNION\n" +
                "SELECT\n" +
                "\tc1.id, v1.vehicleId \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tv.depId,\n" +
                "\t\ta.id \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebydep v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 and  a.id = v.mainid \n" +
                "\t\tAND ( a.deleted = true \n" +
                "\t\t or v.deleted = true" +
                " or a.isuse = 0  or a.validendtime <=sysdate() ) \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.depId = v1.depId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE;";

        System.out.println(sql);


        String sql1 = "SELECT\n" +
                "\tc1.id ,v1.SimNo \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\ta.id,\n" +
                "\t\tv.vehicleId \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebyvehicle v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 \n" +
                "\t\tAND a.id = v.mainid \n" +
                "\t\tAND a.deleted = FALSE and a.validendtime >sysdate() \n" +
                "\t\tAND v.deleted = false and a.isuse = 1 \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.vehicleId = v1.vehicleId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE UNION\n" +
                "SELECT\n" +
                "\tc1.id, v1.SimNo \n" +
                "FROM\n" +
                "\t(\n" +
                "\tSELECT\n" +
                "\t\tv.depId,\n" +
                "\t\ta.id \n" +
                "\tFROM\n" +
                "\t\ttakingphotosbytime a,\n" +
                "\t\ttakingphotosbytimebydep v \n" +
                "\tWHERE\n" +
                "\t\t1 = 1 and  a.id = v.mainid \n" +
                "\t\tAND a.isuse = 1 \n" +
                "\t\tAND a.deleted = FALSE  and a.validendtime >sysdate()  \n" +
                "\t\tAND v.deleted = FALSE \n" +
                "\t) c1\n" +
                "\tLEFT JOIN vehicle v1 ON c1.depId = v1.depId \n" +
                "WHERE\n" +
                "\tv1.deleted = FALSE;";
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(sql1);

    }

}
