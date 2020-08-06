package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.AlarmConfig;
import net.fxft.ascsareavoice.ltmonitor.service.IAlarmConfigService;
import net.fxft.common.jdbc.JdbcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 报警配置服务
 *
 * @author admin
 */

@Service("alarmConfigService")
public class AlarmConfigService extends BaseService<Long, AlarmConfig>
        implements IAlarmConfigService {

    private static final Logger log = LoggerFactory.getLogger(AlarmConfigService.class);
    /**
     * 基本信息缓存
     */
    public Map<String, AlarmConfig> alarmConfigMap = new ConcurrentHashMap<>();

    @Autowired
    private JdbcUtil jdbc;


    public AlarmConfigService() {
        super(AlarmConfig.class);
    }

    @PostConstruct
    @Scheduled(fixedRate = 60000, initialDelay = 60000)
    public void updateCache() {
        try {
			List<AlarmConfig> ls = jdbc.sql("select * from AlarmConfig")
                    .query(AlarmConfig.class);
            Map<String, AlarmConfig> tmpconfigmap = new ConcurrentHashMap<>();
			for (AlarmConfig a : ls) {
                tmpconfigmap.put(a.getAlarmType() + "_" + a.getAlarmSource(),	a);
			}
            this.alarmConfigMap = tmpconfigmap;
			log.debug("更新AlarmConfig成功！");
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		}
    }


    @Override
    public AlarmConfig getAlarmConfig(String alarmType, String alarmSource) {
        String key = alarmType + "_" + alarmSource;
        return alarmConfigMap.get(key);
    }



    public boolean isAlarmEnabled(String alarmType, String alarmSource) {
        String key = alarmType + "_" + alarmSource;
        if (alarmConfigMap.containsKey(key)) {
            AlarmConfig a = alarmConfigMap.get(key);
            if (a.isEnabled()) {
                return true;
            }else{
                return false;
            }
        }
        return false;
    }


	public boolean isStatisticAlarm(String alarmType, String alarmSource) {
		String key = alarmType + "_" + alarmSource;
		if (alarmConfigMap.containsKey(key)) {
			AlarmConfig a = alarmConfigMap.get(key);
			return a.isStatisticEnabled();
		}
		return false;
	}
//
//	/**
//	 * 获得报警联动关联的视频通道号，多个通道以逗号隔开
//	 *
//	 * @param alarmType
//	 * @param alarmSource
//	 * @return
//	 */
	public String getAlarmVideoChannels(String alarmType, String alarmSource) {
		String key = alarmType + "_" + alarmSource;
		if (alarmConfigMap.containsKey(key)) {
			AlarmConfig a = alarmConfigMap.get(key);
			return a.getVideoMonitorChannels();
		}
		return null;
	}
//
//	/**
//	 * 获得报警联动关联的视频通道号，多个通道以逗号隔开
//	 *
//	 * @param alarmType
//	 * @param alarmSource
//	 * @return
//	 */
	public String getAlarmPhotoChannels(String alarmType, String alarmSource) {
		String key = alarmType + "_" + alarmSource;
		if (alarmConfigMap.containsKey(key)) {
			AlarmConfig a = alarmConfigMap.get(key);
			return a.getTakePictureChannels();
		}
		return null;
	}

}
