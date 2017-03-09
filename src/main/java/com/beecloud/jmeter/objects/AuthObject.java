package com.beecloud.jmeter.objects;

import java.util.zip.CRC32;

/**
 * Created by dell on 2016/11/11.
 */
public class AuthObject {
    private String vin; //17位
    private String iccid; //20位
    private String tboxSerial; //20位
    private String imei;//15位
    private String pid; //16位

	/**
     * 自动补全
     * @param str
     * @param length
     * @return
     */
    public String formatStr(String str, int length){
      if (str == null){
           str="";
         }
      int strLen = str.getBytes().length;
      if (strLen == length){	//等于
          return str;
        } else if (strLen < length){	//小于
          int temp = length - strLen;
          String tem = "";
          for (int i = 0; i < temp; i++)
            {
              tem = tem + " ";
            }
          return str + tem;
        } else{		//大于
          return str.substring(0, length);
        }

    }
    
    /**
     * PID
     * @return
     */
    public String getPid() {
		return formatStr(pid,16);
	}

	public void setPid(String pid) {
		this.pid = pid;
	}
    
    public String getVin() {
        return formatStr(vin,17);
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getIccid() {
        return formatStr(iccid,20);
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public String getTboxSerial() {
        return formatStr(tboxSerial,20);
    }

    public void setTboxSerial(String tboxSerial) {
        this.tboxSerial = tboxSerial;
    }

    public String getImei() {
        return formatStr(imei,15);
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "AuthObject{" +
                "vin='" + vin + '\'' +
                ", iccid='" + iccid + '\'' +
                ", tboxSerial='" + tboxSerial + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }

    /**
     * 获取IdentityCode
     * @return
     */
    public long getIdentityCode(){
        String tmp = this.getVin()+this.getTboxSerial()+this.getImei()+this.getIccid()+this.getPid();
        CRC32 crc32 = new CRC32();
        crc32.reset();
        byte[] data = tmp.getBytes();
        for (byte b : data) {
            crc32.update(b);
        }
        long identityCode = crc32.getValue();
        return identityCode;
    }
}
