package com.beecloud.jmeter.constants;

import java.util.HashMap;
import java.util.Map;


public  class Constants {
    public static final String MQTT_AT_MOST_ONCE = "At most once [0]";
    public static final String MQTT_AT_LEAST_ONCE = "At least once [1]";
    public static final String MQTT_EXACTLY_ONCE = "Exactly Once [2]";
    public static final String MQTT_CLEAN_SESSION = "Clean Session";
    public static final String MQTT_AUTO_AUTH = "Auto Auth";
    public static final String MQTT_KEEP_ALIVE = "Keep Alive";
    public static final String MQTT_KEEP_ALIVE_DEFAULT = "0";
    public static final String MQTT_PASSWORD = "Password";
    public static final String MQTT_PROVIDER_URL = "Provider URL";
    public static final String MQTT_PUBLISHER_TITLE = "BeeCloud MQTT Publisher";
    public static final String MQTT_SEND_AS_RETAINED_MSG = "Retained";
    public static final String MQTT_SUBSCRIBER_TITLE = "BeeCloud MQTT Subscriber";
    public static final String MQTT_VEHICLE = "Vehicle";
    public static final String MQTT_URL_DEFAULT = "tcp://10.28.4.76:1883";
    public static final String MQTT_USER_PASSWORD = "";
    public static final String MQTT_USER_USERNAME = "";
    public static final String MQTT_USERNAME = "Username";
    public static final String MQTT_TEXT_AREA = "Text Message";
    public static final String MQTT_QOS = "Qos Option";
    public static final String VEHICLE_DEFAULT = "{\"vin\":\"VIN99999999999903\",\"iccid\":\"89860300000000000003\",\"tboxSerial\":\"FFFF0000000000000003\",\"imei\":\"000000000000003\",\"pid\":\"BEECLOUD\"}";
    public static final String RESULT_OK = "SUCCESS";
    public static final String RESULT_FAILED = "FAILED";
    private static Map<String,Integer> qos = new HashMap<String,Integer>();
    static {
        qos.put(MQTT_AT_MOST_ONCE,0);
        qos.put(MQTT_AT_LEAST_ONCE,1);
        qos.put(MQTT_EXACTLY_ONCE,2);
    }

    public static Integer getQos(String type){
        return qos.get(type);
    }
}
