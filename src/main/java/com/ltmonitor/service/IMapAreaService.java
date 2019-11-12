package com.ltmonitor.service;

import com.ltmonitor.entity.LineSegment;
import com.ltmonitor.entity.MapArea;

import java.util.List;

public interface IMapAreaService extends IBaseService<Long,MapArea> {

	void saveRoute(MapArea ec, List<LineSegment> segments);

	void saveRoute2(MapArea ec);

	List<MapArea> getMapAreaList(List<Long> depIdList);

	
	

}