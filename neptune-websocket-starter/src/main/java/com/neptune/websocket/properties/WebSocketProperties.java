package com.neptune.websocket.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lonely lee
 * @date 2019/04/10 15:59
 * @since v1.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "websocket.server")
public class WebSocketProperties {
    @Value("${websocket.server.ip}")
    private String websocketIp;
    @Value("${websocket.server.port:8990}")
    private int websocketPort;
    @Value("${websocket.server.urlPrefix:/ws}")
    private String urlPrefix;

    @Value("${websocket.server.runWithClient:false}")
    private boolean runWithClient;

    @Value("${websocket.client.autoStart:false}")
    private boolean clientAutoStart;

    @Value("${websocket.client.clientId:}")
    private String clientId;
    @Value("${websocket.client.groupId:}")
    private String groupId;

    public String getFullWebsocketUri(){
        String uri = "ws://" + this.websocketIp + ":" + websocketPort + urlPrefix;
        return uri;
    }
}
