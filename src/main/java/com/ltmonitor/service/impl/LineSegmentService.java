package com.ltmonitor.service.impl;

import com.ltmonitor.entity.LineSegment;
import com.ltmonitor.service.ILineSegmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("lineSegmentService")
public class LineSegmentService extends BaseService<Long, LineSegment> implements ILineSegmentService {


	public LineSegmentService() {
		super(LineSegment.class);
	}

	public List<LineSegment> getLineSegments(long routeId) {
		String hql = "from LineSegment where routeId = ? and deleted = ?";
		List<LineSegment> result = this.query(hql, new Object[] {
				routeId, false });

		return result;
	}
	

}
