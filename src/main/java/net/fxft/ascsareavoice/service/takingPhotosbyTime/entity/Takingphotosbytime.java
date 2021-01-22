package net.fxft.ascsareavoice.service.takingPhotosbyTime.entity;

import lombok.Data;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

@Data
@DbTable(value="subiaodb.takingphotosbytime")
public class Takingphotosbytime implements java.io.Serializable  {

private static final long serialVersionUID = 1L;

    public static  final  int condition不限制=0;
    public static  final  int condition根据车速=1;
    public static  final  int condition根据停车时长=2;
    public static final String F_id = "id";
    public static final String F_name = "name";
    public static final String F_interval = "interval";
    public static final String F_createdate = "createdate";
    public static final String F_updatedate = "updatedate";
    public static final String F_starttime = "starttime";
    public static final String F_endtime = "endtime";
    public static final String F_validstarttime = "validstarttime";
    public static final String F_validendtime = "validendtime";
    public static final String F_condition = "condition";
    public static final String F_speed = "speed";
    public static final String F_packduration = "packduration";
    public static final String F_channel = "channel";
    public static final String F_userid = "userid";
    public static final String F_isuse = "isuse";
    public static final String F_deleted = "deleted";


    /**  主键修改则传列表中的  */
        @DbId
    private int  id;
    /**  名称修改则传列表中的  */
    private String  name;
    /**  拍照间隔(单位：分)修改则传列表中的  */
    private Long  configinterval;
    /**  创建时间修改则传列表中的  */
    private Date  createdate;
    /**  修改时间修改则传列表中的  */
    private Date  updatedate;
    /**  配置生效开始时间，时间格式为00:00:00 修改则传列表中的  */
    private String  starttime;
    /**  配置生效结束时间,时间格式为 23:59:59修改则传列表中的  */
    private String  endtime;
    /**  配置有效期开始时间修改则传列表中的  */
    private Date  validstarttime;
    /**  配置有效期结束时间修改则传列表中的  */
    private Date  validendtime;
    /**  触发条件,0、不限制，1、根据车速，2、根据停车时长修改则传列表中的  */
    private Long  configcondition;
    /**  车速单位km/h，触发条件为1的车速修改则传列表中的  */
    private double  speed;
    /**  停车时长单位分钟，触发条件为2的停车时长修改则传列表中的  */
    private double  packduration;
    /**  拍照的通道,用";"号隔开修改则传列表中的  */
    private String  channel;
    /**  用户id修改则传列表中的  */
    private Long  userid;
    private String username;
    /**  是否启用,0禁用,1启用修改则传列表中的  */
    private Boolean  isuse;
    /**  删除标志,1代表删除,0代表正常修改则传列表中的  */
    private Long  deleted;



public static void main(String[] args) {
String name="{\n";
    name +="  \"id\":1, //主键\n";
    name +="  \"name\":\"测试\", //名称\n";
    name +="  \"interval\":1, //拍照间隔(单位：分)\n";
    name +="  \"createdate\":\"2020-09-11 00:00:00\", //创建时间\n";
    name +="  \"updatedate\":\"2020-09-11 00:00:00\", //修改时间\n";
    name +="  \"starttime\":\"测试\", //配置生效开始时间，时间格式为00:00:00 \n";
    name +="  \"endtime\":\"测试\", //配置生效结束时间,时间格式为 23:59:59\n";
    name +="  \"validstarttime\":\"测试\", //配置有效期开始时间\n";
    name +="  \"validendtime\":\"测试\", //配置有效期结束时间\n";
    name +="  \"condition\":1, //触发条件,0、不限制，1、根据车速，2、根据停车时长\n";
    name +="  \"speed\":\"\", //车速单位km/h，触发条件为1的车速\n";
    name +="  \"packduration\":\"\", //停车时长单位分钟，触发条件为2的停车时长\n";
    name +="  \"channel\":\"测试\", //拍照的通道,用;号隔开\n";
    name +="  \"userid\":1, //用户id\n";
    name +="  \"isuse\":false, //是否启用,0禁用,1启用\n";
    name +="  \"deleted\":1 //删除标志,1代表删除,0代表正常\n";
name+="}";
System.out.println(name);
name ="    \"success\":true, \n "+
" \"code\":200,\n " +
" \"message\":\"success\", \n " +
" \"data\": " +name+
", \n \"total\":1 ";
System.out.println(name);
}

}