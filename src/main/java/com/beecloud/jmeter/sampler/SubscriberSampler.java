package com.beecloud.jmeter.sampler;

import com.beecloud.jmeter.client.SubscriberClient;
import com.beecloud.jmeter.constants.Constants;
import com.beecloud.jmeter.objects.AuthObject;
import com.beecloud.jmeter.objects.ConnectInfo;
import com.google.gson.Gson;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.testelement.ThreadListener;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/7.
 */
public class SubscriberSampler extends AbstractSampler implements TestStateListener,ThreadListener {
    private static final String BROKER_URL = "mqtt.subscriber.broker.url";
    private static final String VEHICLE = "mqtt.subscriber.vehicle";
    private static final String RETAINED = "mqtt.subscriber.message.retained";
    private static final String CLEAN_SESSION = "mqtt.subscriber.clean.session";
    private static final String USERNAME = "mqtt.subscriber.auth.username";
    private static final String PASSWORD = "mqtt.subscriber.auth.password";
    private static final String KEEP_ALIVE = "mqtt.subscriber.keep.alive";
    private static final String QOS = "mqtt.subscriber.qos";

    private static final Logger log = LoggingManager.getLoggerForClass();
    private SubscriberClient subscirberClient = null;
    private Exception initException = null;

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
        AuthObject authObject = gson.fromJson(getVehicle() ,AuthObject.class);
        connectInfo.setAuthObject(authObject);
        System.out.println(connectInfo);
        subscirberClient = new SubscriberClient(connectInfo);
    }



    @Override
    public void testStarted() {
        if (log.isDebugEnabled()) {
            log.debug("Thread started " + new Date());
            log.debug("MQTT SubScriber Sampler: ["
                    + Thread.currentThread().getName() + "], hashCode=["
                    + hashCode() + "]");
        }
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
        final SampleResult result = new SampleResult();
        result.setSampleLabel(Constants.MQTT_SUBSCRIBER_TITLE);
        result.sampleStart();
        if(null != initException){
            result.sampleEnd();
            result.setSuccessful(false);
            StringWriter stringWriter = new StringWriter();
            initException.printStackTrace(new PrintWriter(stringWriter));
            result.setResponseData(stringWriter.toString(), null);
            result.setResponseMessage("Unable publish messages.\n" + "Exception: " + initException.toString());
            result.setDataType(SampleResult.TEXT);
            result.setResponseCode("FAILED");
            return result;
        }
        long start = System.currentTimeMillis();
        while (true){
            if(subscirberClient.isCompleted()){
                result.setSuccessful(true);
                result.sampleEnd();
                result.setResponseCode("OK");
                result.setResponseData(subscirberClient.getReceiveMessage(),"UTF-8");
                return result;
            }
            if(System.currentTimeMillis()-start>1000*10){
                result.setSuccessful(false);
                result.sampleEnd();
                result.setResponseCode("TIMEOUT");
                return result;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void threadStarted() {
        if (subscirberClient == null || !subscirberClient.isConnect()) {
            try {
                initClient();
                subscirberClient.Connect();
                subscirberClient.subscribe();
            } catch (Exception e) {
                initException = e ;
            }
        }
    }

    @Override
    public void threadFinished() {
        subscirberClient.close();
    }
}
