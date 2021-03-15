package net.fxft.ascsareavoice.service.MapArea.entiry;

import lombok.*;
import net.fxft.common.jdbc.*;
import java.util.*;

/**
 * 用于记录当前在这个围栏里面的车辆
 */
@Data
@DbTable(value="subiaodb.areagpsrealdata")
public class Areagpsrealdata implements java.io.Serializable  {

private static final long serialVersionUID = 1L;
    public static final String F_id = "id";
    public static final String F_vehicleid = "vehicleid";
    public static final String F_areaid = "areaid";
    public static final String F_createdate = "createdate";


    /**    */
        @DbId
    private long  id;
    /**  车辆主键  */
    private long  vehicleid;
    /**  围栏id  */
    private long  areaid;
    /**  创建时间  */
    private Date  createdate;

public static void main(String[] args) {
String name="{\n";
    name +="  \"id\":0, //\n";
    name +="  \"vehicleid\":0, //车辆主键\n";
    name +="  \"areaid\":0, //围栏id\n";
    name +="  \"createdate\":\"2020-09-11 00:00:00\" //创建时间\n";
name+="}";
System.out.println(name);

}

}