package com.beecloud.jmeter.sampler;

import com.alibaba.fastjson.JSON;
import com.beecloud.jmeter.client.PublisherClient;
import com.beecloud.jmeter.constants.Constants;
import com.beecloud.jmeter.constants.MessageType;
import com.beecloud.jmeter.objects.AuthObject;
import com.beecloud.jmeter.objects.ConnectInfo;
import com.beecloud.jmeter.utils.AuthUtil;
import com.beecloud.platform.protocol.core.datagram.BaseDataGram;
import com.beecloud.platform.protocol.core.message.AbstractMessage;
import com.beecloud.platform.protocol.core.message.AuthReqMessage;
import com.beecloud.platform.protocol.core.message.BaseMessage;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/7.
 */
public class PublisherSampler extends AbstractSampler implements TestStateListener {
    private static final String BROKER_URL = "mqtt.broker.url";
    private static final String VEHICLE = "mqtt.vehicle";
    private static final String RETAINED = "mqtt.message.retained";
    private static final String CLEAN_SESSION = "mqtt.clean.session";
    private static final String USERNAME = "mqtt.auth.username";
    private static final String PASSWORD = "mqtt.auth.password";
    private static final String KEEP_ALIVE = "mqtt.keep.alive";
    private static final String QOS = "mqtt.qos";
    private static final String MESSAGE = "mqtt.message";
    private static final String AUTO_AUTH = "mqtt.auth";
    private AuthObject authObject = null;
    private PublisherClient publisherClient = null;

    public boolean getAutoAuth(){
        return getPropertyAsBoolean(AUTO_AUTH);
    }

    public void setAutoAuth(boolean autoAuth){
        setProperty(AUTO_AUTH,autoAuth);
    }

    public String getMessage(){
        return getPropertyAsString(MESSAGE);
    }

    public void setMessage(String message){
        setProperty(MESSAGE,message);
    }

    public  String getBrokerUrl() {
        return getPropertyAsString(BROKER_URL);
    }

    public void setBrokerUrl(String brokerUrl){
        setProperty(BROKER_URL,brokerUrl);
    }

    public  String getVehicle() {
        return getPropertyAsString(VEHICLE);
    }

    public void setVehicle(String vehicle){
        setProperty(VEHICLE,vehicle);
    }

    public  boolean getRetained() {
        return getPropertyAsBoolean(RETAINED);
    }

    public void setRetained(boolean retained){
        setProperty(RETAINED,retained);
    }

    public  boolean getCleanSession() {
        return getPropertyAsBoolean(CLEAN_SESSION);
    }

    public void setCleanSession(boolean cleanSession){
        setProperty(CLEAN_SESSION,cleanSession);
    }

    public  String getUsername() {
        return getPropertyAsString(USERNAME);
    }

    public void setUsername(String username){
        setProperty(USERNAME,username);
    }

    public  String getPassword() {
        return getPropertyAsString(PASSWORD);
    }

    public void setPassword(String password){
        setProperty(PASSWORD,password);
    }

    public  int getKeepAlive() {
        return getPropertyAsInt(KEEP_ALIVE,10);
    }
    public void setKeepAlive(int keepAlive){
        setProperty(KEEP_ALIVE,keepAlive);
    }

    public  int getQos() {
        return getPropertyAsInt(QOS);
    }

    public void setQos(int qos){
        setProperty(QOS,qos);
    }


    /**
     * 转换GUI用户输入的Message为MqttMessage
     * @return
     */
    public MqttMessage getSendMessage(){
        /**
         * 容错处理,替换掉IdentityCode（输入的identityCode与Vin码信息可能不匹配）
         */
        String message = getMessage();
        Gson gson = new Gson();
        AuthObject authObject = gson.fromJson(getVehicle(),AuthObject.class);
        long identityCode = authObject.getIdentityCode();
        Object tobeReplace = JsonPath.parse(getMessage()).read("$.identity.identityCode");
        message = message.replace(String.valueOf(tobeReplace),String.valueOf(identityCode));

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setRetained(getRetained());
        mqttMessage.setQos(getQos());
        /**
         * 获取payload
         */
        String applicationId = JsonPath.parse(message).read("$.applicationHeader.applicationID");
        int stepId = JsonPath.parse(message).read("$.applicationHeader.stepId");
        String key = applicationId+stepId;
        AbstractMessage abstractMessage = (AbstractMessage)JSON.parseObject(message, MessageType.getMessage(key));
        byte[] data = abstractMessage.encode();
        BaseDataGram baseDataGram = new BaseDataGram();
        BaseMessage baseMessage = new BaseMessage(data);
        baseDataGram.addMessage(baseMessage);
        mqttMessage.setPayload(baseDataGram.encode());
        return mqttMessage;
    }


    /**
     * 构造认证消息
     * @return
     */
    public MqttMessage getAuthMessage(){
        AuthReqMessage authReqMessage = AuthUtil.getAuthReqMessage(authObject);
        byte[] data = authReqMessage.encode();
        BaseDataGram baseDataGram = new BaseDataGram();
        BaseMessage baseMessage = new BaseMessage(data);
        baseDataGram.addMessage(baseMessage);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setRetained(getRetained());
        mqttMessage.setQos(getQos());
        mqttMessage.setPayload(baseDataGram.encode());
        return mqttMessage;
    }



    /**
     * 初始化Client
     * @throws MqttException
     * @throws IOException
     */
    private void initClient() throws MqttException, IOException {
        ConnectInfo connectInfo = new ConnectInfo();
        connectInfo.setUrl(getBrokerUrl());
        connectInfo.setQos(getQos());
        connectInfo.setRetain(getRetained());
        connectInfo.setCleanSession(getCleanSession());
        connectInfo.setUserName(getUsername());
        connectInfo.setPassword(getPassword());
        connectInfo.setKeepAlive(getKeepAlive());
        Gson gson = new Gson();
        authObject = gson.fromJson(getVehicle() ,AuthObject.class);
        connectInfo.setAuthObject(authObject);
        publisherClient = new PublisherClient(connectInfo);
    }

    @Override
    public void testStarted() {

    }

    @Override
    public void testStarted(String s) {

    }

    @Override
    public void testEnded() {

    }

    @Override
    public void testEnded(String s) {

    }


    @Override
    public SampleResult sample(Entry entry) {
        SampleResult result = new SampleResult();
        result.setSampleLabel(Constants.MQTT_PUBLISHER_TITLE);
        result.setSamplerData(getVehicle());
        result.sampleStart();
            try {
                if (publisherClient == null || !publisherClient.isConnect()) {
                    initClient();
                    publisherClient.Connect();
                }
                if(getAutoAuth()){
                    publisherClient.publish(getAuthMessage());
                }
                publisherClient.publish(getSendMessage());
                result.setSuccessful(true);
                result.sampleEnd();
                result.setResponseCode(Constants.RESULT_OK);
                return result;
            } catch (Exception e) {
                result.sampleEnd();
                result.setSuccessful(false);
                StringWriter stringWriter = new StringWriter();
                e.printStackTrace(new PrintWriter(stringWriter));
                result.setResponseData(stringWriter.toString(), null);
                result.setResponseMessage("Unable publish messages.\n" + "Exception: " + e.toString());
                result.setDataType(SampleResult.TEXT);
                result.setResponseCode(Constants.RESULT_FAILED);
                return result;
            }
    }
}
