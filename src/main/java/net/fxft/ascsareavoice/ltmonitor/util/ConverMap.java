package net.fxft.ascsareavoice.ltmonitor.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型自动转换的Map类,自动强制转换的类
 */
public class ConverMap<K,V> extends HashMap implements Map {

    public String getString(String key,String defaultvalue){
        return  ConverterUtils.toString(get(key),defaultvalue);
    }

    public Integer getInt(String key,Integer defaultvalue){
        return  ConverterUtils.toInt(get(key),defaultvalue);
    }

    public Long getLong(String key,long defaultvalue){
        return  ConverterUtils.toLong(get(key),defaultvalue);
    }

    public double getDouble(String key,Double defaultvalue){
        return  ConverterUtils.toDouble(get(key),defaultvalue);
    }

    public float getFloat(String key,Float defaultvalue){
        return  ConverterUtils.toFloat(get(key),defaultvalue);
    }

    public Date getDate(String key, Date defaultvalue){
        return  ConverterUtils.toDate(get(key),defaultvalue);
    }

    public String getString(String key){
        return  ConverterUtils.toString(get(key),"");
    }

    public Integer getInt(String key){
        return  ConverterUtils.toInt(get(key),0);
    }

    public Long getLong(String key){
        return  ConverterUtils.toLong(get(key),0);
    }

    public double getDouble(String key){
        return  ConverterUtils.toDouble(get(key),0.0);
    }

    public float getFloat(String key){
        return  ConverterUtils.toFloat(get(key), (float) 0.0);
    }

    public Date getDate(String key){
        return  ConverterUtils.toDate(get(key),null);
    }



}
