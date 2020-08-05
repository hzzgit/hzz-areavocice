package net.fxft.ascsareavoice.service.WaybillArea;

/**
 * @author ：hzz
 * @description：TODO
 * @date ：2020/8/5 14:26
 */

import com.ltmonitor.entity.GPSRealData;

/**
 * 运单围栏报警计算规则
 */
public interface IWaybillAreaService {

    //添加到队列
    public void addAreaqueue(GPSRealData rd) ;
}
