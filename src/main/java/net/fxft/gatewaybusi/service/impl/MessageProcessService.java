package net.fxft.gatewaybusi.service.impl;

import com.ltmonitor.entity.GPSRealData;
import com.ltmonitor.jt808.protocol.JT_0200;
import com.ltmonitor.jt808.protocol.JT_0704;
import com.ltmonitor.jt808.protocol.T808Message;
import net.fxft.gatewaybusi.dataprocess.Process0200_RealData;
import net.fxft.gatewaybusi.service.IMessageProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
	public void processMsg(GPSRealData rd) {
		try{
				process0200_realData.processData(rd.getSimNo(),rd);
		} catch (Exception ex) {
			log.error("MessageProcessService处理出错！", ex);
		}
	}

}
