package com.neptune.example.client.producer;

import com.neptune.websocket.entity.message.MessageModel;
import com.nptune.rocketmq.producer.BaseProducer;
import org.apache.commons.lang3.SerializationUtils;

/**
 * @author 李亚斌
 * @date 2019/05/17 10:55
 * @since v1.1
 */
public class MessageModelProducer extends BaseProducer<MessageModel> {
    public MessageModelProducer(String producerName, String topicName, String tagName) {
        super(producerName, topicName, tagName);
    }

    @Override
    protected byte[] transformMessage(MessageModel messageModel) {
        return SerializationUtils.serialize(messageModel);
    }
}
