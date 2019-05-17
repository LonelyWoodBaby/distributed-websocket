package com.neptune.websocket.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author lonely lee
 * @date 2019/04/11 10:53
 * @since v1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(WebSocketServerImportSelector.class)
public @interface EnableWebSocketServer {
    boolean autoStartServer() default true;
}
