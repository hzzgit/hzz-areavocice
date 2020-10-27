package net.fxft.ascsareavoice.service.WaybillArea;

import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.vo.WaybillAreaMainVo;
import net.fxft.ascsareavoice.vo.WaybillAreaPointVo;
import net.fxft.ascsareavoice.vo.WaybillAreaVo;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/8/5 14:44
 */
@Service
public class WaybillAreaDao {
    @Autowired
    private JdbcUtil jdbcUtil;

    @Autowired
    private WaybillAreaService waybillAreaService;

    /**
     * 查询出所有启用的订单围栏配置
     * @return
     */
    public ConcurrentHashMap<String, List<WaybillAreaMainVo>> searchwaybillarea() {
        ConcurrentHashMap<String, List<WaybillAreaMainVo>> data = new ConcurrentHashMap<String, List<WaybillAreaMainVo>>();
        String sql = "select id,startTime,endTime,SimNo,bytime,userid,name from orderareamanage a  where 1=1 \n" +
                "and a.state=1 ";
        List<WaybillAreaVo> query = jdbcUtil.sql(sql).query(WaybillAreaVo.class);
        if (ConverterUtils.isList(query)) {
            for (WaybillAreaVo waybillAreaVo : query) {
                List<WaybillAreaMainVo> datas = new ArrayList<>();
                WaybillAreaMainVo waybillAreaMainVo = new WaybillAreaMainVo();
                Date endTime = waybillAreaVo.getEndTime();
                waybillAreaMainVo.setEndTime(endTime);
                waybillAreaMainVo.setId(waybillAreaVo.getId());
                waybillAreaMainVo.setSimNo(waybillAreaVo.getSimNo());
                waybillAreaMainVo.setStartTime(waybillAreaVo.getStartTime());
                waybillAreaMainVo.setUserid(waybillAreaVo.getUserid());
                waybillAreaMainVo.setBytime(waybillAreaVo.getBytime());
                waybillAreaMainVo.setName(waybillAreaVo.getName());
                if (data.containsKey(waybillAreaVo.getSimNo())) {
                    datas = data.get(waybillAreaMainVo.getSimNo());
                }
                if (waybillAreaVo.getBytime() == 1) {//如果要根据时间
                    if (endTime.getTime() < System.currentTimeMillis()) {//如果大于了当前时间，就算他过了
                        continue;
                    }
                }
                datas.add(waybillAreaMainVo);
                data.put(waybillAreaVo.getSimNo(), datas);
            }
        }
        return data;
    }

    /**
     * 根据每个订单配置获取到点位进出情况
     * @param data
     */
    public void searchwaybillareapoint(ConcurrentHashMap<String, List<WaybillAreaMainVo>> data) {
        String sql = "select a.simNo,b.id,b.longitude,b.latitude,b.maptype,b.pointtype,b.orderid,b.validradius from orderareapoint b,orderareamanage a" +
                " where b.orderid=a.id " +
                " and a.state=1  ";
        List<WaybillAreaPointVo> query = jdbcUtil.sql(sql).query(WaybillAreaPointVo.class);
        if (ConverterUtils.isList(query)) {
            for (WaybillAreaPointVo waybillAreaPointVo : query) {
                String simNo = waybillAreaPointVo.getSimNo();
                List<WaybillAreaPointVo> waybillAreaPointVos = new ArrayList<>();
                if (data.containsKey(simNo)) {
                    List<WaybillAreaMainVo> waybillAreaMainVos = data.get(simNo);
                    for (WaybillAreaMainVo waybillAreaMainVo : waybillAreaMainVos) {
                        if (waybillAreaPointVo.getOrderid().equals(waybillAreaMainVo.getId())) {
                            waybillAreaPointVos = waybillAreaMainVo.getWaybillAreaPointVos();
                            waybillAreaPointVos.add(waybillAreaPointVo);
                        }
                    }

                }
            }
        }
    }
}
