package net.fxft.ascsareavoice.ltmonitor.service;

import net.fxft.ascsareavoice.ltmonitor.entity.LineSegment;

import java.util.List;

/**
 * @author www.jt808.com
 * 
 */
public interface ILineSegmentService extends IBaseService<Long, LineSegment> {

	List<LineSegment> getLineSegments(long routeId);
}
