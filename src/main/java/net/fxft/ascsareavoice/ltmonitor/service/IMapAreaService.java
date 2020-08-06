package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.entity.LineSegment;
import net.fxft.ascsareavoice.ltmonitor.entity.MapArea;

import java.util.List;

public interface IMapAreaService extends IBaseService<Long, MapArea> {

	void saveRoute(MapArea ec, List<LineSegment> segments);

	void saveRoute2(MapArea ec);

	List<MapArea> getMapAreaList(List<Long> depIdList);

	
	

}