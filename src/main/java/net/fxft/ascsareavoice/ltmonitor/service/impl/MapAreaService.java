package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.LineSegment;
import net.fxft.ascsareavoice.ltmonitor.entity.MapArea;
import net.fxft.ascsareavoice.ltmonitor.service.IMapAreaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service("mapAreaService")
public class MapAreaService extends BaseService<Long, MapArea> implements IMapAreaService {

    public MapAreaService() {
        super(MapArea.class);
    }

    @Override
    public Collection<MapArea> loadAll() {
        String hsql = "select * from MapArea where deleted = 0";
        return this.query(hsql);
    }

    /**
     * 保存线路 及线路下的所有线段
     */
    @Transactional
    public void saveRoute(MapArea ec, List<LineSegment> segments) {
        this.saveOrUpdate(ec);
        if (segments.size() > 0) {
            for (LineSegment ls : segments) {
                ls.setRouteId(ec.getEntityId());
                jdbc.defaultDao(LineSegment.class)
                        .saveOrUpdate(ls);
            }
//            LineSegmentDao.saveOrUpdateAll(segments);
        }

    }

    /**
     * 保存线路 及线路下的所有线段
     */
    public void saveRoute2(MapArea ec) {
        this.saveOrUpdate(ec);
    }

    /**
     * 获取部门权限下的所有区域
     *
     * @param depIdList
     * @return
     */
    public List<MapArea> getMapAreaList(List<Long> depIdList) {
        List<MapArea> ls = jdbc.select(MapArea.class)
                .andNotDeleted()
                .andIn("depId", depIdList)
                .query();
        return ls;
    }

}
