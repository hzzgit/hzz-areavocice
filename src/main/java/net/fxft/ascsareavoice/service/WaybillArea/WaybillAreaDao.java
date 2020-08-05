package net.fxft.ascsareavoice.service.WaybillArea;

import com.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.vo.WaybillAreaMainVo;
import net.fxft.ascsareavoice.vo.WaybillAreaPointVo;
import net.fxft.ascsareavoice.vo.WaybillAreaVo;
import net.fxft.common.jdbc.JdbcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    public ConcurrentHashMap<String, WaybillAreaMainVo> searchwaybillarea(){
        ConcurrentMap<String, Boolean> crossMap =new ConcurrentHashMap<>();
        ConcurrentHashMap<String, WaybillAreaMainVo> data=new ConcurrentHashMap<String, WaybillAreaMainVo>();
        String sql="select id,startTime,endTime,SimNo,bytime,userid,name from orderareamanage a  where 1=1 \n" +
                "and a.state=1 and a.endTime > SYSDATE()";
        List<WaybillAreaVo> query = jdbcUtil.sql(sql).query(WaybillAreaVo.class);
        if(ConverterUtils.isList(query)){
            for (WaybillAreaVo waybillAreaVo : query) {
                WaybillAreaMainVo waybillAreaMainVo =new WaybillAreaMainVo();
                waybillAreaMainVo.setEndTime(waybillAreaVo.getEndTime());
                waybillAreaMainVo.setId(waybillAreaVo.getId());
                waybillAreaMainVo.setSimNo(waybillAreaVo.getSimNo());
                waybillAreaMainVo.setStartTime(waybillAreaVo.getStartTime());
                waybillAreaMainVo.setUserid(waybillAreaVo.getUserid());
                waybillAreaMainVo.setBytime(waybillAreaVo.getBytime());
                waybillAreaMainVo.setName(waybillAreaVo.getName());
                data.put(waybillAreaVo.getSimNo(),waybillAreaMainVo );
            }
        }
        waybillAreaService.setCrossMap(crossMap);
        return data;
    }

    public void searchwaybillareapoint(  ConcurrentHashMap<String, WaybillAreaMainVo> data){
        String sql="select a.simNo,b.id,b.longitude,b.latitude,b.maptype,b.pointtype,b.orderid from orderareapoint b,orderareamanage a" +
                " where b.orderid=a.id " +
                " and a.state=1 and a.endTime > SYSDATE()  ";
        List<WaybillAreaPointVo> query = jdbcUtil.sql(sql).query(WaybillAreaPointVo.class);
        if (ConverterUtils.isList(query)) {
            for (WaybillAreaPointVo waybillAreaPointVo : query) {
                String simNo = waybillAreaPointVo.getSimNo();
                List<WaybillAreaPointVo> waybillAreaPointVos=new ArrayList<>();
                if(data.containsKey(simNo)){
                    WaybillAreaMainVo waybillAreaMainVo = data.get(simNo);
                    waybillAreaPointVos=waybillAreaMainVo.getWaybillAreaPointVos();
                    waybillAreaPointVos.add(waybillAreaPointVo);
                }
            }
        }
    }
}
