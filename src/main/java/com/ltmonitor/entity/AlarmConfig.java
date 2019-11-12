package com.ltmonitor.entity;

import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.io.Serializable;

/**
 * @author DELL
 * 报警参数配置，配置某一类型的报警，是否启动，是否弹窗，是否有声音，是否统计起止时间
 */

//@Entity
//@Table(name="AlarmConfig")
//@org.hibernate.annotations.Proxy(lazy = false)
@DbTable(value = "AlarmConfig", camelToUnderline = false)
public class AlarmConfig implements Serializable {
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "id", unique = true, nullable = false)
    @DbId
    private long id;
    /**
     * 所属部门Id
     */
    private long depId;
    /**
     * 报警中文名称
     */
    private String name;
    /**
     * 报警类型 常量 来自 AlarmRecord中定义的常量
     */
    private String alarmType;
    /**
     * 报警来源 常量 来自 AlarmRecord中定义的常量
     */
    private String alarmSource;
    /**
     * 是否启动
     */
    private boolean enabled;
    /**
     * 是否播放报警声音
     */
    private boolean soundEnabled;
    /**
     * 是否弹窗
     */
    private boolean popupEnabled;
    /**
     * 是否统计报警的起止时间
     */
    private boolean statisticEnabled;
    /**
     * 报警联动,需要实时监控的通道号,为空则不启动实时视频
     */
    private String videoMonitorChannels;
    /**
     * 报警时需要拍照的摄像头，如果为空，则不拍照
     */
    private String takePictureChannels;
    /**
     * 报警的风险级别，high meidum low null
     */
    private String riskLevel;
    /**
     * 扣分系数
     */
    private int points;
    /**
     * 是否自动处理
     */
    private boolean autoProcess;
    private String textForDriver;
    /**
     * 当报警发生的时候，只报警一次，false，则一直报警直到报警关闭或取消
     */
    private boolean alarmOnce;
    /**
     * 警报规则说明
     */
    private String remark;
    public AlarmConfig() {
        riskLevel = "none";
    }

    public long getId() {
        return id;
    }

    public void setId(long value) {
        id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }

    public boolean isPopupEnabled() {
        return popupEnabled;
    }

    public void setPopupEnabled(boolean popupEnabled) {
        this.popupEnabled = popupEnabled;
    }

    public boolean isStatisticEnabled() {
        return statisticEnabled;
    }

    public void setStatisticEnabled(boolean statisticEnabled) {
        this.statisticEnabled = statisticEnabled;
    }

    public long getDepId() {
        return depId;
    }

    public void setDepId(long depId) {
        this.depId = depId;
    }

    /**
     * 报警联动,需要实时监控的通道号,为空则不启动实时视频
     */
    public String getVideoMonitorChannels() {
        return videoMonitorChannels;
    }

    public void setVideoMonitorChannels(String videoMonitorChannels) {
        this.videoMonitorChannels = videoMonitorChannels;
    }

    /**
     * 报警时需要拍照的摄像头，如果为空，则不拍照
     */
    public String getTakePictureChannels() {
        return takePictureChannels;
    }

    public void setTakePictureChannels(String takePictureChannels) {
        this.takePictureChannels = takePictureChannels;
    }

    /**
     * 报警的风险级别，3-0 高中低无
     */
    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isAutoProcess() {
        return autoProcess;
    }

    public void setAutoProcess(boolean autoProcess) {
        this.autoProcess = autoProcess;
    }

    public String getTextForDriver() {
        return textForDriver;
    }

    public void setTextForDriver(String textForDriver) {
        this.textForDriver = textForDriver;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
