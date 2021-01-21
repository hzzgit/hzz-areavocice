package net.fxft.ascsareavoice.service.takingPhotosbyTime.entity;

import lombok.Data;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

@Data
@DbTable(value="subiaodb.takingphotosbytimebydep")
public class Takingphotosbytimebydep implements java.io.Serializable  {

private static final long serialVersionUID = 1L;
    public static final String F_id = "id";
    public static final String F_mainid = "mainid";
    public static final String F_depid = "depid";
    public static final String F_createdate = "createdate";
    public static final String F_deleted = "deleted";


    /**  主键修改则传列表中的  */
        @DbId
    private Long  id;
    /**  定时拍照表id修改则传列表中的  */
    private Long  mainid;
    /**  绑定机构的id修改则传列表中的  */
    private Long  depid;
    /**  创建时间修改则传列表中的  */
    private Date  createdate;
    /**  删除标志,0代表未删除,1代表删除修改则传列表中的  */
    private Long  deleted;

public static void main(String[] args) {
String name="{\n";
    name +="  \"id\":1, //主键\n";
    name +="  \"mainid\":1, //定时拍照表id\n";
    name +="  \"depid\":1, //绑定机构的id\n";
    name +="  \"createdate\":\"2020-09-11 00:00:00\", //创建时间\n";
    name +="  \"deleted\":1 //删除标志,0代表未删除,1代表删除\n";
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