package com.neptune.example.client.producer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lonely lee
 * @date 2019/05/17 10:59
 * @since v1.0
 */
@Configuration
public class MessageModelProducerConfigure {
    @Bean(initMethod = "startProducerClient")
    public MessageModelProducer messageModelProducer(){
        return new MessageModelProducer("messageModel","messageWebsocket","tagA");
    }
}
