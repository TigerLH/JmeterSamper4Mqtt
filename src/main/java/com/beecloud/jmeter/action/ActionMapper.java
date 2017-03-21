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
        /**锁车**/
        map.put(ApplicationID.LOCK.name() + 2,new LockMessage());
        /**启动引擎**/
        map.put(ApplicationID.START_CAR.name() + 2,new StartEngineMessage());
        /**获取部件状态**/
        map.put(ApplicationID.VEHICLE_COMPONENT_ACQUISITION.name() + 2,new StateFetchMessage());
        /**远程开压缩机**/
        map.put(ApplicationID.REMOTE_CONTROL.name() + 2,new ACRemoteControlMessage());
    }

    public static IMessage getMessage(String key){
        return map.get(key);
    }
    public static boolean hasKey(String key){
        return map.containsKey(key);
    }
}
