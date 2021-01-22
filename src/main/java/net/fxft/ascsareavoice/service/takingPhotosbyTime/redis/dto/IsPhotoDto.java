package net.fxft.ascsareavoice.service.takingPhotosbyTime.redis.dto;


import net.fxft.ascsareavoice.ltmonitor.util.TimeUtils;

import java.util.Date;

/**
 * @author ：hzz
 * @description：照片上传的记录信息
 * @date ：2021/1/21 13:54
 */
public class IsPhotoDto {

        public static  int 已下发=0;
        public static  int 照片已上传=1;

    /**
     * 执行时间
     */
    private Date time;

    /**
     * 状态，0=已下发命令，1=照片已上传
     */
    private int status;

    public IsPhotoDto(int status) {
        this.status = status;
        time =new Date();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "IsPhotoDto{" +
                "time=" + TimeUtils.dateTodetailStr(time) +
                ", status=" + status +
                '}';
    }
}
