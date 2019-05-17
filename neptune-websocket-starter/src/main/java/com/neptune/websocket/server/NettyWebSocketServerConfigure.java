package com.neptune.websocket.server;

import com.neptune.websocket.client.NettyWebSocketClient;
import com.neptune.websocket.properties.WebSocketProperties;
import com.neptune.websocket.server.initializer.WebSocketChannelInitializer;
import com.neptune.websocket.server.initializer.handler.CustomWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CompletableFuture;

/**
 * @author lonely lee
 * @date 2019/04/11 11:25
 * @since v1.0
 */
@Slf4j
public class NettyWebSocketServerConfigure {

    @Autowired
    private WebSocketProperties webSocketProperties;
    @Autowired
    private CustomWebSocketHandler customWebSocketHandler;
    @Autowired
    private WebSocketChannelInitializer webSocketChannelInitializer;

    @Autowired(required = false)
    private NettyWebSocketClient nettyWebSocketClient;


    @Bean
    public WebSocketChannelInitializer getWebSocketChannelInitializer(){
        return new WebSocketChannelInitializer(customWebSocketHandler,webSocketProperties.getUrlPrefix());
    }
    @Bean
    @ConditionalOnMissingBean(name = "customWebSocketHandler")
    public CustomWebSocketHandler getCustomWebSocketHandler(){
        return new CustomWebSocketHandler();
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public NettyWebSocketServer nettyWebSocketServer(){
        log.info("初始化Websocket服务器，端口号绑定地址为：" + webSocketProperties.getWebsocketPort());
        NettyWebSocketServer nettyWebSocketServer = new NettyWebSocketServer(webSocketProperties.getWebsocketPort(),webSocketChannelInitializer);
        if(nettyWebSocketClient!=null && webSocketProperties.isRunWithClient()){
            CompletableFuture.runAsync(nettyWebSocketServer::startWebSocketServer);
            log.info("检测到已注入WebsocketClient，则建立Client连接");
            nettyWebSocketClient.createWebSocketClient();
        }else {
            nettyWebSocketServer.startWebSocketServer();
        }
        return nettyWebSocketServer;
    }

//    @Bean(initMethod = "startWebSocketServer",destroyMethod = "shutdown")
//    @ConditionalOnMissingBean
//    public NettyWebSocketServer nettyWebSocketServer(){
//        log.info("初始化Websocket服务器，端口号绑定地址为：" + webSocketProperties.getWebsocketPort());
//        return new NettyWebSocketServer(webSocketProperties.getWebsocketPort(),webSocketChannelInitializer);
//    }

}
