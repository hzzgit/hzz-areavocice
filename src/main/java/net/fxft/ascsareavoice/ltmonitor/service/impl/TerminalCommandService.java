package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.ascsareavoice.ltmonitor.service.ITerminalCommandService;
import com.ltmonitor.util.DateUtil;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service("terminalCommandService")
public class TerminalCommandService extends BaseService<Long, TerminalCommand> implements ITerminalCommandService {

    public TerminalCommandService() {
        super(TerminalCommand.class);
    }

    public List<TerminalCommand> getLatestCommand() {
        String hsql = "select * from TerminalCommand where CreateDate > ? and Status = ? ";
        Date startTime = DateUtil.getDate(DateUtil.now(), Calendar.MINUTE, -5);
        List<TerminalCommand> result = jdbc.sql(hsql).setNotPrint()
                .addIndexParam(startTime, TerminalCommand.STATUS_NEW).query(TerminalCommand.class);
        return result;
    }

    /**
     * 根据流水号找到命令
     *
     * @param sn
     * @return
     */
    @Override
    public TerminalCommand getCommandBySn(long sn) {
        String hsql = "select * from TerminalCommand where SN = ? and createDate > ? order by createDate desc";
        Date startDate = DateUtil.getDate(new Date(), Calendar.HOUR_OF_DAY, -1);
        TerminalCommand tc = (TerminalCommand) this.find(hsql,
                new Object[]{sn, startDate});
        return tc;
    }

    /**
     * 根据流水号找到命令
     *
     * @param sn
     * @return
     */
    @Override
    public TerminalCommand getCommandBySn(long sn, int cmdType) {
        String hsql = "select * from TerminalCommand where SN = ? and createDate > ? and cmdType = ? order by createDate desc";
        Date startDate = DateUtil.getDate(new Date(), Calendar.HOUR_OF_DAY, -1);
        TerminalCommand tc = (TerminalCommand) this.find(hsql,
                new Object[]{sn, startDate, cmdType});
        return tc;
    }

    @Override
    public TerminalCommand getCommandBySn(long sn, int cmdType, String simNo) {
        String hsql = "select * from TerminalCommand where SN = ? and createDate > ? and cmdType = ? and simNo = ? order by createDate desc";
        Date startDate = DateUtil.getDate(new Date(), Calendar.DAY_OF_YEAR, -3);
        TerminalCommand tc = (TerminalCommand) this.find(hsql,
                new Object[]{sn, startDate, cmdType, simNo});
        return tc;
    }

    /**
     * 根据命令类型找到最近下发的指令命令
     */
    @Override
    public TerminalCommand getCommandByCmdType(String simNo, int cmdType) {
        String hsql = "select * from TerminalCommand where simNo = ? and createDate > ? and cmdType = ? order by createDate desc";
        Date startDate = DateUtil.getDate(new Date(), Calendar.HOUR_OF_DAY, -1);
        TerminalCommand tc = (TerminalCommand) this.find(hsql,
                new Object[]{simNo, startDate, cmdType});
        return tc;
    }

    /**
     * 根据命令类型找到最近下发的指令命令
     */
    @Override
    public TerminalCommand getCommandByCmdType(String simNo, int cmdType, int subCmdType) {
        String hsql = "select * from TerminalCommand where simNo = ? and createDate > ? and cmdType = ? and subCmdType = ? order by createDate desc";
        Date startDate = DateUtil.getDate(new Date(), Calendar.HOUR_OF_DAY, -1);
        TerminalCommand tc = (TerminalCommand) this.find(hsql,
                new Object[]{simNo, startDate, cmdType, subCmdType});
        return tc;
    }

}
