package net.fxft.ascsareavoice.service.j808.service.impl;

import com.ltmonitor.entity.GPSRealData;
import net.fxft.ascsareavoice.ltmonitor.entity.TerminalCommand;
import net.fxft.ascsareavoice.service.IRealDataService;
import net.fxft.ascsareavoice.service.j808.service.ICommandHandler;
import net.fxft.ascsareavoice.service.j808.service.IMessageSender;
import net.fxft.common.util.BasicUtil;
import net.fxft.gateway.protocol.DevMsgAttr;
import net.fxft.gateway.protocol.DevMsgAttrConst;
import net.fxft.gateway.protocol.DeviceMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommandHandler implements ICommandHandler {

    @Autowired
    private IMessageSender messageSender;
    @Autowired
    private IRealDataService realDataService;

    @Override
    public void OnRecvCommand(DeviceMsg tm, TerminalCommand tc,GPSRealData gpsRealData) {
        if (gpsRealData.isOnline() == false) {
            tc.setStatus(TerminalCommand.STATUS_OFFLINE);
            tc.setRemark("cmdproc offline.");
        } else {
            DevMsgAttr msgAttr = new DevMsgAttr();
            msgAttr.loadDefaultValue();
            msgAttr.addExtend(DevMsgAttrConst.KEY_TERMINALCOMMAND_ID, tc.getEntityId());
            tm.setDevMsgAttr(msgAttr);
            String topic = gpsRealData.getToDevTopic();
            if (BasicUtil.isEmpty(topic)) {
                tc.setStatus(TerminalCommand.STATUS_OFFLINE);
                if (tc.getCmdType()!=0x9208){
                    tc.setRemark("ToDevTopic is empty.");
                }
            }else {

                boolean res = messageSender.Send808Message(tm, topic, null);
                tc.setSN(tm.getMsgSerialNo());
                tc.setStatus(res ? TerminalCommand.STATUS_PROCESSING
                        : TerminalCommand.STATUS_FAILED);
                if (tc.getCmdType()!=0x9208){
                    if (res) {
                        tc.setRemark("kafka topic is " + topic);
                    } else {
                        tc.setRemark("send kafka failed.");
                    }
                }

            }
        }
    }

//    @Override
//    public boolean OnRecvCommand(T808Message tm, TerminalCommand tc) {
//        if (realDataService.isOnline(tc.getSimNo()) == false) {
//            tc.setStatus(TerminalCommand.STATUS_OFFLINE);
//            return false;
//        } else {
//            DevMsgAttr msgAttr = new DevMsgAttr();
//            msgAttr.loadDefaultValue();
//            msgAttr.addExtend(DevMsgAttrConst.KEY_TERMINALCOMMAND_ID, tc.getEntityId());
//            tm.setDevMsgAttr(msgAttr);
//            GPSRealData gpsRealData = realDataService.getGpsRealData(tc.getSimNo());
//            boolean res = messageSender.Send808Message(tm, gpsRealData.getToDevTopic(), null);
//            tc.setSN(tm.getHeader().getMessageSerialNo());
//            tc.setStatus(res ? TerminalCommand.STATUS_PROCESSING
//                    : TerminalCommand.STATUS_FAILED);
//            return res;
//        }
//    }
}
