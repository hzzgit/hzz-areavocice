package net.fxft.ascsareavoice.service.MapArea.dto;

import lombok.Data;
import net.fxft.ascsareavoice.service.MapArea.entiry.Areagpsrealdata;
import net.fxft.common.jdbc.DbId;
import net.fxft.common.jdbc.DbTable;

import java.util.Date;

/**
 * 用于对围栏实时车辆表进行io数据库操作
 */
@Data
public class AreagpsrealdataDto  {

private static final long serialVersionUID = 1L;

    /**
     * 用来判断是插入还是删除
     */
    private boolean isadd=true;


    private Areagpsrealdata areagpsrealdata;
}