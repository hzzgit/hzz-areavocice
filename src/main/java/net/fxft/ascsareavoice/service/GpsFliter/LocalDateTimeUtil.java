package net.fxft.ascsareavoice.service.GpsFliter;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class LocalDateTimeUtil {

	/**
	 * 将当前时间转化为时间戳，归0毫秒制
	 * @return
	 */
	public static Long getZeroMilliSecondByTime(){
		return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000 * 1000;
	}
	
	/**
	 * 将当前时间转化为时间戳，毫秒制
	 * @return
	 */
	public static Long getMilliSecondByTime(){
		return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
	}
	
	/**
	 * 将当前时间转化为时间戳，秒制
	 * @return
	 */
	public static Long getSecondByTime(){
		return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond();
	}
	
	/**
	 * 将指定字符串时间转化为时间戳，归0毫秒制
	 * @param time
	 * @return
	 */
	public static Long getZeroMilliSecondByTime(String time){
		if("".equals(time) || null == time){
			return null;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(time, df);
		return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000 * 1000;
	}
	
	/**
	 * 将指定字符串时间转化为时间戳，毫秒制
	 * @param time
	 * @return
	 */
	public static Long getMilliSecondByTime(String time){
		if("".equals(time)||null == time){
			return null;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(time, df);
		return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
	}
	
	/**
	 * 将指定字符串当前时间转化为时间戳，秒制
	 * @param time
	 * @return
	 */
	public static Long getSecondByTime(String time){
		if("".equals(time)||null == time){
			return null;
		}
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(time, df);
		return dateTime.toInstant(ZoneOffset.of("+8")).getEpochSecond();
	}
	
	/**
	 * 将时间戳转换成字符串， 类型的时间格式，时间格式为：yyyy-MM-dd HH:mm:ss
	 * @param timeStamp
	 * @return
	 */
    public static String TimeStampConverString(Long timeStamp){
    	if(timeStamp == null){
    		return null;
    	}
    	//如果传进来的时间戳不是毫秒制，则需要转成毫秒制
    	if(timeStamp.toString().length() == 10){
    		timeStamp = timeStamp * 1000;
    	}
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneOffset.of("+8")));
    }
    
    /**
     * 将时间戳转换成字符串，类型的时间格式，自定义时间格式
     * @param timeStamp
     * @param format
     * @return
     */
    public static String TimeStampConverString(Long timeStamp,String format){
    	if(timeStamp == null || "".equals(format) || null == format ){
    		return null;
    	}
    	//如果传进来的时间戳不是毫秒制，则需要转成毫秒制
    	if(timeStamp.toString().length() == 10){
    		timeStamp = timeStamp * 1000;
    	}
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(format);
        return ftf.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),ZoneOffset.of("+8")));
    }


	/**
	 * 字符串转LocalDateTime，目前只支持常用的/,-,无符号这三种格式
	 * @param time
	 * @return
	 */
	public static LocalDateTime DateConvertoLocalDateTime(Date time){

		Instant instant = time.toInstant();//An instantaneous point on the time-line.(时间线上的一个瞬时点。)
		ZoneId zoneId = ZoneId.systemDefault();//A time-zone ID, such as {@code Europe/Paris}.(时区)
		LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
		return localDateTime;
	}

    /**
     * 字符串转LocalDateTime，目前只支持常用的/,-,无符号这三种格式
     * @param time
     * @return
     */
    public static LocalDateTime StringConverLocalDateTime(String time){
    	if("".equals(time) || null == time){
			return null;
		}
    	String format = null;
    	//判断传入的时间格式是-还是/还是无符号
    	if(time.indexOf("/") > -1  && time.indexOf("-") == -1 && time.indexOf(":") > -1 && time.length() == 19){
    		format = "yyyy/MM/dd HH:mm:ss";
    	}else if(time.indexOf("-") > -1 && time.indexOf("/") == -1 && time.indexOf(":") > -1 && time.length() == 19){
    		format = "yyyy-MM-dd HH:mm:ss";
    	}else if(time.indexOf("-") == -1 && time.indexOf("/") == -1 && time.indexOf(":") == -1 && time.length() == 14){
    		format = "yyyyMMddHHmmss";
    	}else{
    		return null;
    	}
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(time,df);
    }
    /**
     * 计算两个时间的时间差（返回单位：秒）
     * @param endTime
     * @param startTime
     * @return
     */
    public static Long diffTime(String endTime,String startTime){
    	if("".equals(endTime) || null == endTime||"".equals(startTime) || null == startTime){
			return null;
		}
    	return LocalDateTimeUtil.StringConverLocalDateTime(endTime).toEpochSecond(ZoneOffset.of("+8")) 
		- LocalDateTimeUtil.StringConverLocalDateTime(startTime).toEpochSecond(ZoneOffset.of("+8"));
    }

	
	/**
	 * 根据时间戳获取LocalDateTime
	 * @param timeStamp
	 * @return
	 */
	public static LocalDateTime TimeStampConverLocalDateTime(Long timeStamp){
		if (timeStamp == null) {
            return null;
        }
		String localDateTimeStr = TimeStampConverString(timeStamp);
		LocalDateTime localDateTime = StringConverLocalDateTime(localDateTimeStr);
        return localDateTime;
	}
	
	/**
	 * 将当前的localTimeDate转化成yyyy-MM-dd HH:mm:ss格式
	 * @return
	 */
	public static String LocalDateTimeFormat(){
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		 return LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
	}
	
	/**
	 * 将当前的localTimeDate转化成自定义格式
	 * @param format
	 * @return
	 */
	public static String LocalDateTimeFormat(String format){
		if("".equals(format) || null == format){
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.now(ZoneOffset.of("+8")).format(formatter);
	}
	
	/**
	 * 将指定的localTimeDate转化成yyyy-MM-dd HH:mm:ss格式
	 * @param localTimeDate
	 * @return
	 */
	public static String LocalDateTimeFormat(LocalDateTime localTimeDate){
		if(localTimeDate == null){
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return localTimeDate.format(formatter);
	}
	
	/**
	 * 将指定的localTimeDate转化成自定义格式
	 * @param localTimeDate
	 * @param format
	 * @return
	 */
	public static String LocalDateTimeFormat(LocalDateTime localTimeDate,String format){
		if(localTimeDate == null || "".equals(format) || null == format){
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return localTimeDate.format(formatter);
	}
	
	/**
	 * 将指定的date转化为localTimeDate
	 * @param date
	 * @return
	 */
	public static LocalDateTime DateConverLocalDateTime(Date date) {
		if(date == null){
			return null;
		}
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }
	
	/**
	 * 将指定的localTimeDate转化为date
	 * @param localDateTime
	 * @return
	 */
	public static Date LocalDateTimeConverDate(LocalDateTime localDateTime) {
		if(localDateTime == null){
			return null;
		}
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }
	
	/**
	 * 获取传入字符串时间的当天的起始时间，即指定时间当日0点0分0秒
	 * @param time
	 * @return
	 */
	public static LocalDateTime getDayStart(String time){
		if("".equals(time) || null == time){
			return null;
		}
		LocalDateTime localDateTime = StringConverLocalDateTime(time);
		return localDateTime.withHour(0).withMinute(0).withSecond(0);
	}
	
	/**
	 * 获取传入字符串时间的当天的结束时间，即指定时间当日23点59分59秒
	 * @param time
	 * @return
	 */
	public static LocalDateTime getDayEnd(String time){
		if("".equals(time) || null == time){
			return null;
		}
		LocalDateTime localDateTime = StringConverLocalDateTime(time);
		return localDateTime.withHour(23).withMinute(59).withSecond(59);
	}
	
	/**
	 * 将YYYY-M-D格式的时间转化成YYYY-MM-DD格式的时间
	 * @return
	 */
	public static String timeFotmateConver(String time){
		SimpleDateFormat sdf1 = null;
		if(time.indexOf("-") > -1){
			sdf1 = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
		}else if(time.indexOf("/") > -1){
			sdf1 = new SimpleDateFormat("yyyy/M/d HH:mm:ss");
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String lastTime = null;
		try {
			Date paseTime = sdf1.parse(time);
			lastTime = sdf2.format(paseTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return lastTime;
	}

	/**
	 * 字符串转LocalDate
	 * @param date
	 * @return
	 */
	public static LocalDate StringConverLocalDate(String date){
		return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
	
	
}
