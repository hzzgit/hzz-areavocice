package net.fxft.ascsareavoice.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/8/5 14:54
 */
@Data
public class WaybillAreaMainVo {
    private long id;
    private Date startTime,endTime;
    private String SimNo;
    private long userid;
    private int bytime;
    private String name;
    private List<WaybillAreaPointVo> waybillAreaPointVos=new ArrayList<>();
}
