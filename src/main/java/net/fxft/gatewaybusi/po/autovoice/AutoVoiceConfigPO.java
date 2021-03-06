package net.fxft.gatewaybusi.po.autovoice;

//播报的配置，可能有多个配置
public class AutoVoiceConfigPO {

    //语音播报配置的主键
    private int id;

    private int type ;

    //时间间隔（分钟）
    private int sendInterval;

    //语音播放的内容
    private String sendContent;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSendInterval() {
        return sendInterval;
    }

    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    public String getSendContent() {
        return sendContent;
    }

    public void setSendContent(String sendContent) {
        this.sendContent = sendContent;
    }
}
