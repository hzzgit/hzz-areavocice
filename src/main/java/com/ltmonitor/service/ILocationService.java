package com.ltmonitor.service;

public interface ILocationService {

	public String getLocation(double lat, double lng, String remark);

	public String getMapType();
}