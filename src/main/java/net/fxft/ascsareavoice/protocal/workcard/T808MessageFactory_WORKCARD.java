package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.IMessageBody;
import com.ltmonitor.jt808.tool.ClassUtils;
import com.ltmonitor.jt808.tool.Tools;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2021/3/8 17:13
 */
public final class T808MessageFactory_WORKCARD {

    public static IMessageBody Create(int version, int messageType, byte[] messageBodyBytes) {
        String nameSpace = T808MessageFactory_WORKCARD.class.getPackage().getName();
        String className = nameSpace + ".JT_" + Tools.ToHexString((long)messageType, 2)+"_WORKCARD".toUpperCase();
        Object messageBody = ClassUtils.getBean(className);
        if (messageBody != null) {
            IMessageBody msg = (IMessageBody)messageBody;
            msg.ReadFromBytes(version, messageBodyBytes);
            return msg;
        } else {
            return null;
        }
    }

}
