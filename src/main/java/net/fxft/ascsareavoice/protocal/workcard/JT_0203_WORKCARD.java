package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.IMessageBody;
import lombok.extern.slf4j.Slf4j;

/**
 * 0203上班打卡信息
 */
@Slf4j
public class JT_0203_WORKCARD extends JT_0204_WORKCARD implements IMessageBody {


    @Override
    public int getMsgType() {
        return 515;
    }
}
