package net.fxft.ascsareavoice.service.takingPhotosbyTime.dao;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.dao.dto.TakingPhotoBySimNoDto;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.Takingphotosbytime;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

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
    private List<Takingphotosbytime> searchTakingPhotoConfig() {
        String sql = "select * from takingphotosbytime where deleted =false and  isuse=1 ";
        List<Takingphotosbytime> query = jdbcUtil.sql(sql).query(Takingphotosbytime.class);
        return query;
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
                "\t\tAND a.deleted = FALSE \n" +
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
                "\t\tAND a.deleted = FALSE \n" +
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


}
