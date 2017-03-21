package com.beecloud.jmeter.action;

import com.beecloud.platform.protocol.core.constants.ApplicationID;

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
        /**开锁**/
        map.put(ApplicationID.UNLOCK.name() + 2,new UnLockMessage());
    }

    public static IMessage getMessage(String key){
        return map.get(key);
    }
    public static boolean hasKey(String key){
        return map.containsKey(key);
    }
}
