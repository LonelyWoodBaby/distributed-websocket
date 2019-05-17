package com.neptune.websocket.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neptune.websocket.entity.NettyWebsocketException;
import com.neptune.websocket.entity.message.MessageModel;

import java.io.IOException;

/**
 * @author lonely lee
 * @date 2019/05/14 11:49
 * @since v1.0
 */
public class MessageUtil {
    private MessageUtil(){}

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String messageToJson(String message){
        MessageModel messageModel = new MessageModel(message);
        return transferJson(messageModel);
    }

    public static String messageToJson(MessageModel messageModel){
        return transferJson(messageModel);
    }

    public static MessageModel transJsonToModel(String json){
        try {
            return objectMapper.readValue(json,MessageModel.class);
        } catch (IOException e) {
            throw new NettyWebsocketException("json转换时出现异常：" + e.getMessage());
        }
    }

    private static String transferJson(Object object){
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new NettyWebsocketException("json转换时出现异常：" + e.getMessage());
        }
    }
}
