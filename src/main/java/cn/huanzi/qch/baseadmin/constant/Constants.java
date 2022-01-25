package cn.huanzi.qch.baseadmin.constant;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public final static String wxClientId="wx6039cb4ab1ae383e";
    public final static String wxSecurityId="86355b47d46c04db34774f6369884f58";
    private final static  Map<String,String> costType = new HashMap<>();
    static {
        costType.put("back","老头乐");
        costType.put("fat","胖子乐");
    }

    public static String getCostName(String type){
        return costType.get(type);
    }

}
