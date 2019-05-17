package com.neptune.example.server;

import com.neptune.websocket.enable.EnableWebSocketServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lonely lee
 * @date 2019/05/15 10:52
 * @since v1.0
 */
@EnableWebSocketServer
@SpringBootApplication
public class WebsocketServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebsocketServerApplication.class,args);
    }
}
