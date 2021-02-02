package net.fxft.ascsareavoice.service.takingPhotosbyTime.service.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author ：hzz
 * @description：用来缓存
 * @date ：2021/2/2 11:45
 */
@Data
public class CmdIdDto {
    //simNo+通道号下发的命令id
    private long cmdId;
    //simNo+通道号下发的命令时间
    private Date cmdTime;


    public static   CmdIdDto bulider(long cmdId,Date cmdTime){
        CmdIdDto cmdIdDto=new CmdIdDto();
        cmdIdDto.setCmdId(cmdId);
        cmdIdDto.setCmdTime(cmdTime);
        return  cmdIdDto;
    }
}
