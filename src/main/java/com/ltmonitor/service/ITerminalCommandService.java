package com.ltmonitor.service;

import com.ltmonitor.entity.TerminalCommand;

import java.util.List;

public interface ITerminalCommandService extends IBaseService<Long, TerminalCommand> {

	List<TerminalCommand> getLatestCommand();

	/**
	 * 根据流水号找到指令
	 * @param sn
	 * @return
	 */
	TerminalCommand getCommandBySn(long sn);
	/**
	 * 根据流水号找到命令
	 *
	 * @param sn
	 * @return
	 */
	TerminalCommand getCommandBySn(long sn, int cmdType);
	TerminalCommand getCommandBySn(long sn, int cmdType, String simNo);
	TerminalCommand getCommandByCmdType(String simNo, int cmdType);
	TerminalCommand getCommandByCmdType(String simNo, int cmdType, int subCmdType);

}
