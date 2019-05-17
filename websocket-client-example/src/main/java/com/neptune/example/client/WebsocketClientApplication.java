package com.neptune.example.client;

import com.neptune.websocket.enable.EnableWebSocketClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lonely lee
 * @date 2019/05/15 10:59
 * @since v1.0
 */
@SpringBootApplication
@EnableWebSocketClient
public class WebsocketClientApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebsocketClientApplication.class,args);
    }
}
