package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;

/**
 * 分段限速路段，每个路段是线路上的人工在地图上指定的几个拐点组成的子路段
 * @author DELL
 *
 */
@DbTable(value = "MapArea", camelToUnderline = false)

public class RouteSegment implements  Serializable {

	@DbId
	@DbColumn(columnName = "segId")
	private long entityId;
	
	private String name;
	
	//延时报警时间
	private int delay;
	
	private double maxSpeed;
	//开始点
	private long startSegId;
	//结束点
	private long endSegId;
	//所在的线路id
	private long routeId;
	//分段上的拐点集合
	private String strPoints;
	
	public RouteSegment()
	{
		maxSpeed = 50;
		delay=0;
	}
	
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	public double getMaxSpeed() {
		return maxSpeed;
	}
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}
	public long getStartSegId() {
		return startSegId;
	}
	public void setStartSegId(long startSegId) {
		this.startSegId = startSegId;
	}
	public long getEndSegId() {
		return endSegId;
	}
	public void setEndSegId(long endSegId) {
		this.endSegId = endSegId;
	}
	public long getRouteId() {
		return routeId;
	}
	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}
	public String getStrPoints() {
		return strPoints;
	}
	public void setStrPoints(String strPoints) {
		this.strPoints = strPoints;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
