package com.beecloud.jmeter.utils;

import java.util.UUID;

/**
 * Created by dell on 2016/11/10.
 */
public class UuidUtil {
    public static void main(String ...args){
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
    }

    public static String getUuid(){
        return UUID.randomUUID().toString();
    }
}
