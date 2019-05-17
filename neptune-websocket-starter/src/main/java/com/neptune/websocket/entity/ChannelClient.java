package com.neptune.websocket.entity;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.Map;

@Data
public class ChannelClient {
    private String remoteAddress;
    private long addTime;
    private Map<String, String> fiterConfig;
    private Channel channel;

    private String clientId;
    private String groupId;

    public ChannelClient(String remoteAddress, long addTime, Channel channel) {
        this.remoteAddress = remoteAddress;
        this.addTime = addTime;
        this.channel = channel;
    }

    public ChannelClient(String clientId, String remoteAddress, long addTime, Channel channel) {
        this.remoteAddress = remoteAddress;
        this.addTime = addTime;
        this.channel = channel;
        this.clientId = clientId;
    }

    public ChannelClient(String groupId, String clientId, String remoteAddress, long addTime, Channel channel) {
        this.remoteAddress = remoteAddress;
        this.addTime = addTime;
        this.channel = channel;
        this.clientId = clientId;
        this.groupId = groupId;
    }

}
