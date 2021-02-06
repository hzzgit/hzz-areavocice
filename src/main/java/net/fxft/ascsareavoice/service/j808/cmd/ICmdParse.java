package net.fxft.ascsareavoice.service.j808.cmd;

import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.gateway.protocol.IMsgBody;

public interface ICmdParse {

    IMsgBody parse(TerminalCommand tc) throws Exception;

}
