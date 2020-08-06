package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.RouteSegment;
import net.fxft.ascsareavoice.ltmonitor.service.IRouteSegmentService;
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
