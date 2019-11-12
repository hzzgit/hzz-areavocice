package net.fxft.gatewaybusi.service;

import com.ltmonitor.jt808.protocol.T808Message;

public interface IMessageProcessService {

	/**
	 * 消息处理
	 * @param msgFromTerminal
	 */
	void processMsg(T808Message msgFromTerminal);

}