package net.fxft.ascsareavoice.ltmonitor.util;

import com.ltmonitor.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

/**
 * java 8  LocalDateTime 时间转换工具类
 *
 * @author xiaowen
 * @date 2016年11月1日 @ version 1.0
 */
public final class TimeUtils {


    // 无参数的构造函数,防止被实例化
    private TimeUtils() {
    }

    ;


    /**
     * 获取当前时间
     * <p>
     * <p>
     * 时间格式
     *
     * @return
     */
    public static String getCurrentDateTime() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }


    public static LocalDate datetolocaldate(Date dates) {
        Instant instant = dates.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = instant.atZone(zoneId).toLocalDate();
        return localDate;
    }

    public static String getbeforeDateTime(int beforeday) {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, -beforeday);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(calendar.getTime());
    }

    /**
     * Date转换为LocalDateTime
     *
     * @param date
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        Instant instant = date.toInstant();//An instantaneous point on the time-line.(时间线上的一个瞬时点。)
        ZoneId zoneId = ZoneId.systemDefault();//A time-zone ID, such as {@code Europe/Paris}.(时区)
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        Date date = Date.from(zdt.toInstant());
        return date;
    }


    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd
     *
     * @param dateDate
     * @param
     * @return
     */
    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ");
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    /**
     * 将短时间格式时间转换为字符串 yyyy-MM-dd h24:mm:ss
     *
     * @param dateDate
     * @param
     * @return
     */
    public static String dateTodetailStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    /**
     * 给时间加上几个小时
     *
     * @param hour 需要加的时间
     * @return
     */
    public static Date addDateMinut(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hour);// 24小时制
        date = cal.getTime();
        return date;

    }

    public static String getdatebyDAY(int day) {
        Calendar cal = Calendar.getInstance();
//System.out.println(Calendar.DATE);//5
        cal.add(Calendar.DATE, -day);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time);
    }

    public static String gettimetodateday(Date date) {

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public static String getdatebyMonth(Date time, int month) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.MONTH, -month);
        Date m = c.getTime();
        String mon = format.format(m);
        return mon;
    }


    //将字符串的转成时间类型
    public static Date date(String date_str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");//改为需要的东西
            Date date = formatter.parse(date_str);
            return date;
        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    //将字符串的转成时间类型
    public static Date datebymonth(String date_str) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");//改为需要的东西
            Date date = formatter.parse(date_str);
            return date;
        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    //将字符串的转成时间类型
    public static Date todatetime(String date_str) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(date_str);
            return date;
        } catch (NullPointerException e) {
            return null;
        } catch (ParseException e) {
        }
        return null;
    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     *
     * @param nowTime   当前时间
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     * @author jqlin
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);


        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);


        Calendar end = Calendar.getInstance();
        end.setTime(endTime);


        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间戳转时间
     * @param timeStamp
     * @return 字符串类型的时间
     */
    public static Date timeStamptotime(long timeStamp)   {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//这个是你要转成后的时间的格式
        Date da= new Date(timeStamp);
        return da;
    }
    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

    /**
     * 获取两个时间之间，按照每天来区分时间的集合
     *
     * @return
     */
    public static List<Map<String, Date>> getdataallday(Date start, Date end) {
        int dateco = differentDays(start, end);
        List<Map<String, Date>> alldate = new ArrayList<>();
        for (int i = 0; i < dateco + 1; i++) {
            Date startTime = null;
            Date endTime = null;
            if (i == 0 && i != dateco) {
                startTime = start;
                endTime = getchangdaydate(start, 1);
            } else if (i != 0 && i != dateco) {
                startTime = getchangdaydate(start, i);
                endTime = getchangdaydate(start, i + 1);
            } else if (i != 0 && i == dateco) {
                startTime = getchangdaydate(start, i);
                endTime = end;
            } else {
                startTime = start;
                endTime = end;
            }
            Map<String, Date> data = new HashMap<>();
            data.put("startTime", startTime);
            data.put("endTime", endTime);
            alldate.add(data);
        }
        return alldate;
    }

    private static Date getchangdaydate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date date1 = c.getTime();
        return date1;
    }

    /**
     * 计算两个时间相差多少分钟
     *
     * @param endDate
     * @param nowDate
     * @return
     */
    public static long getDatePoorfen(Date endDate, Date nowDate) {
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少分钟
        long min = diff  / nm;
        return min;
    }


    /**
     * 判断两个时间是否相差多少分钟以上
     *
     * @return
     */
    public static boolean isdifferminute(Date startTime, Date endTime, int fenzhong) {
        try {
            Long secend = new Long(fenzhong * 60);
            LocalDateTime start = date2LocalDateTime(startTime);
            LocalDateTime end = date2LocalDateTime(endTime);
            Duration between = Duration.between(start, end);
            if (between.getSeconds() > secend) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 判断两个时间是否相差多少分钟以上
     *
     * @return
     */
    public static long isdifferminute(Date startTime, Date endTime) {
        try {
            LocalDateTime start = date2LocalDateTime(startTime);
            LocalDateTime end = date2LocalDateTime(endTime);
            Duration between = Duration.between(start, end);
           return   between.getSeconds();
        } catch (Exception e) {
            return  0;
        }

    }


    //获取离线时长,当前时间减去上次在线时间，但是必须是离线的车辆
    public static String getdur(Date onlineTime) {
        double number = 0.1 * DateUtil.getSeconds(
                onlineTime, new Date()) / 6;
        String durstr = Constants.getIntervalDescr(number);
        return durstr;
    }

    /**
     * 获取一段时间之前或者之后的时间
     *
     * @param date
     * @param field
     * @param i
     * @return
     */
    public static Date getDate(Date date, int field, int i) {
        if (date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, i);
        return calendar.getTime();
    }

    /**
     * 获取两个时间相差多少秒
     *
     * @return
     */
    public static long gettwodatediff(Date startTime, Date endTime) {
        try {
            LocalDateTime start = date2LocalDateTime(startTime);
            LocalDateTime end = date2LocalDateTime(endTime);
            Duration between = Duration.between(start, end);
            return between.getSeconds();

        } catch (Exception e) {
            return 0;
        }

    }

    public static void main(String[] args) {
        Date sendTime = new Date();
        Date startTime = new Date();
        Date endTime = new Date();
        sendTime = TimeUtils.todatetime("2020-08-11 15:00:31");
        startTime = TimeUtils.todatetime("2020-08-11 10:00:00");
        endTime = TimeUtils.todatetime("2020-08-11 23:25:54");
        boolean arg = isEffectiveDate(sendTime, startTime, endTime);

     //   Date a=TimeUtils.date("2020-06-12 02:02:11");
      //  Date todatetime = TimeUtils.todatetime("2020-06-12");
        System.out.println(arg);
    }


}
