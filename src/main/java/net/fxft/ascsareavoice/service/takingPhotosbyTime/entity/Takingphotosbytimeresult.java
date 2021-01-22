package net.fxft.ascsareavoice.service.takingPhotosbyTime.entity;

import lombok.Data;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

@Data
@DbTable(value="subiaodb.takingphotosbytimeresult")
public class Takingphotosbytimeresult implements java.io.Serializable  {

private static final long serialVersionUID = 1L;
    public static final String F_id = "id";
    public static final String F_createdate = "createdate";
    public static final String F_updatedate = "updatedate";
    public static final String F_deleted = "deleted";
    public static final String F_userid = "userid";
    public static final String F_photonum = "photonum";
    public static final String F_remark = "remark";
    public static final String F_latitude = "latitude";
    public static final String F_longitude = "longitude";
    public static final String F_sendtime = "sendtime";
    public static final String F_simno = "simno";
    public static final String F_vehicleid = "vehicleid";
    public static final String F_speed = "speed";
    public static final String F_drivername = "drivername";
    public static final String F_certificate = "certificate";
    public static final String F_configid = "configid";
    public static final String F_status = "status";


    /**    */
        @DbId
    private long  id=0;
    /**  创建时间  */
    private Date  createdate=new Date();
    /**  更新时间  */
    private Date  updatedate=new Date();
    /**  删除标志  */
    private int  deleted=0;
    /**  发送命令用户id  */
    private long  userid;
    /**  拍照总数  */
    private long  photonum;
    /**  备注  */
    private String  remark;
    /**  维度  */
    private double  latitude;
    /**  经度  */
    private double  longitude;
    /**  定位时间  */
    private Date  sendtime;
    /**  simNo  */
    private String  simno;
    /**  车辆主键  */
    private long  vehicleid;
    /**  当前车速  */
    private double  speed;
    /**  司机姓名  */
    private String  drivername;
    /**  从业资格证号  */
    private String  certificate;
    /**  配置表id,表名takingphotosbytime  */
    private long  configid;
    /**  上传情况，0,未上传,1上传成功  */
    private long  status=0;

public static void main(String[] args) {
String name="{\n";
    name +="  \"id\":0, //\n";
    name +="  \"createdate\":\"2020-09-11 00:00:00\", //创建时间\n";
    name +="  \"updatedate\":\"2020-09-11 00:00:00\", //更新时间\n";
    name +="  \"deleted\":\"\", //删除标志\n";
    name +="  \"userid\":0, //发送命令用户id\n";
    name +="  \"photonum\":0, //拍照总数\n";
    name +="  \"remark\":\"\", //备注\n";
    name +="  \"latitude\":\"\", //维度\n";
    name +="  \"longitude\":\"\", //经度\n";
    name +="  \"sendtime\":\"2020-09-11 00:00:00\", //定位时间\n";
    name +="  \"simno\":\"\", //simNo\n";
    name +="  \"vehicleid\":\"\", //车辆主键\n";
    name +="  \"speed\":\"\", //当前车速\n";
    name +="  \"drivername\":\"\", //司机姓名\n";
    name +="  \"certificate\":\"\", //从业资格证号\n";
    name +="  \"configid\":0, //配置表id,表名takingphotosbytime\n";
    name +="  \"status\":0 //上传情况，0,未上传,1上传成功\n";
name+="}";
System.out.println(name);

}

}