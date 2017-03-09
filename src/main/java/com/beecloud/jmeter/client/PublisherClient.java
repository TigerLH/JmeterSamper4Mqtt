package com.beecloud.jmeter.client;


import com.beecloud.jmeter.objects.ConnectInfo;
import com.beecloud.jmeter.utils.UuidUtil;
import org.apache.log.util.Closeable;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

import java.io.File;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/7.
 */
public class PublisherClient implements Closeable{
	private MqttAsyncClient client = null;
    private String mqtt_server_topic = "mqtt/server";
	private ConnectInfo connectInfo;

	public PublisherClient(ConnectInfo connectInfo){
		this.connectInfo = connectInfo;
	}

	/**
	 * 建立Mqtt连接
	 * 临时文件需动态生成,名称不能相同,否则会抛异常
	 */
	public void Connect(){
		String testPlanFileDir = System.getProperty("java.io.tmpdir") + File.separator + "mqtt" +
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
	 * 发布消息
	 * @param message
	 */
	public void publish(MqttMessage message){
		try {
			IMqttToken iMqttToken = client.publish(mqtt_server_topic, message);
			iMqttToken.waitForCompletion();
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	public boolean isConnect(){
		return client.isConnected();
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



