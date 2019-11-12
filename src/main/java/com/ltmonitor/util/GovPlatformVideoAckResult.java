package com.ltmonitor.util;

/**
 * 与上级运管交互的视频应答消息
 */
public class GovPlatformVideoAckResult {
    /**
     * 成功
     */
    public  static byte SUCCESS = 0;

    /**
     * 失败
     */
    public static byte FAILED = 1;

    /**
     * 不支持
     */
    public static byte NOT_SUPPORT = 2;

    /**
     * 会话已结束
     */
    public static byte SESSION_END = 3;

    /**
     * 时效口令错误
     */
    public  static byte AUTHORIZE_CODE_INCORRECT = 4;

    /**
     * 不满足跨域条件
     */
    public static byte NOT_MATCH_CORSS_DOMAIN = 5;
}
