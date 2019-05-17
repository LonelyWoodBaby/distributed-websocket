package com.neptune.example.server.consumer;

import com.neptune.websocket.entity.ChannelManagerPool;
import com.neptune.websocket.entity.message.MessageModel;
import com.nptune.rocketmq.consumer.BaseConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

/**
 * @author lonely lee
 * @date 2019/05/17 10:26
 * @since v1.0
 */
@Slf4j
public class MessageModelConsumer extends BaseConsumer {
    private final boolean selfReceive = true;

    protected MessageModelConsumer(String consumerName, String topicName) {
        super(consumerName, topicName);
    }

    @Override
    protected void messageBytesHandler(byte[] result) throws Exception {
        MessageModel messageModel = SerializationUtils.deserialize(result);
        //分布式情况下，消费者将不再支持注册操作，只用于消息推送
        ChannelManagerPool.INSTANCE.sendMessageModel(messageModel,selfReceive);
    }
}
