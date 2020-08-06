package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.entity.RouteSegment;

import java.util.List;

/**
 * @author www.jt808.com
 *
 */
public interface IRouteSegmentService extends IBaseService<Long, RouteSegment> {
	

	List<RouteSegment> getRouteSegments(long routeId);
	

}
