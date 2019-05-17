package com.nptune.rocketmq.consumer.impl;

import com.nptune.rocketmq.consumer.BaseConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;

/**
 * @author lonely lee
 * @date 2019/05/16 17:36
 * @since v1.0
 */
@Slf4j
public class MessageConsumer extends BaseConsumer {

    public MessageConsumer(String topicName) {
        super("messageExample", topicName);
    }

    @Override
    protected void messageBytesHandler(byte[] result) throws Exception {
        String message = SerializationUtils.deserialize(result);
        log.info("消费者已收到消息：" + message);
    }
}
