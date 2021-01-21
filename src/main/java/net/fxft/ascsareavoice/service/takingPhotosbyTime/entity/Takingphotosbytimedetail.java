package net.fxft.ascsareavoice.service.takingPhotosbyTime.entity;

import lombok.Data;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

@Data
@DbTable(value = "subiaodb.takingphotosbytimedetail")
public class Takingphotosbytimedetail implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    public static final String F_id = "id";
    public static final String F_resultid = "resultid";
    public static final String F_createdate = "createdate";
    public static final String F_updatedate = "updatedate";
    public static final String F_deleted = "deleted";
    public static final String F_remark = "remark";
    public static final String F_channelid = "channelid";
    public static final String F_codeformat = "codeformat";
    public static final String F_commandid = "commandid";
    public static final String F_commandtype = "commandtype";
    public static final String F_filename = "filename";
    public static final String F_mediadataid = "mediadataid";
    public static final String F_mediatype = "mediatype";
    public static final String F_configid = "configid";
    public static final String F_vehicleid = "vehicleid";


    /**
     * 主键
     */
    @DbId
    private long id;
    /**
     * 结果表主键，用于关联
     */
    private long resultid;
    /**
     * 创建时间
     */
    private Date createdate;
    /**
     * 更新时间
     */
    private Date updatedate;
    /**
     * 删除标志
     */
    private String deleted;
    /**
     * 备注
     */
    private String remark;
    /**
     * 通道
     */
    private byte channelid;
    /**
     * 多媒体格式编码 0：JPEG；1：TIF；2：MP3；3：WAV；4：WMV
     */
    private byte codeformat;
    /**
     * 命令id
     */
    private long commandid;
    /**
     * 上传情况，0未上传，1上传成功
     */
    private int commandtype;
    /**
     * 文件名
     */
    private String filename;
    /**
     * 多媒体id
     */
    private int mediadataid;
    /**
     * 多媒体类型 0：图像；1：音频；2：视频；
     */
    private byte mediatype;
    /**
     * 定时拍照配置表主键
     */
    private long configid;
    /**
     * 车辆主键
     */
    private long vehicleid;

    public static void main(String[] args) {
        String name = "{\n";
        name += "  \"id\":0, //主键\n";
        name += "  \"resultid\":0, //结果表主键，用于关联\n";
        name += "  \"createdate\":\"2020-09-11 00:00:00\", //创建时间\n";
        name += "  \"updatedate\":\"2020-09-11 00:00:00\", //更新时间\n";
        name += "  \"deleted\":\"\", //删除标志\n";
        name += "  \"remark\":\"\", //备注\n";
        name += "  \"channelid\":\"\", //通道\n";
        name += "  \"codeformat\":\"\", //多媒体格式编码 0：JPEG；1：TIF；2：MP3；3：WAV；4：WMV\n";
        name += "  \"commandid\":0, //命令id\n";
        name += "  \"commandtype\":\"\", //上传情况，0未上传，1上传成功\n";
        name += "  \"filename\":\"\", //文件名\n";
        name += "  \"mediadataid\":0, //多媒体id\n";
        name += "  \"mediatype\":\"\", //多媒体类型 0：图像；1：音频；2：视频；\n";
        name += "  \"configid\":0, //定时拍照配置表主键\n";
        name += "  \"vehicleid\":0 //车辆主键\n";
        name += "}";
        System.out.println(name);

    }

}