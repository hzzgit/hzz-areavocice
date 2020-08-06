package net.fxft.ascsareavoice.ltmonitor.util;

public class FileUtil {

    public static String toLinuxFilePath(String path){
        return path.replace('\\', '/');
    }

}
