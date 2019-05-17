package com.neptune.websocket.entity;

/**
 * @author lonely lee
 * @date 2019/05/14 12:01
 * @since v1.0
 */
public class NettyWebsocketException extends RuntimeException {
    public NettyWebsocketException(String message) {
        super(message);
    }

    public NettyWebsocketException(String message, Throwable cause) {
        super(message, cause);
    }

    public NettyWebsocketException(Throwable cause) {
        super(cause);
    }

    public NettyWebsocketException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
