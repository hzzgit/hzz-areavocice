package net.fxft.ascsareavoice.ltmonitor.util;

import net.fxft.ascsareavoice.ltmonitor.entity.AlarmConfig;
import net.fxft.ascsareavoice.ltmonitor.entity.AlarmRecord;
import net.fxft.gateway.kafka.jt809.WarnData;

public class AlarmTypeConstants {
    /**
     * adas胎压报警
     */
    public static String TYPE_ADAS_TIRE_PRESSURE_ALARM = "adas_tire_pressure";

    /**
     * 将企业平台上面支持的808报警、adas报警 转换为809上报的报警类型
     * @param alarmType
     * @param alarmSource
     * @return
     */
    public static int convertSubiao809AlarmType(String alarmType, String alarmSource) {
        if (AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE.equals(alarmSource)) {
            if ("1".equals(alarmType)) {
                return 0x0106;//生理疲劳驾驶预警
            } else if ("2".equals(alarmType)) {
                return 0x000E;//接打电话报警
            } else if ("3".equals(alarmType)) {
                return 0x000F;//抽烟报警
            } else if ("4".equals(alarmType)) {
                return 0x0010; //分神报警;//0x0107 分神驾驶报警
            } else if ("5".equals(alarmType)) {
                return 0x0011;//驾驶员异常报警;
            }
        } else if (AlarmRecord.ALARM_FROM_ADAS.equals(alarmSource)) {
            if ("1".equals(alarmType)) {
                return 0x0102;//前向碰撞
            } else if ("2".equals(alarmType)) {
                return 0x103;//车道偏离报警
            } else if ("3".equals(alarmType)) {
                return 0x104;//车距过近报警
            } else if ("4".equals(alarmType)) {
                return 0x0105; //行人碰撞报警;
            } else if ("5".equals(alarmType)) {
                return 0x0013;//频繁变道报警;
            }
        } else if (AlarmRecord.ALARM_FROM_ADAS_BLIND_AREA.equals(alarmSource)) {
            if ("1".equals(alarmType)) {
                return 0x0109;//后方接近报警
            } else if ("2".equals(alarmType)) {
                return 0x10A;//左侧后方接近报警
            } else if ("3".equals(alarmType)) {
                return 0x10B;//右侧后方接近报警
            }
        } else if (AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE.equals(alarmSource)) {
            if ("1".equals(alarmType)) {
                return 0x010C;//胎压异常报警（胎压定时上报）
            } else if ("2".equals(alarmType)) {
                return 0x10D;//胎压异常报警（胎压过高报警）
            } else if ("3".equals(alarmType)) {
                return 0x10E;//胎压异常报警（胎压过低报警）
            } else if ("4".equals(alarmType)) {
                return 0x010F; //胎压异常报警（胎温过高报警）;
            } else if ("5".equals(alarmType)) {
                return 0x0110;//胎压异常报警（传感器异常报警）;
            } else if ("6".equals(alarmType)) {
                return 0x0111;//胎压异常报警（胎压不平衡报警）
            } else if ("7".equals(alarmType)) {
                return 0x0112;//胎压异常报警（慢漏气报警）
            } else if ("8".equals(alarmType)) {
                return 0x0113;//胎压异常报警（电池电量低报警）
            }
        } else if (AlarmRecord.ALARM_FROM_TERM.equals(alarmSource)) {
            if (alarmType.equals("1")) {
                return 1; //超速
            }else if (alarmType.equals("2")) {
                return 2;//疲劳
            }else if (alarmType.equals("0")) {
                return  3; //紧急
            }else if (alarmType.equals(AlarmRecord.TYPE_IN_AREA)) {
                return 4;//进入区域
            }else if (alarmType.equals(AlarmRecord.TYPE_CROSS_BORDER)) {
                return 5;//离开区域
            }else if (alarmType.equals(AlarmRecord.TYPE_OFFSET_ROUTE)) {
                return 0x0B;//偏离线路
            }else if (alarmType.equals("29")) {
                return 0x12;//碰撞
            }else if (alarmType.equals("30")) {
                return 0x13; //侧翻
            }else if (alarmType.equals("18")) {
                return 0x122; //累计驾驶超时报警
            }else if (alarmType.equals("28")) {
                return 0x123;//离线位移报警
            }else if (alarmType.equals("4")) {
                return 0x130;//GNSS 模块发生故障
            }else if (alarmType.equals("5")) {
                return 0x131; //GNSS 天线未接或被剪断
            }else if (alarmType.equals("6")) {
                return 0x132; //GNSS 天线短路
            }else if (alarmType.equals("7")) {
                return 0x133;//终端主电源欠压
            }else if (alarmType.equals("8")) {
                return 0x134;//终端主电源掉电
            }else if (alarmType.equals("9")) {
                return 0x135;//终端 LCD 或显示器故障
            }else if (alarmType.equals("10")) {
                return 0x136;//TTS 模块故障
            }else if (alarmType.equals("11")) {
                return 0x137;//摄像头故障
            }else if (alarmType.equals("24")) {
                return 0x138; //车辆 VSS 故障
            }else if (alarmType.equals("25") ){
                return 0x139;//1：车辆油量异常
            }else if (alarmType.equals("12")) {
                return 0x140;//道路运输证 IC 卡模块故障
            }else if (alarmType.equals("26")) {
                return 0x141;//1：车辆被盗
            }else if (alarmType.equals("19")) {
                return 0x142;//超时停车
            }
        } else if (AlarmRecord.ALARM_FROM_VIDEO.equals(alarmSource)) {
            if (alarmType.equals("0"))
                return WarnData.VIDEO_LOSS;
            else if (alarmType.equals("1"))
                return WarnData.VIDEO_COVER;
            else if (alarmType.equals("2"))
                return WarnData.STORAGE_ERROR;
            else if (alarmType.equals("3"))
                return WarnData.OTHER_VIDEO_DEVICE_ERROR;
            else if (alarmType.equals("4"))
                return WarnData.BUS_OVERLOAD;
            else if (alarmType.equals("5"))
                return WarnData.ABNOMRAL_DRIVE_BEHAVE;
            else if (alarmType.equals("6"))
                return WarnData.SPECIAL_ALARM_VIDEO_FULL;
            else
                return WarnData.OTHER;
        }
        return 0;
    }


    public static AlarmConfig converTo808AlarmType(int jt809AlarmType) {
        AlarmConfig a = new AlarmConfig();
        if (jt809AlarmType == 0x0107) {
            a.setAlarmType("1"); //生理疲劳驾驶预警
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE);
            return a;
        }else if (jt809AlarmType == 0x000E) {
            a.setAlarmType("2"); //接打电话报警
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE);
            return a;
        }else if (jt809AlarmType == 0x000F) {
            a.setAlarmType("3"); //生理疲劳驾驶预警
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE);
            return a;
        }else if (jt809AlarmType == 0x0010) {
            a.setAlarmType("4"); //分神驾驶报警
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE);
            return a;
        }else if (jt809AlarmType == 0x0011) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_DRIVER_STATE);
            a.setAlarmType("5"); //驾驶员异常报警
            return a;
        }else if (jt809AlarmType == 0x0102) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS);
            a.setAlarmType("1"); //前向碰撞
            return a;
        }else if (jt809AlarmType == 0x0103) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS);
            a.setAlarmType("2"); //车道偏离报警
            return a;
        }else if (jt809AlarmType == 0x0104) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS);
            a.setAlarmType("3"); //车距过近报警
            return a;
        }else if (jt809AlarmType == 0x0105) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS);
            a.setAlarmType("4"); //行人碰撞报警
            return a;
        }else if (jt809AlarmType == 0x0013) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS);
            a.setAlarmType("5"); //频繁变道报警
            return a;
        }else if (jt809AlarmType == 0x0109) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_BLIND_AREA);
            a.setAlarmType("1"); //后方接近报警
            return a;
        }else if (jt809AlarmType == 0x10A) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_BLIND_AREA);
            a.setAlarmType("2"); //左侧后方接近报警
            return a;
        }else if (jt809AlarmType == 0x10B) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_BLIND_AREA);
            a.setAlarmType("3"); //右侧后方接近报警
            return a;
        }else if (jt809AlarmType == 0x010C) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("1"); //胎压异常报警（胎压定时上报）
            return a;
        }else if (jt809AlarmType == 0x10D) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("2"); //胎压异常报警（胎压过高报警）
            return a;
        }else if (jt809AlarmType == 0x10E) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("3"); //胎压异常报警（胎压过低报警）
            return a;
        }else if (jt809AlarmType == 0x10F) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("4"); //胎压异常报警（胎温过高报警）
            return a;
        }else if (jt809AlarmType == 0x0110) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("5"); //胎压异常报警（传感器异常报警）
            return a;
        }else if (jt809AlarmType == 0x0111) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("6"); //胎压异常报警（胎压不平衡报警）
            return a;
        }else if (jt809AlarmType == 0x0112) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("7"); //胎压异常报警（慢漏气报警）
            return a;
        }else if (jt809AlarmType == 0x0113) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_ADAS_TIRE_PRESSURE);
            a.setAlarmType("8"); //胎压异常报警（电池电量低报警）
            return a;
        }else if (jt809AlarmType == 1) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("1"); //超速
            return a;
        }else if (jt809AlarmType == 2) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("2"); //疲劳
            return a;
        }else if (jt809AlarmType == 3) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("0"); //紧急
            return a;
        }else if (jt809AlarmType == 4) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType(AlarmRecord.TYPE_IN_AREA); //进入区域
            return a;
        }else if (jt809AlarmType == 5) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType(AlarmRecord.TYPE_CROSS_BORDER); //离开区域
            return a;
        }else if (jt809AlarmType == 0x0B) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType(AlarmRecord.TYPE_OFFSET_ROUTE); //偏离线路
            return a;
        }else if (jt809AlarmType == 0x12) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("29"); //碰撞
            return a;
        }else if (jt809AlarmType == 0x13) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("30"); //侧翻
            return a;
        }else if (jt809AlarmType == 0x122) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("18"); //累计驾驶超时报警
            return a;
        }else if (jt809AlarmType == 0x123) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("28"); //离线位移报警
            return a;
        }else if (jt809AlarmType == 0x130) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("4"); //GNSS 模块发生故障
            return a;
        }else if (jt809AlarmType == 0x131) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("5"); //GNSS 天线未接或被剪断
            return a;
        }else if (jt809AlarmType == 0x132) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("6"); //GNSS 天线短路
            return a;
        }else if (jt809AlarmType == 0x133) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("7"); //终端主电源欠压
            return a;
        }else if (jt809AlarmType == 0x134) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("8"); //终端主电源掉电
            return a;
        }else if (jt809AlarmType == 0x135) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("9"); //终端 LCD 或显示器故障
            return a;
        }else if (jt809AlarmType == 0x136) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("10"); //TTS 模块故障
            return a;
        }else if (jt809AlarmType == 0x137) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("11"); //摄像头故障
            return a;
        }else if (jt809AlarmType == 0x138) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("24"); //车辆 VSS 故障
            return a;
        }else if (jt809AlarmType == 0x139) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("25"); //1：车辆油量异常
            return a;
        }else if (jt809AlarmType == 0x140) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("12"); //道路运输证 IC 卡模块故障
            return a;
        }else if (jt809AlarmType == 0x141) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("26"); //1：车辆被盗
            return a;
        }else if (jt809AlarmType == 0x142) {
            a.setAlarmSource(AlarmRecord.ALARM_FROM_TERM);
            a.setAlarmType("19"); //超时停车
            return a;
        }
        return null;
    }

}

