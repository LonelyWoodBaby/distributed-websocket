package com.neptune.example.server.consumer;

import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lonely lee
 * @date 2019/05/17 10:51
 * @since v1.0
 */
@Configuration
public class MessageConsumerConfigure {
    @Bean(initMethod = "startConsumerClient")
    public MessageModelConsumer messageModelConsumer(){
        MessageModelConsumer messageModelConsumer = new MessageModelConsumer("messageModel","messageWebsocket");
        //设定消费者接受消息为广播模式
        messageModelConsumer.setMessageModel(MessageModel.BROADCASTING);
        return messageModelConsumer;
    }
}
