package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import com.ltmonitor.entity.GPSRealData;
import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.service.JT808Constants;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.j808.service.ICommandHandler;
import net.fxft.ascsareavoice.service.j808.service.T808Manager;
import net.fxft.ascsareavoice.service.j808.cmd.ICmdParse;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.PictureParam;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.util.BasicUtil;
import net.fxft.gateway.protocol.DeviceMsg;
import net.fxft.gateway.protocol.IMsgBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ：hzz
 * @description：命令下发逻辑类
 * @date ：2021/1/21 17:01
 */
@Service
@Slf4j
public class TemSendService {

    @Autowired
    private RealDataService realDataService;

    @Autowired
    private JdbcUtil jdbcUtil;

    @Autowired
    private ICommandHandler commandHandler;

    @Autowired
    @Qualifier("CmdParse_8801")
    private ICmdParse cmdParse_8801;

    /**
     * 发送立即拍照指令，返回指令表主键
     *
     * @param simNo    simNo终端卡号
     * @param channel  要拍照的通道号
     * @param userId   配置这个定时拍照任务的用户id
     * @param userName 配置这个定时拍照任务的用户名称
     * @return
     */
    public long sendTakePhoto(String simNo, int channel, long userId, String userName, Date checkTime, GPSRealData gpsRealData) {

        long cmdId = 0;
        PictureParam p = new PictureParam();
        p.setChannel(channel);
        VehicleData vehicleData = realDataService.getVehicleData(simNo);
        if (vehicleData != null) {
            TerminalCommand tc = new TerminalCommand();
            tc.setCmdType(JT808Constants.CMD_TAKE_PHOTO);
            tc.setCmdData(p.getCommandString());
            tc.setVehicleId(vehicleData.getEntityId());
            tc.setPlateNo(vehicleData.getPlateNo());
            tc.setSimNo(vehicleData.getSimNo());
            tc.setUserId(userId);
            tc.setOwner(userName);
            tc.setCreateDate(checkTime);
            tc.setUpdateDate(checkTime);
            Integer cmdType = tc.getCmdType();
            String strCmd = Integer.toHexString(cmdType);
            while (strCmd.length() < 4) {
                strCmd = "0" + strCmd;
            }
            strCmd = "0x" + strCmd;
            DeviceMsg tm = null;
            tm = Parse(tc);
            try {
                if (tm != null) {
                    //将最新的指令转换成T808message类，通知T808Manager下发给指定设备
                    commandHandler.OnRecvCommand(tm, tc, gpsRealData);
                } else {
                    tc.setStatus(TerminalCommand.STATUS_INVALID);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                tc.setStatus(TerminalCommand.STATUS_INVALID);
                tc.setRemark(ex.getMessage());
            }
            jdbcUtil.insert(tc).insertColumn(ColumnSet.all()).execute(true);
            cmdId = tc.getEntityId();
            log.debug("定时拍照:车辆发送命令成功,simNo=" + simNo + ",channel=" + channel + ",cmdId=" + cmdId);
        } else {
            log.debug("定时拍照:车辆不存在无法发送,simNo=" + simNo);
        }
        return cmdId;

    }


    /**
     * 不对非法命令格式进行解析，在命令录入时确保格式正确
     *
     * @param tc
     * @return
     * @throws Exception
     */
    public DeviceMsg Parse(TerminalCommand tc) {
        DeviceMsg ts = new DeviceMsg();
        ts.setDeviceNo(tc.getSimNo());
        ts.setMsgSerialNo(T808Manager.getSerialNo());
        ts.setMsgType(tc.getCmdType());
        tc.setStatus(TerminalCommand.STATUS_PROCESSING);
        tc.setSN(ts.getMsgSerialNo());
        ICmdParse cmdParse = null;
        try {
            IMsgBody content = cmdParse_8801.parse(tc);
            ts.setMsgBody(content);
            return ts;
        } catch (Exception e) {
            log.error("CmdParse执行出错！cls=" + cmdParse.getClass(), e);
            tc.setRemark("CmdParse执行出错！cls=" + cmdParse.getClass() + "; ex=" + BasicUtil.exceptionMsg(e));
            return null;
        }
    }


}
