package com.beecloud.jmeter.action;

import com.beecloud.platform.protocol.core.message.BaseMessage;

/**
 * @author hong.lin
 * @description
 * @date 2017/3/6.
 */
public interface IMessage {
    BaseMessage produceAckMessage();
    BaseMessage produceResultMessage();
}
