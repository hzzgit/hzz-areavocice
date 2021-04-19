package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.IMessageBody;
import lombok.extern.slf4j.Slf4j;

/**
 * 0200定位信息
 */
@Slf4j
public class JT_0200_WORKCARD extends JT_0204_WORKCARD implements IMessageBody {

    @Override
    public int getMsgType() {
        return 512;
    }

}
