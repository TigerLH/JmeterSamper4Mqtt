package com.beecloud.jmeter.client;


import com.beecloud.jmeter.action.ActionMapper;
import com.beecloud.jmeter.constants.MessageType;
import com.beecloud.jmeter.objects.ConnectInfo;
import com.beecloud.jmeter.utils.UuidUtil;
import com.beecloud.platform.protocol.core.datagram.BaseDataGram;
import com.beecloud.platform.protocol.core.element.Identity;
import com.beecloud.platform.protocol.core.header.ApplicationHeader;
import com.beecloud.platform.protocol.core.message.BaseMessage;
import com.google.gson.Gson;
import org.apache.log.util.Closeable;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/6.
 */
public class ProcessClient extends Thread implements MqttCallback,Closeable{
	private MqttAsyncClient client = null;
    private String mqtt_server_topic = "mqtt/server";
	private String tbox_topic = "  mqtt/vehicle/%s";
	private ConnectInfo connectInfo;
	private boolean isCompleted = false;
	private String appMessage = "";
	private Long StartTime;
	public ProcessClient(ConnectInfo connectInfo){
		 this.connectInfo = connectInfo;
	}


	/**
	 * 建立Mqtt连接
	 */
	public void Connect(){
		String testPlanFileDir = System.getProperty("java.io.tmpdir") + File.separator + "mqtt"  +
				File.separator + Thread.currentThread().getId();
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(testPlanFileDir);
		String password = connectInfo.getPassword();
		boolean cleanSession = connectInfo.isCleanSession();
		String userName = connectInfo.getUserName();
		String url = connectInfo.getUrl();
		int keepAlive = connectInfo.getKeepAlive();
		try {
			MqttConnectOptions conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(cleanSession);
			if (password != null && !password.isEmpty()) {
				conOpt.setPassword(password.toCharArray());
			}
			if (userName != null && !userName.isEmpty()) {
				conOpt.setUserName(userName);
			}
			conOpt.setKeepAliveInterval(keepAlive);
			client = new MqttAsyncClient(url, this.getClientId(),dataStore);
			IMqttToken conToken = client.connect(conOpt);
			conToken.waitForCompletion();
			client.setCallback(this);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		super.run();
		this.Connect();
		try {
			this.subscribeTbox();
			this.subscribeApp();
		} catch (MqttException e) {
			e.printStackTrace();
		}
		this.startRecord();
		while (true){
			if(isCompleted()){

			}
			if(System.currentTimeMillis()-StartTime>1000*10){

			}
		}
	}


	/**
	 * 计时开始
     */
	public void startRecord(){
		StartTime = System.currentTimeMillis();
	}

    /**
	 * UUid做为ClientId
	 * @return
     */
	public String getClientId(){
		return UuidUtil.getUuid();
	}



	public String getAppMessage(){
		return this.appMessage;
	}
    /**
	 * 订阅消息
	 * @throws MqttException
     */
	public void subscribeTbox() throws MqttException {
		if("".equals(connectInfo.getAuthObject())){
			return;
		}
		IMqttToken iMqttToken = client.subscribe(String.format(tbox_topic,connectInfo.getAuthObject().getVin()).trim(),connectInfo.getQos());
		iMqttToken.waitForCompletion();
	}


    /**
	 * 订阅App消息
	 * @throws MqttException
     */
	public void subscribeApp() throws MqttException {
		if("".equals(connectInfo.getAppTopic())){
			return;
		}
		IMqttToken iMqttToken = client.subscribe(connectInfo.getAppTopic().trim(),connectInfo.getQos());
		iMqttToken.waitForCompletion();
	}


	public boolean isConnect(){
		return client.isConnected();
	}

    /**
	 * 发布消息
	 * @param baseMessage
     */
	public void publish(BaseMessage baseMessage){
		BaseDataGram baseDataGram = new BaseDataGram();
		baseDataGram.addMessage(baseMessage);
		MqttMessage msg = new MqttMessage();
		msg.setQos(connectInfo.getQos());
		msg.setRetained(connectInfo.isRetain());
		msg.setPayload(baseDataGram.encode());
		try {
			client.publish(mqtt_server_topic, msg);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public boolean isCompleted(){
		return this.isCompleted;
	}

	@Override
	public void connectionLost(Throwable throwable) {

	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		System.out.println("receive Topic:"+s);
//		System.out.println(s.equals(connectInfo.getAppTopic()));
		if(s.equals(connectInfo.getAppTopic())){
			appMessage = new String(mqttMessage.getPayload(),"UTF-8");
			isCompleted = true;
			return;
		}
		BaseDataGram baseDataGram = new BaseDataGram(mqttMessage.getPayload());
		List<BaseMessage> baseMessages = baseDataGram.getMessages();
		BaseMessage baseMessage = baseMessages.get(0);
		//需要根据StepId和ApplicationId判断对应的业务
		ApplicationHeader applicationHeader = baseMessage.getApplicationHeader();
		String name = applicationHeader.getApplicationID().name();
		int stepId = applicationHeader.getStepId();
		String key = name+stepId;
		if(!ActionMapper.hasKey(key)){
			return;
		}
		Constructor con = MessageType.getMessage(key).getConstructor(byte[].class);
		Object object = con.newInstance(baseMessage.encode());
		Gson gson = new Gson();
		System.out.println(gson.toJson(object));
		Identity identity = (Identity)object.getClass().getMethod("getIdentity").invoke(object);
		long sequenceId = applicationHeader.getSequenceId();
		BaseMessage ackMessage = ActionMapper.getMessage(key).produceAckMessage(sequenceId,identity);
		BaseMessage resultMessage = ActionMapper.getMessage(key).produceResultMessage(sequenceId,identity);
		this.publish(ackMessage);
		this.publish(resultMessage);
	}


	@Override
	public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

	}

	@Override
	public void close(){
		try {
			IMqttToken iMqttToken = client.disconnect(null,null);
			iMqttToken.waitForCompletion();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}



