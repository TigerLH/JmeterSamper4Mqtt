package com.beecloud.jmeter.objects;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/7.
 */
public class ConnectInfo {
    private String url;
    private String userName;
    private String password;
    private AuthObject authObject;
    private boolean cleanSession;
    private int keepAlive;
    private int qos;
    private boolean isRetain;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AuthObject getAuthObject() {
        return authObject;
    }

    public void setAuthObject(AuthObject authObject) {
        this.authObject = authObject;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public void setCleanSession(boolean cleanSession) {
        this.cleanSession = cleanSession;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isRetain() {
        return isRetain;
    }

    public void setRetain(boolean retain) {
        isRetain = retain;
    }

    @Override
    public String toString() {
        return "ConnectInfo{" +
                "url='" + url + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", authObject=" + authObject +
                ", cleanSession=" + cleanSession +
                ", keepAlive=" + keepAlive +
                ", qos=" + qos +
                ", isRetain=" + isRetain +
                '}';
    }
}
