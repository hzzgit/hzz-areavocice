package net.fxft.ascsareavoice.ltmonitor.entity;

import net.fxft.common.jdbc.DbColumn;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

/**
 * 多媒体记录
 *
 * @author admin
 */
//@Entity
//@Table(name="mediaItem")
//@org.hibernate.annotations.Proxy(lazy = false)
//@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
@DbTable(value = "mediaItem", camelToUnderline = false)
public class MediaItem extends TenantEntity {
    public static final String UPLOAD = "upload"; //媒体数据上传
    public static final String SEARCH = "search"; //媒体检索项
    public static final String INFO = "info"; //媒体事件信息

    public static final String USER = "user";
    //	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "mediaItemId", unique = true, nullable = false)
    @DbId
    @DbColumn(columnName = "mediaItemId")
    private long entityId;
    private String plateNo;
    private String simNo;
    private String location;
    //发生时间
    private java.util.Date sendTime = new java.util.Date(0);
    /**
     * 多媒体数据ID
     */
    private int mediaDataId;
    /**
     * 多媒体类型
     */
    private byte mediaType;
    /**
     * 多媒体格式编码
     */
    private byte codeFormat;
    private double latitude;
    private double longitude;
    private double speed;
    /**
     * 事件项编码
     */
    private byte eventCode;
    /**
     * 通道ID
     */
    private byte channelId;
    //数据存储的文件名
    private String fileName;
    //根据那个命令返回的ID，通过此命令可以查询出所返回的数据
    private long commandId;
    //upload 上传命令，search 检索命令, 检索不返回文件数据
    private String commandType;
    public MediaItem() {
        setCommandType(UPLOAD);
        setCreateDate(new java.util.Date());
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(long value) {
        entityId = value;
    }

    public String getPlateNo() {
        return plateNo;
    }

    //private String alarmState;

    public void setPlateNo(String value) {
        plateNo = value;
    }

    public String getSimNo() {
        return simNo;
    }

    public void setSimNo(String value) {
        simNo = value;
    }

    public java.util.Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(java.util.Date value) {
        sendTime = value;
    }

    public byte getEventCode() {
        return eventCode;
    }

    public void setEventCode(byte value) {
        eventCode = value;
    }

    public byte getChannelId() {
        return channelId;
    }

    public void setChannelId(byte value) {
        channelId = value;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String value) {
        fileName = value;
    }

    public long getCommandId() {
        return commandId;
    }

    public void setCommandId(long value) {
        commandId = value;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String value) {
        commandType = value;
    }

    public int getMediaDataId() {
        return mediaDataId;
    }

    public void setMediaDataId(int mediaDataId) {
        this.mediaDataId = mediaDataId;
    }

    public byte getMediaType() {
        return mediaType;
    }

    public void setMediaType(byte mediaType) {
        this.mediaType = mediaType;
    }

    public byte getCodeFormat() {
        return codeFormat;
    }

    public void setCodeFormat(byte codeFormat) {
        this.codeFormat = codeFormat;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}