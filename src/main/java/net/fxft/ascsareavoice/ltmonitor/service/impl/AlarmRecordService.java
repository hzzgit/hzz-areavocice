package net.fxft.ascsareavoice.ltmonitor.service.impl;

import net.fxft.ascsareavoice.ltmonitor.entity.AlarmRecord;
import net.fxft.ascsareavoice.ltmonitor.service.IAlarmRecordService;
import org.springframework.stereotype.Service;

@Service("alarmRecordService")
public class AlarmRecordService extends BaseService<Long, AlarmRecord> implements IAlarmRecordService {

	public AlarmRecordService() {
		super(AlarmRecord.class);
	}
}
