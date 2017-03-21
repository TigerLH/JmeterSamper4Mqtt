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
import com.beecloud.vehicle.spa.protocol.config.rawdata.vehiclestate.StateConfiguration;
import com.beecloud.vehicle.spa.protocol.constants.StateConfig;
import com.beecloud.vehicle.spa.protocol.element.ResponseState;
import com.beecloud.vehicle.spa.protocol.message.ResponseStateMessage;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.util.Date;
import java.util.List;

/**
 * @author yan.zhou
 * @description
 * @date 2017/3/21.
 */
public class StateFetchMessage implements IMessage{

    @Override
    public BaseMessage produceAckMessage(long sequenceId,Identity identity) {
        AckMessage ackMessage = new AckMessage();

        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.VEHICLE_COMPONENT_ACQUISITION);
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

        ResponseStateMessage responseStateMessage = new ResponseStateMessage();
        ApplicationHeader applicationHeader = new ApplicationHeader();
        applicationHeader.setApplicationID(ApplicationID.VEHICLE_COMPONENT_ACQUISITION);
        applicationHeader.setProtocolVersion(0);
        applicationHeader.setSequenceId(sequenceId);
        applicationHeader.setStepId(5);

        ErrorInfo errorInfo = new ErrorInfo();
        errorInfo.setErrorCode(ErrorCode.OK);

        List<StateConfiguration> stateConfigurationList= Lists.newArrayList(
                new StateConfiguration(StateConfig.AIR,28),
                new StateConfiguration(StateConfig.DOOR_LQ,2),
                new StateConfiguration(StateConfig.DOOR_LH,2),
                new StateConfiguration(StateConfig.DOOR_RH,2),
                new StateConfiguration(StateConfig.DOOR_RQ,2),
                new StateConfiguration(StateConfig.THE_HOOD,2),
                new StateConfiguration(StateConfig.TRUNK,2),
                new StateConfiguration(StateConfig.CHARGING_PORT,2),
                new StateConfiguration(StateConfig.control_lock,2),
                new StateConfiguration(StateConfig.WINDOW_LQ,2),
                new StateConfiguration(StateConfig.WINDOW_LH,2),
                new StateConfiguration(StateConfig.WINDOW_RF,2),
                new StateConfiguration(StateConfig.WINDOW_RH,3),
                new StateConfiguration(StateConfig.WINDOW_RQ,3),
                new StateConfiguration(StateConfig.BRAKE,60),
                new StateConfiguration(StateConfig.THROTTLE,60),
                new StateConfiguration(StateConfig.GEAR_LEVER,8),
                new StateConfiguration(StateConfig.HANDBRAKE,2),
                new StateConfiguration(StateConfig.IGNITION_STATE,2),
                new StateConfiguration(StateConfig.TOTAL_MILEAGE,300),
                new StateConfiguration(StateConfig.RESIDUAL_OIL_VOLUME,300));

        ResponseState responseState = new ResponseState();
        responseState.setStateConfigurations(stateConfigurationList);

        TimeStamp timeStamp = new TimeStamp(new Date());

        responseStateMessage.setApplicationHeader(applicationHeader);
        responseStateMessage.setIdentity(identity);
        responseStateMessage.setTimeStamp(timeStamp);
        responseStateMessage.setErrorInfo(errorInfo);
        responseStateMessage.setResponseState(responseState);
        byte [] data = responseStateMessage.encode();
        BaseMessage baseMessage = new BaseMessage(data);
        return baseMessage;
    }


    public static void main(String...args){
        Identity identity = new Identity();
        identity.setIdentityCode(12312123);
        Gson gson = new Gson();
        BaseMessage baseMessage = new StateFetchMessage().produceResultMessage(1000,identity);
       System.out.println(gson.toJson(baseMessage));
    }
}
