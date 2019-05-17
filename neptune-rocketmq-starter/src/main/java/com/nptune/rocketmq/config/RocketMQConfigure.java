package com.nptune.rocketmq.config;


import com.nptune.rocketmq.consumer.impl.MessageConsumer;
import com.nptune.rocketmq.producer.impl.MessageProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lonely lee
 * @date 2019/05/16 21:13
 * @since v1.0
 */
@Configuration
public class RocketMQConfigure {
    @Value("${neptune.mq.address}")
    private String mqAddress;
    @Value("${neptune.mq.instance.messageExample.topicName}")
    private String topicName;
    @Value("${neptune.mq.instance.messageExample.tagName}")
    private String tagName;

    @Bean(initMethod = "startProducerClient")
    public MessageProducer messageProducer(){
        MessageProducer messageProducer = new MessageProducer(topicName,tagName);
        messageProducer.setMqAddress(mqAddress);
        return messageProducer;
    }

    @Bean(initMethod = "startConsumerClient")
    public MessageConsumer messageConsumer(){
        MessageConsumer messageConsumer = new MessageConsumer(topicName);
        messageConsumer.setMqAddress(mqAddress);
        return messageConsumer;
    }
}
