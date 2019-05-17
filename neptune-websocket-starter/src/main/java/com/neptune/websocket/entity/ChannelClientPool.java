package com.neptune.websocket.entity;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 一开始打算用本地缓存池建立此缓存，后继决定使用redis建立中间缓存
 * 考虑到如果建立本地缓存，则可能会导致
 */
public enum ChannelClientPool {
    /**
     * channel线程池实例
     */
    INSTANCE;
    private ChannelGroup channels;
    private Map<String,ChannelClient> channelClientMap;
    ChannelClientPool(){
        channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        channelClientMap = new ConcurrentHashMap<>();
    }

    public void addNewChannel(Channel channel){
        channels.add(channel);
        channelClientMap.put(channel.id().asLongText(),new ChannelClient(
               channel.remoteAddress().toString(), System.currentTimeMillis(),channel
        ));
    }

    public void removeChannel(Channel channel){
        channels.remove(channel);
        channelClientMap.remove(channel.id().asLongText());
    }

    public ChannelClient getClientEntity(Channel channel){
        String key = channelClientMap.keySet().stream()
                .filter(channelId -> channelId.equalsIgnoreCase(channel.id().asLongText()))
                .findAny().orElse(null);
        return key == null ? null : channelClientMap.get(key);
    }


    public List<ChannelClient> getClientEntityExcludeSelf(Channel channel){
        List<ChannelClient> resultList = channelClientMap.keySet().stream()
                .filter(channelId -> !channelId.equalsIgnoreCase(channel.id().asLongText()))
                .map(key -> channelClientMap.get(key))
                .collect(Collectors.toList());
        return resultList;
    }

    public List<ChannelClient> getAllClientEntity(){
        return channelClientMap.keySet().stream()
                .map(key -> channelClientMap.get(key))
                .collect(Collectors.toList());
    }
}
