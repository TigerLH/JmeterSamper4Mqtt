package com.beecloud.jmeter.action;


import java.util.HashMap;
import java.util.Map;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/14.
 */
public class ActionMapper {
    private static Map<String,IMessage> map = new HashMap<String,IMessage>();
    static {
        map.put(ApplicationID.UNLOCK.name() + 2,xxxxxx);
    }

    public static IMessage getMessage(String key){
        return map.get(key);
    }
    public static boolean hasKey(String key){
        return map.containsKey(key);
    }
}
