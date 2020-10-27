package net.fxft.ascsareavoice.service.AutoVoice.po;

import java.util.Date;

//播报的配置，可能有多个配置
public class AutoVoiceConfigPO {

    //语音播报配置的主键
    private int id;
    //1、ACC开之后立即、2、ACC开之后等待
    private int type ;

    //时间间隔（分钟）
    private int sendInterval;

    //语音播放的内容
    private String sendContent;

    private Date startTime;
    private Date endTime;

    private int isuse;


    public int getIsuse() {
        return isuse;
    }

    public void setIsuse(int isuse) {
        this.isuse = isuse;
    }

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
