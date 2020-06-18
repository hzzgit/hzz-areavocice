package net.fxft.ascsareavoice.service;

import com.ltmonitor.entity.GPSRealData;

public interface IMessageProcessService {

	/**
	 * 消息处理
	 * @param msgFromTerminal
	 */
	void processMsg(GPSRealData rd);

}