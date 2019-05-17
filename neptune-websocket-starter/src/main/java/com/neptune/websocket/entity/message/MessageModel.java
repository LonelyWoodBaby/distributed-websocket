package com.neptune.websocket.entity.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lonely lee
 * @date 2019/05/14 11:14
 * @since v1.0
 */
@Data
public class MessageModel implements Serializable {
    private String content;
    private long sendTime;
    private SpreadType spreadType;

    private String groupId;
    private String clientId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String sendClientId;

    public MessageModel() {
    }

    public MessageModel(String content) {
        this.content = content;
        this.sendTime = System.currentTimeMillis();
        spreadType = SpreadType.BROADCAST;
    }

    public MessageModel(String content, String groupId) {
        this(content);
        this.groupId = groupId;
        this.spreadType = SpreadType.GROUP;
    }

    public MessageModel(String content, String groupId, String clientId) {
        this(content,groupId);
        this.clientId = clientId;
        this.spreadType = SpreadType.POINT;
    }

    public MessageModel sendClientId(String sendClientId){
        this.setSendClientId(sendClientId);
        return this;
    }

    public MessageModel spreadType(SpreadType spreadType){
        this.setSpreadType(spreadType);
        return this;
    }

    public static MessageModel registerClient(String groupId, String clientId){
        return new MessageModel("创建连接",groupId,clientId)
                .spreadType(SpreadType.INITIAL).sendClientId(clientId);

    }
}
