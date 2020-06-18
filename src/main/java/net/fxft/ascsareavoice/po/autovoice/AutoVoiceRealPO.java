package net.fxft.ascsareavoice.po.autovoice;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//用来保存实时数据的acc状态及定位时间
public class AutoVoiceRealPO {

    private boolean isacc;

    private Date onlineDate;

    private Map<Integer,Date> configTime=new HashMap<>();

    public boolean isIsacc() {
        return isacc;
    }

    public void setIsacc(boolean isacc) {
        this.isacc = isacc;
    }

    public Date getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(Date onlineDate) {
        this.onlineDate = onlineDate;
    }

    public Map<Integer, Date> getConfigTime() {
        return configTime;
    }

    public void setConfigTime(Map<Integer, Date> configTime) {
        this.configTime = configTime;
    }
}
