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
public class ProcessClient implements MqttCallback,Closeable{
	private MqttAsyncClient client = null;
    private String mqtt_server_topic = "mqtt/server";
	private String tbox_topic = "  mqtt/vehicle/%s";
	private ConnectInfo connectInfo;
    private boolean isOver = false;
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


    /**
	 * UUid做为ClientId
	 * @return
     */
	public String getClientId(){
		return UuidUtil.getUuid();
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
		Gson gson = new Gson();
		System.out.println(gson.toJson(baseMessage));
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

	/**
	 * 判断是否已经回复Message
	 * @return
     */
	public boolean isCompleted(){
		return this.isOver;
	}

	@Override
	public void connectionLost(Throwable throwable) {

	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		System.out.println("receive Topic:"+s);
		BaseDataGram baseDataGram = new BaseDataGram(mqttMessage.getPayload());
		List<BaseMessage> baseMessages = baseDataGram.getMessages();
		BaseMessage baseMessage = baseMessages.get(0);
		//需要根据StepId和ApplicationId判断对应的业务
		ApplicationHeader applicationHeader = baseMessage.getApplicationHeader();
		String name = applicationHeader.getApplicationID().name();
		int stepId = applicationHeader.getStepId();
		if(s.equals(connectInfo.getAppTopic())){
			isOver = true;
			return;
		}
		String key = name+stepId;
		System.out.println(key);
		if(!ActionMapper.hasKey(key)){
			return;
		}
		Constructor con = MessageType.getMessage(key).getConstructor(byte[].class);
		Object object = con.newInstance(baseMessage.encode());
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
			client.disconnect();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}
}



