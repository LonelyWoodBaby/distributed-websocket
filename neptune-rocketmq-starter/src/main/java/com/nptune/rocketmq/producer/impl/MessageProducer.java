package com.nptune.rocketmq.producer.impl;

import com.nptune.rocketmq.producer.BaseProducer;
import org.apache.commons.lang3.SerializationUtils;

/**
 * @author lonely lee
 * @date 2019/05/16 17:25
 * @since v1.0
 */
public class MessageProducer extends BaseProducer<String> {

    public MessageProducer(String topicName, String tagName) {
        super("messageExample", topicName, tagName);
    }

    @Override
    protected byte[] transformMessage(String s) {
        return SerializationUtils.serialize(s);
    }
}
