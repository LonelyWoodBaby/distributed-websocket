package com.neptune.websocket.enable;

import com.neptune.websocket.client.WebSocketClientAutoConfigure;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @author lonely lee
 * @date 2019/04/11 10:52
 * @since v1.0
 */
public class WebSocketClientImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        Map<String, Object> result = annotationMetadata.getAnnotationAttributes(EnableWebSocketClient.class.getName());
        return new String[]{WebSocketClientAutoConfigure.class.getName()};
    }
}
