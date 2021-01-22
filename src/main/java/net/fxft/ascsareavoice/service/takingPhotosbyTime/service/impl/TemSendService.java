package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.ascsareavoice.ltmonitor.entity.VehicleData;
import net.fxft.ascsareavoice.ltmonitor.service.JT808Constants;
import net.fxft.ascsareavoice.ltmonitor.util.ConverterUtils;
import net.fxft.ascsareavoice.service.impl.RealDataService;
import net.fxft.ascsareavoice.service.takingPhotosbyTime.entity.PictureParam;
import net.fxft.common.jdbc.ColumnSet;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.gateway.util.SimNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 发送立即拍照指令，返回指令表主键
     *
     * @param simNo   simNo终端卡号
     * @param channel 要拍照的通道号
     * @param userId  配置这个定时拍照任务的用户id
     * @param userName  配置这个定时拍照任务的用户名称
     * @return
     */
    public long sendTakePhoto(String simNo, int channel, long userId, String userName) {

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
            Integer cmdType = tc.getCmdType();
            String strCmd = Integer.toHexString(cmdType);
            while (strCmd.length() < 4) {
                strCmd = "0" + strCmd;
            }
            strCmd = "0x" + strCmd;
            jdbcUtil.insert(tc).insertColumn(ColumnSet.all()).execute(true);
            cmdId=tc.getEntityId();
            log.debug("定时拍照:车辆发送命令成功,simNo=" + simNo+",channel="+channel+",cmdId="+cmdId);
        } else {
            log.debug("定时拍照:车辆不存在无法发送,simNo=" + simNo);
        }
        return cmdId;

    }


}
