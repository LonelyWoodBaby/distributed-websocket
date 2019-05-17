package com.neptune.websocket.client;

import com.neptune.websocket.properties.WebSocketProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * @author lonely lee
 * @date 2019/04/10 18:22
 * @since v1.0
 */
@Slf4j
public class WebSocketClientAutoConfigure {
    @Autowired
    private WebSocketProperties webSocketProperties;

    @Bean
    public NettyWebSocketClient nettyWebSocketClient(){
        String websocketServerUri = webSocketProperties.getFullWebsocketUri();
        log.info("初始化WebSocketClient,请求服务地址为：" + websocketServerUri);
        NettyWebSocketClient nettyWebSocketClient = new NettyWebSocketClient(websocketServerUri);
        if(!webSocketProperties.isRunWithClient() && webSocketProperties.isClientAutoStart()){
            String clientId = webSocketProperties.getClientId();
            String groupId = webSocketProperties.getGroupId();

            if((clientId == null || clientId.isEmpty()) && (groupId == null || groupId.isEmpty())){
                nettyWebSocketClient.createWebSocketClient();
            }else{
                nettyWebSocketClient.createWebSocketClient(webSocketProperties.getGroupId(),webSocketProperties.getClientId());
            }
        }
        return nettyWebSocketClient;
    }
}
