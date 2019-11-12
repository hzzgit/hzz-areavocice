package net.fxft.gatewaybusi.service.impl;

import com.ltmonitor.jt808.protocol.T808Message;
import net.fxft.gatewaybusi.dataprocess.Process0200_RealData;
import net.fxft.gatewaybusi.service.IMessageProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息分发类
 * @author admin
 *
 */
@Service
public class MessageProcessService implements IMessageProcessService {

	private static Logger log = LoggerFactory.getLogger(MessageProcessService.class);

	@Autowired
	private Process0200_RealData process0200_realData;

	/**
	 * 开始处理收到的完整的808数据包
	 */
	public void processMsg(T808Message msgFromTerminal) {
		int msgType = msgFromTerminal.getMessageType();
		if (msgType == 0) {
			return;
		}
		try {
			// /如果有分包，如果不是0x0801的拍照上传包，则需要先进行分包聚合成完整的包后，才能交给gpsDataService处理
			if (msgType == 0x0200){
				// gps数据处理服务
				process0200_realData.processData(msgFromTerminal.getSimNo(),msgFromTerminal);
			}
		} catch (Exception ex) {
			log.error("MessageProcessService处理出错！", ex);
		}
	}

}
