package com.neptune.websocket.enable;

import com.neptune.websocket.server.NettyWebSocketServerConfigure;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author lonely lee
 * @date 2019/04/11 10:52
 * @since v1.0
 */
public class WebSocketServerImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> result = annotationMetadata.getAnnotationAttributes(EnableWebSocketServer.class.getName());
        if(result.containsKey("autoStartServer") && result.get("autoStartServer").equals(Boolean.TRUE)){
            return new String[]{NettyWebSocketServerConfigure.class.getName()};
        }
        return new String[0];
    }
}
