package com.beecloud.jmeter.action;

import com.beecloud.platform.protocol.core.constants.ApplicationID;
import com.beecloud.platform.protocol.core.constants.ErrorCode;
import com.beecloud.platform.protocol.core.element.ErrorInfo;
import com.beecloud.platform.protocol.core.element.FunctionCommandStatus;
import com.beecloud.platform.protocol.core.element.Identity;
import com.beecloud.platform.protocol.core.element.TimeStamp;
import com.beecloud.platform.protocol.core.header.ApplicationHeader;
import com.beecloud.platform.protocol.core.message.AckMessage;
import com.beecloud.platform.protocol.core.message.BaseMessage;
import com.beecloud.vehicle.spa.protocol.config.rawdata.lock.LockNotifyRawData;
import com.beecloud.vehicle.spa.protocol.config.rawdata.unlock.UnlockNotificationRawData;
import com.beecloud.vehicle.spa.protocol.message.ResponseMessage;
import com.google.gson.Gson;

import java.util.Date;

/**
 * @author yan.zhou
 * @description
 * @date 2017/3/21.
 */
public class LockMessage implements IMessage{

    @Override
    public BaseMessage produceAckMessage(long sequenceId,Identity identity) {
        AckMessage ackMessage = new AckMessage();

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.LOCK);
        applicationHeader.setProtocolVersion(0);
        applicationHeader.setStepId(3);
        applicationHeader.setSequenceId(sequenceId);

        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setDate(new Date());

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode(ErrorCode.OK);

        ackMessage.setApplicationHeader(applicationHeader);
        ackMessage.setErrorInfo(errorInfo);
        ackMessage.setTimeStamp(timeStamp);
        ackMessage.setIdentity(identity);
        return ackMessage;
    }

    @Override
    public BaseMessage produceResultMessage(long sequenceId,Identity identity) {
        ResponseMessage resMessage = new ResponseMessage();
        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.LOCK);
        applicationHeader.setProtocolVersion(0);
        applicationHeader.setSequenceId(sequenceId);
        applicationHeader.setStepId(5);

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode(ErrorCode.OK);

        FunctionCommandStatus functionCommandStatus = new FunctionCommandStatus();
        functionCommandStatus.setStatus(FunctionCommandStatus.CommandStatus.COMPLETE);
        LockNotifyRawData rawData = new LockNotifyRawData();
        rawData.setStatus(LockNotifyRawData.Status.SUCCESS);
        functionCommandStatus.setRawData(rawData.encode());

        TimeStamp timeStamp = new TimeStamp(new Date());


        resMessage.setApplicationHeader(applicationHeader);
        resMessage.setError(errorInfo);
        resMessage.setStatus(functionCommandStatus);
        resMessage.setTime(timeStamp);
        resMessage.setIdentity(identity);
        return resMessage;
    }


    public static void main(String...args){
        Identity identity = new Identity();
        identity.setIdentityCode(12312123);
        Gson gson = new Gson();
        BaseMessage baseMessage = new LockMessage().produceResultMessage(1000,identity);
       System.out.println(gson.toJson(baseMessage));
    }
}
