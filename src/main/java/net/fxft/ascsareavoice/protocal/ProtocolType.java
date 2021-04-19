package net.fxft.ascsareavoice.protocal;

/**
 * 用来记录视频服务器已经适配的特定协议版本,
 * 如果未适配说明是通用版本，那就可以直接播放通用的服务器
 */
public enum ProtocolType {
    粤标("yuebiao"),
    电子工牌("workcard");


    ProtocolType(String name) {
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 判断这些协议是否已经在视频服务器适配
     * @return
     */
    public static boolean isProtocolVersion(String name){
        //如果协议是空或者null
        if(name==null||"".equalsIgnoreCase(name.trim())){
            return false;
        }
        ProtocolType[] values = ProtocolType.values();
        for (ProtocolType value : values) {
            if(value.getName().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
