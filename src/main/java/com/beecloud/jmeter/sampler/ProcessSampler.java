package com.beecloud.jmeter.sampler;

import com.beecloud.jmeter.client.ProcessClient;
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

/**
 * @author hong.lin
 * @description
 * @date 2017/3/7.
 */
public class ProcessSampler extends AbstractSampler implements TestStateListener,ThreadListener {
    private static final String BROKER_URL = "mqtt.process.broker.url";
    private static final String VEHICLE = "mqtt.process.vehicle";
    private static final String RETAINED = "mqtt.process.message.retained";
    private static final String CLEAN_SESSION = "mqtt.process.clean.session";
    private static final String USERNAME = "mqtt.process.auth.username";
    private static final String PASSWORD = "mqtt.process.auth.password";
    private static final String KEEP_ALIVE = "mqtt.process.keep.alive";
    private static final String APP_TOPIC = "mqtt.process.app.topic";
    private static final String QOS = "mqtt.process.qos";

    private static final Logger log = LoggingManager.getLoggerForClass();
    private ProcessClient processClient = null;
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

    public void setAppTopic(String topic){
        setProperty(APP_TOPIC,topic);
    }

    public String getAppTopic(){
        return getPropertyAsString(APP_TOPIC);
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
        connectInfo.setAppTopic(getAppTopic());
        Gson gson = new Gson();
        AuthObject authObject = gson.fromJson(getVehicle() ,AuthObject.class);
        connectInfo.setAuthObject(authObject);
        log.debug(connectInfo.toString());
        processClient = new ProcessClient(connectInfo);
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
        result.setSampleLabel(Constants.MQTT_PROCESS_TITLE);
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
            if(processClient.isCompleted()){
                result.setSuccessful(true);
                result.sampleEnd();
                result.setResponseCode("OK");
                result.setResponseData(processClient.getAppMessage(),"UTF-8");
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
        if (processClient == null || !processClient.isConnect()) {
            try {
                initClient();
                processClient.Connect();
                processClient.subscribeTbox();
                processClient.subscribeApp();
            } catch (Exception e) {
                initException = e ;
            }
        }
    }

    @Override
    public void threadFinished() {
        processClient.close();
    }
}
