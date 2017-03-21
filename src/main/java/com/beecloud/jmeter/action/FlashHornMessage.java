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
import com.beecloud.vehicle.spa.protocol.config.rawdata.enableengine.EnableEngineStartNotifyRawData;
import com.beecloud.vehicle.spa.protocol.config.rawdata.remotecontrol.FunctionResult;
import com.beecloud.vehicle.spa.protocol.config.rawdata.remotecontrol.Result;
import com.beecloud.vehicle.spa.protocol.constants.ConfigTable;
import com.beecloud.vehicle.spa.protocol.constants.FunctionResultType;
import com.beecloud.vehicle.spa.protocol.message.RemoteNotifyMessage;
import com.beecloud.vehicle.spa.protocol.message.ResponseMessage;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * @author yan.zhou
 * @description
 * @date 2017/3/21.
 */
public class FlashHornMessage implements IMessage{

    @Override
    public BaseMessage produceAckMessage(long sequenceId,Identity identity) {
        AckMessage ackMessage = new AckMessage();

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.REMOTE_CONTROL);
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

        RemoteNotifyMessage remoteNotifyMessage = new RemoteNotifyMessage();
        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.REMOTE_CONTROL);
        applicationHeader.setProtocolVersion(0);
        applicationHeader.setSequenceId(sequenceId);
        applicationHeader.setStepId(5);

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode(ErrorCode.OK);

        FunctionCommandStatus functionCommandStatus = new FunctionCommandStatus();
        functionCommandStatus.setStatus(FunctionCommandStatus.CommandStatus.COMPLETE);
        List<Result> resultList= Lists.newArrayList(new Result(ConfigTable.HORN, FunctionResultType.SUCCESS));
        FunctionResult functionResult = new FunctionResult();
        functionResult.setResults(resultList);
        functionCommandStatus.setRawData(functionResult.encode());

        TimeStamp timeStamp = new TimeStamp(new Date());


        remoteNotifyMessage.setApplicationHeader(applicationHeader);
        remoteNotifyMessage.setErrorInfo(errorInfo);
        remoteNotifyMessage.setFunctionCommandStatus(functionCommandStatus);
        remoteNotifyMessage.setTimeStamp(timeStamp);
        remoteNotifyMessage.setIdentity(identity);
        byte [] data = remoteNotifyMessage.encode();
        BaseMessage baseMessage = new BaseMessage(data);
        return baseMessage;
    }

    public static void main(String...args){
        Identity identity = new Identity();
        identity.setIdentityCode(12312123);
        Gson gson = new Gson();
        BaseMessage baseMessage = new FlashHornMessage().produceResultMessage(1000,identity);
       System.out.println(gson.toJson(baseMessage));
    }
}
