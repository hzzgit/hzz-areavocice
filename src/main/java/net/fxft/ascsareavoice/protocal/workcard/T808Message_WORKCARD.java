package net.fxft.ascsareavoice.protocal.workcard;

import com.ltmonitor.jt808.protocol.IMessageBody;
import com.ltmonitor.jt808.protocol.MyBuffer;
import com.ltmonitor.jt808.protocol.T808Message;
import com.ltmonitor.jt808.protocol.T808MessageHeader;
import com.ltmonitor.jt808.tool.Tools;
import net.fxft.gateway.protocol.DevMsgAttr;
import net.fxft.gateway.protocol.IMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class T808Message_WORKCARD implements Serializable {

    private static Logger log = LoggerFactory.getLogger(T808Message.class);

    public static final int DONE = 1;
    public static final int ERROR = 0;
    public static final int NOT_DONE = 2;
    public static final int MORE = 3;

    private static byte _PrefixID = 0x7E;

    private DevMsgAttr devMsgAttr;
    private T808MessageHeader header = new T808MessageHeader();
    private int status;
    /**
     * 如果是分包，分包是不能解析的，等待和其他包合成一个完整的包才能解析
     */
    private byte[] childPacket;

    private byte[] _checkSum = null;

    private IMsgBody messageContents;

    private String plateNo;

//    private transient String packetDescr;

    private transient String errorMessage;

    private int protocolType;
    private int version;
    private String subMsgType;

    public T808Message_WORKCARD() {
    }

    public T808Message_WORKCARD(DevMsgAttr devMsgAttr, String simNo, int messageType, IMessageBody echoData) {
        this.setMessageContents(echoData);
        this.setHeader(new T808MessageHeader());
        this.getHeader().setMessageType(messageType);
        this.getHeader().setSimId(simNo);
        this.getHeader().setIsPackage(false);
        this.devMsgAttr = devMsgAttr;
    }

    public DevMsgAttr getDevMsgAttr() {
        return devMsgAttr;
    }

//    @Override
//    public int getMsgObjectVersion() {
//        if (getMessageContents() != null) {
//            return getMessageContents().getMsgObjectVersion();
//        }
//        return 0;
//    }

    public void setDevMsgAttr(DevMsgAttr devMsgAttr) {
        this.devMsgAttr = devMsgAttr;
    }

    public String getSimNo() {
        if (header != null)
            return header.getSimId();
        return "";
    }

    public long getUuid() {
        if (devMsgAttr != null) {
            return devMsgAttr.getUuid();
        } else {
            return 0;
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (devMsgAttr != null) {
            sb.append(devMsgAttr.toString());
        }
        sb.append("; ").append(header);
        if (messageContents != null) {
            sb.append("; bodystr{").append(messageContents.toString()).append("}");
//            sb.append("; bodyjson").append(JacksonUtil.toJsonString(messageContents));
        } else if (this.getHeader().getIsPackage()) {
            String str = "分包号:" + this.getHeader().getMessagePacketNo() + ",总包数:"
                    + this.getHeader().getMessageTotalPacketsCount();
            if (childPacket != null && childPacket.length > 0) {
                str += ", 分包长度:" + childPacket.length;
            }
            sb.append("; 分包{").append(str).append("}");
        }
        return sb.toString();

//        String prefix = "header{"+String.valueOf(header)+"}";
//        if (devMsgAttr != null) {
//            prefix = devMsgAttr.toString();
//        }
//        if (messageContents != null) {
//            return prefix + messageContents.toString() + "; \n" + header + "; \nMessageBody" + JacksonUtil.toJsonString(messageContents);
//        } else if (this.getHeader().getIsPackage()) {
//            String str = "分包号:" + this.getHeader().getMessagePacketNo() + ",总包数:"
//                    + this.getHeader().getMessageTotalPacketsCount();
//            if (childPacket != null && childPacket.length > 0) {
//                str += ", 分包长度:" + childPacket.length;
//            }
//            return prefix + str + "; \n" + header;
//        }
//        return prefix + String.valueOf(header);
    }

    public final int getStatus() {
        return status;
    }

    public final void setStatus(int value) {
        status = value;
    }

    /**
     * 报文头
     */
    public final T808MessageHeader getHeader() {
        return header;
    }

    public final void setHeader(T808MessageHeader value) {
        header = value;
    }


    public static byte getPrefixID() {
        return _PrefixID;
    }

    public final int getMessageType() {
        return getHeader().getMessageType();
    }


    public final byte[] getCheckSum() {
        return _checkSum;
    }


    public final IMsgBody getMessageContents() {
        return messageContents;
    }

    public final void setMessageContents(IMsgBody value) {
        messageContents = value;
    }


    public final String getPlateNo() {
        return plateNo;
    }

    public final void setPlateNo(String value) {
        plateNo = value;
    }


//    public final String getPacketDescr() {
//        return packetDescr;
//    }
//
//    public final void setPacketDescr(String value) {
//        packetDescr = value;
//    }


    public final String getErrorMessage() {
        return errorMessage;
    }

    public final void setErrorMessage(String value) {
        errorMessage = value;
    }

    public final byte[] WriteToBytes(int version) {
        MyBuffer buff = new MyBuffer();
        // buff.mark();
        byte[] bodyBytes = null;
        if (getMessageContents() != null && getMessageContents() instanceof IMessageBody) {
            bodyBytes = ((IMessageBody) getMessageContents()).WriteToBytes(version);
        }
        // java.util.ArrayList<Byte> messageBytes = new
        // java.util.ArrayList<Byte>();
        if (bodyBytes != null) {
            header.setMessageSize(bodyBytes.length);
            header.setIsPackage(false);
            byte[] headerBytes = header.WriteToBytes(version);
            buff.put(headerBytes);
            buff.put(bodyBytes);
        } else {
            header.setMessageSize(0);
            byte[] headerBytes = header.WriteToBytes(version);
            buff.put(headerBytes);
        }
        // int pos = buff.position();
        // byte[] messageBytes = new byte[pos - buff.markValue() + 1];
        // buff.get(messageBytes);
        byte[] messageBytes = buff.array();
        byte checkSum = GetCheckXor(messageBytes, 0, messageBytes.length);
        // messageBytes[messageBytes.length - 1] = checkSum; // 填充校验码
        buff.put(checkSum);
        byte[] escapedBytes = Escape(buff.array()); // 转义
        buff.clear();
        buff.put(_PrefixID);
        buff.put(escapedBytes);
        buff.put(_PrefixID);

        byte[] data = buff.array();

//        if (TraceLogger.isTrace(this.getSimNo()) && TraceLogger.isLogBytesEnabled()) {
//            log.debug("下行报文! msgType=0x" + Tools.ToHexString4bit(getMessageType()) + "; simNo=" + getSimNo() +
//                    "; bytes=" + ByteUtil.byteToHexStr(data));
//        }

        return data;
    }


    public final void ReadFromBytes(byte[] messageBytes) throws Exception {

        byte[] validMessageBytes = UnEscape(messageBytes);
        try {
            // 检测校验码
            byte xor = GetCheckXor(validMessageBytes, 1,
                    validMessageBytes.length - 2);
            byte realXor = validMessageBytes[validMessageBytes.length - 2];
            if (true) {
                _checkSum = new byte[]{xor};

                int start = header.ReadFromBytes(validMessageBytes, 1) + 1;
                if (header.getMessageSize() > 0) {
                    byte[] sourceData = new byte[header
                            .getMessageSize()];
                    System.arraycopy(validMessageBytes, start,
                            sourceData, 0, sourceData.length);
                    if (header.getIsPackage()
                    ) {
                        // 分包的消息体是纯数据不进行解析，保留在消息中.
                        childPacket = new byte[header.getMessageSize()];
                        System.arraycopy(sourceData, 0, childPacket, 0,
                                header.getMessageSize());

                    } else {
                        // 其余的包都进行解析
                        setMessageContents(T808MessageFactory_WORKCARD.Create(header.getProtocolVersion(),
                                header.getMessageType(), sourceData));
                    }

                } else {
                    setMessageContents(T808MessageFactory_WORKCARD.Create(header.getProtocolVersion(),
                            header.getMessageType(), new byte[0]));
                }

//                if (TraceLogger.isTrace(this.getSimNo())) {
//                    log.trace("上行解析:" + this.toString());
//                }
            } else {
//                setErrorMessage();
                log.error("校验码不正确"
                        + Tools.ToHexFormatString(messageBytes));
            }

        } catch (Exception ex) {
            throw new Exception("T808Message_WORKCARD电子工牌解析异常！ReadFromBytes()" + Tools.ToHexFormatString(messageBytes), ex);

        }

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * 获取校验和
     *
     * @param data
     * @param pos
     * @param len
     * @return
     */
    private byte GetCheckXor(byte[] data, int pos, int len) {
        byte A = 0;
        for (int i = pos; i < len; i++) {
            A ^= data[i];
        }
        return A;
    }

    /**
     * 将标识字符的转义字符还原
     *
     * @param data
     * @return
     */
    private byte[] UnEscape(byte[] data) {
        MyBuffer buff = new MyBuffer();
        for (int i = 0; i < data.length; i++) {
            if (data[i] == 0x7D) {
                if (data[i + 1] == 0x01) {
                    buff.put((byte) 0x7D);
                    i++;
                } else if (data[i + 1] == 0x02) {
                    buff.put((byte) 0x7E);
                    i++;
                }
            } else {
                buff.put(data[i]);
            }
        }

        byte[] a = buff.array();

        return a;
    }

    /**
     * 加入标示符的转义进行封装
     *
     * @param data
     * @return
     */
    private byte[] Escape(byte[] data) {
        MyBuffer tmp = new MyBuffer();
        for (int j = 0; j < data.length; j++) {
            if (data[j] == 0x7D) {
                tmp.put((byte) 0x7D);
                tmp.put((byte) 0x01);
            } else if (data[j] == 0x7E) {
                tmp.put((byte) 0x7D);
                tmp.put((byte) 0x02);
            } else {
                tmp.put(data[j]);
            }
        }

        return tmp.array();
    }

    public byte[] getChildPacket() {
        return childPacket;
    }

    public void setChildPacket(byte[] childPacket) {
        this.childPacket = childPacket;
    }


    public int getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(int protocolType) {
        this.protocolType = protocolType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSubMsgType() {
        return subMsgType;
    }

    public void setSubMsgType(String subMsgType) {
        this.subMsgType = subMsgType;
    }
}
