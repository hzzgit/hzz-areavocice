package net.fxft.ascsareavoice.service.WaybillAreaKeyPoint.enumconfig;

public enum WaybillAreaKeyPointEnum {
    进入运单关键点停车报警 ("InAreawaybillkeypoint","platform_alarm"),
    离开运单关键点停车报警 ("Crosswaybillkeypoint","platform_alarm"),
    进入运单关键点停车超时报警 ("InAreawaybillkeypointparktimeout","platform_alarm");


    private String alarmType;
    private String alarmSource;

    WaybillAreaKeyPointEnum(String alarmType, String alarmSource) {
        this.alarmType = alarmType;
        this.alarmSource = alarmSource;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }
}
