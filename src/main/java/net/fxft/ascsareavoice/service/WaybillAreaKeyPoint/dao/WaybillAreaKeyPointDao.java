package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao;

import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.AreaPointDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.AreaOrderDO;
import net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.dao.DO.SimNoOrderDO;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：hzz
 * @description：运单关键点停车dao
 * @date ：2020/10/21 9:29
 */
@Service
public class WaybillAreaKeyPointDao {


    @Autowired
    private JdbcUtil jdbcUtil;


    /**
     * 查询所有的区域点位缓存
     */
    public List<AreaPointDO> searchAreaPoint(){
        String sql="select a.id areaId,a.name,b.cfgradius,b.latitude,b.longitude,b.maptype,b.id pointid" +
                " from keypoint_area a left join keypoint_areapoint b\n" +
                "on a.id=b.areaid where 1=1 and a.state=1 and b.deleted=false";
        List<AreaPointDO> query = jdbcUtil.sql(sql).query(AreaPointDO.class);
        return  query;
    }


    /**
     * 查询订单和区域
     * @return
     */
    public List<AreaOrderDO> searchAreaOrder(){
        String sql="SELECT\n" +
                "\tb.orderid,\n" +
                "\tb.areaid,\n" +
                "\tb.cfgparkdisplacedistance,\n" +
                "\tb.cfgparkdisplacetime,\n" +
                "\tb.cfgparktime,a.userid\n" +
                "FROM\n" +
                "\tkeypoint_ordermanage a\n" +
                "\tLEFT JOIN keypoint_orderbyarea b ON a.id = b.orderid\n" +
                "WHERE\n" +
                "\ta.state = 1 \n" +
                "\tAND b.deleted = FALSE \n" +
                "\tand (a.byTime=0 or (a.byTime=1 and a.endTime>=SYSDATE() and a.startTime <=SYSDATE())) ";

        List<AreaOrderDO> query = jdbcUtil.sql(sql).query(AreaOrderDO.class);
        return  query;
    }


    /**
     * 查询simNo和订单id的关系，订单生效，且未超时，
     * @return
     */
    public List<SimNoOrderDO> searchsimNoOrder(){
        String sql="SELECT\n" +
                "\tc.orderid,\n" +
                "\tc.simNo\n" +
                "FROM\n" +
                "\tkeypoint_ordermanage a\n" +
                "\tleft join keypoint_orderbysimno c on a.id=c.orderid\n" +
                "WHERE\n" +
                "\ta.state = 1 \n" +
                "\tand c.deleted=false\n" +
                "\n" +
                "\tand (a.byTime=0 or (a.byTime=1 and a.endTime>=SYSDATE() and a.startTime <=SYSDATE() ))";
        List<SimNoOrderDO> query = jdbcUtil.sql(sql).query(SimNoOrderDO.class);
        return  query;
    }

}
