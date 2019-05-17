package com.nptune.rocketmq.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MQCreateException extends RuntimeException {
    public MQCreateException(String message) {
        log.error(message);
    }
}
