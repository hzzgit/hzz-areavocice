package com.ltmonitor.service.impl;

import com.ltmonitor.entity.RouteSegment;
import com.ltmonitor.service.IRouteSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("routeSegmentService")
public class RouteSegmentService extends BaseService<Long, RouteSegment> implements IRouteSegmentService {


	public RouteSegmentService() {
		super(RouteSegment.class);
	}

	@Override
	public List<RouteSegment> getRouteSegments(long routeId) {
		String hql = "from RouteSegment where routeId = ? ";
		List<RouteSegment> result = this.query(hql, new Object[] {
				routeId });

		return result;
	}
	
	
	

}
