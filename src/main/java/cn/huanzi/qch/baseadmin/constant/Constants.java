package cn.huanzi.qch.baseadmin.constant;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    private final static  Map<String,String> costType = new HashMap<>();
    static {
        costType.put("back","老头乐");
        costType.put("fat","胖子乐");
    }

    public static String getCostName(String type){
        return costType.get(type);
    }

}
