package net.fxft.ascsareavoice.service.j808.service;

import com.ltmonitor.jt808.protocol.T808Message;
import net.fxft.gateway.protocol.DeviceMsg;

public interface IMessageSender {
	
	boolean Send808Message(T808Message tm, String toDevTopic, String toChannelId);

	boolean Send808Message(DeviceMsg dm, String toDevTopic, String toChannelId);

//	boolean Send808Message(String simNo, byte[] barr, String toDevTopic, String toChannelId);

}
