package com.ltmonitor.util;

public class FileUtil {

    public static String toLinuxFilePath(String path){
        return path.replace('\\', '/');
    }

}
