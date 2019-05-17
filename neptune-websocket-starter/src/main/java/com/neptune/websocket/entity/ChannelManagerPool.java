package com.neptune.websocket.entity;

import com.neptune.websocket.entity.message.MessageModel;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 一开始打算用本地缓存池建立此缓存，后继决定使用redis建立中间缓存
 * 考虑到如果建立本地缓存，则可能会导致
 */
@Slf4j
public enum ChannelManagerPool {
    /**
     * channel线程池实例
     */
    INSTANCE;
    private ChannelGroup channels;
    /**
     * 以channelID为key（防止重复），创建缓存
     */
    private Map<String,ChannelClient> channelClientMap;
    ChannelManagerPool(){
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelClientMap = new ConcurrentHashMap<>();
    }

    public void addNewChannel(Channel channel, String groupId, String clientId){
        channels.add(channel);
        channelClientMap.put(channel.id().asLongText(),
                new ChannelClient(groupId,clientId,
                        channel.remoteAddress().toString(),
                        System.currentTimeMillis(),
                        channel
                )
        );
    }

    public void removeChannel(Channel channel){
        channels.remove(channel);
        channelClientMap.remove(channel.id().asLongText());
    }

    public ChannelClient getClientEntity(Channel channel){
        return channelClientMap.keySet().stream()
                .filter(channelId -> channelId.equalsIgnoreCase(channel.id().asLongText()))
                .map(channelId -> channelClientMap.get(channelId))
                .findAny().orElse(null);
    }


    public List<ChannelClient> getClientEntityExcludeSelf(Channel channel){
        return channelClientMap.keySet().stream()
                .filter(channelId -> !channelId.equalsIgnoreCase(channel.id().asLongText()))
                .map(key -> channelClientMap.get(key))
                .collect(Collectors.toList());
    }

    public List<ChannelClient> getAllClientEntity(){
        return channelClientMap.keySet().stream()
                .map(key -> channelClientMap.get(key))
                .collect(Collectors.toList());
    }

    public List<ChannelClient> getClientEntityByGroup(String groupId){
        return channelClientMap.keySet().stream()
                .map(channelId -> channelClientMap.get(channelId))
                .filter(channelClient -> groupId.equals(channelClient.getGroupId()))
                .collect(Collectors.toList());
    }

    public List<ChannelClient> getClientEntityByClient(String clientId){
        return channelClientMap.keySet().stream()
                .map(channelId -> channelClientMap.get(channelId))
                .filter(channelClient -> clientId.equals(channelClient.getClientId()))
                .collect(Collectors.toList());
    }

    public void sendMessageModel(MessageModel messageModel, boolean selfReceive){
        List<ChannelClient> allChannelList = getAllClientEntity();
        allChannelList.stream().filter(channelClient ->{
            switch (messageModel.getSpreadType()){
                case BROADCAST:
                    //所有的已注册连接均可以收到消息
                    log.debug("广播消息");
                    return true;
                case GROUP:
                    //只能是自己组别内的成员收到消息
                    log.debug(messageModel.getGroupId() +" 组内广播消息");
                    return (messageModel.getGroupId() != null)
                            && messageModel.getGroupId().equalsIgnoreCase(channelClient.getGroupId());
                case POINT:
                    log.debug("向用户" + messageModel.getClientId() +"推送消息");
                    return messageModel.getClientId() != null
                            && messageModel.getClientId().equalsIgnoreCase(channelClient.getClientId());
                default:
                    return false;
            }
        }).filter(channelClient -> selfReceive
                || messageModel.getSendClientId().isEmpty()
                || ! messageModel.getSendClientId().equalsIgnoreCase(channelClient.getClientId())
        ).forEach(channelClient -> {
            log.debug("给频道：【" + channelClient.getChannel().id().asLongText() + "】发消息,该频道组名：" + channelClient.getGroupId() + ",频道名：" + channelClient.getClientId());
            channelClient.getChannel().writeAndFlush(new TextWebSocketFrame(messageModel.getContent()));
        });
    }
}
