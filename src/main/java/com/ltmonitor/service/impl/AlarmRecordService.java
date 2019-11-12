package com.ltmonitor.service.impl;

import com.ltmonitor.entity.AlarmRecord;
import com.ltmonitor.service.IAlarmRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("alarmRecordService")
public class AlarmRecordService extends BaseService<Long, AlarmRecord> implements IAlarmRecordService {

	public AlarmRecordService() {
		super(AlarmRecord.class);
	}
}
