package com.ltmonitor.service;

import com.ltmonitor.entity.LineSegment;

import java.util.List;

/**
 * @author www.jt808.com
 * 
 */
public interface ILineSegmentService extends IBaseService<Long, LineSegment> {

	List<LineSegment> getLineSegments(long routeId);
}
