package net.fxft.ascsareavoice.service.j808.service;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.gateway.protocol.DeviceMsg;


public interface ICommandHandler {

	void OnRecvCommand(DeviceMsg tm, TerminalCommand tc, GPSRealData gpsRealData);

//	boolean OnRecvCommand(T808Message tm, TerminalCommand tc);

}