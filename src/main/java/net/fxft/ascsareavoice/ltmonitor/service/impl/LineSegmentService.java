package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.LineSegment;
import net.fxft.ascsareavoice.ltmonitor.service.ILineSegmentService;
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
