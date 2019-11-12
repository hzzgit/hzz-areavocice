package com.ltmonitor.service;

import com.ltmonitor.entity.RouteSegment;

import java.util.List;

/**
 * @author www.jt808.com
 *
 */
public interface IRouteSegmentService extends IBaseService<Long, RouteSegment> {
	

	List<RouteSegment> getRouteSegments(long routeId);
	

}
