package com.beecloud.jmeter.client;


import com.beecloud.jmeter.objects.ConnectInfo;
import com.beecloud.jmeter.utils.UuidUtil;
import com.beecloud.platform.protocol.core.datagram.BaseDataGram;
import com.beecloud.platform.protocol.core.header.ApplicationHeader;
import com.beecloud.platform.protocol.core.message.BaseMessage;
import org.apache.log.util.Closeable;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;
import java.util.List;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/6.
 */
public class SubscriberClient implements MqttCallback,Closeable{
	private MqttAsyncClient client = null;
    private String mqtt_server_topic = "mqtt/server";
	private String tbox_topic = "  mqtt/vehicle/%s";
	private ConnectInfo connectInfo;

	public SubscriberClient(ConnectInfo connectInfo){
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
			client.setCallback(this);
			IMqttToken conToken = client.connect(conOpt);
			conToken.waitForCompletion();
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
	public void subscribe() throws MqttException {
		IMqttToken iMqttToken = client.subscribe(String.format(tbox_topic,connectInfo.getAuthObject().getVin()),connectInfo.getQos());
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
			IMqttToken iMqttToken = client.publish(mqtt_server_topic, msg);
			iMqttToken.waitForCompletion();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void connectionLost(Throwable throwable) {

	}

	@Override
	public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
		BaseDataGram baseDataGram = new BaseDataGram(mqttMessage.getPayload());
		List<BaseMessage> baseMessages = baseDataGram.getMessages();
		BaseMessage baseMessage = baseMessages.get(0);
		//需要根据StepId和ApplicationId判断对应的业务
		ApplicationHeader applicationHeader = baseMessage.getApplicationHeader();
		String name = applicationHeader.getApplicationID().name();
		int stepId = applicationHeader.getStepId();
		String key = name+stepId;
//		BaseMessage ackMessage = MessageMapper.getMessage(key).produceAckMessage();
//		BaseMessage resultMessage = MessageMapper.getMessage(key).produceResultMessage();
//		this.publish(ackMessage);
//		this.publish(resultMessage);
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



