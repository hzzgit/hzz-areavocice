package net.fxft.ascsareavoice.service.WaybillArea;

public enum WaybillAreaEnum {
    进入运单围栏报警 ("InAreawaybill","platform_alarm"),
    离开运单围栏报警 ("Crosswaybill","platform_alarm");

    private String alarmType;
    private String alarmSource;

    WaybillAreaEnum(String alarmType, String alarmSource) {
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
