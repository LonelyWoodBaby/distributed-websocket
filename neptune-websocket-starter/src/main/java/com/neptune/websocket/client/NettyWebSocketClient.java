package com.neptune.websocket.client;

import com.neptune.websocket.entity.message.MessageModel;
import com.neptune.websocket.utils.MessageUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class NettyWebSocketClient {
    private final String websocketServerUrl;
    private Channel nettyClient;
    private EventLoopGroup eventLoopGroup;

    public NettyWebSocketClient(String websocketServerUrl) {
        this.websocketServerUrl = websocketServerUrl;
    }


    public void createWebSocketClient(){
        try {
            this.nettyClient = createClient(null,null);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createWebSocketClient(String groupId, String clientId){
        try {
            this.nettyClient = createClient(groupId,clientId);
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Channel createClient(String groupId, String clientId) throws URISyntaxException, InterruptedException {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_BACKLOG,1024 * 1024 * 10)
                .group(eventLoopGroup)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new ChannelHandler[]{new HttpClientCodec(),new HttpObjectAggregator(1024 * 1024 * 10)});
                        p.addLast("hookedHandler",new WebSocketClientHandler());
                    }
                });
        URI websocketURI = new URI(websocketServerUrl);
        log.info("建立Websocket客户端连接：" + websocketServerUrl);
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(websocketURI, WebSocketVersion.V13, null, true, httpHeaders);
        Channel channel = bootstrap.connect(websocketURI.getHost(),websocketURI.getPort()).sync().channel();
        log.info("客户端" + channel.id() + "已建立");
        WebSocketClientHandler handler = (WebSocketClientHandler)channel.pipeline().get("hookedHandler");
        handler.setHandshaker(handshaker);
        handshaker.handshake(channel);
        handler.handshakeFuture().sync();
        if(groupId != null || clientId != null){
            sendRegister(groupId,clientId);
        }
        return channel;
    }

    public void sendMessage(String message){
        MessageModel messageModel = new MessageModel(message);
        sendMessage(messageModel);
    }

    public void sendMessage(String message, String groupId){
        MessageModel messageModel = new MessageModel(message,groupId);
        sendMessage(messageModel);
    }

    public void sendMessage(String message, String groupId, String clientId){
        MessageModel messageModel = new MessageModel(message,groupId,clientId);
        sendMessage(messageModel);
    }

    public void sendMessage(MessageModel messageModel){
        if(nettyClient == null) {
            throw new RuntimeException("发送失败，也许您没有创建WebsocketClient");
        }
        TextWebSocketFrame frame = new TextWebSocketFrame(MessageUtil.messageToJson(messageModel));
        log.info("开始发送日志：" + messageModel.getContent());
        nettyClient.writeAndFlush(frame).addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isSuccess()){
                log.info("消息接收处理成功");
            }else{
                log.error("消息接收处理失败：" +channelFuture.cause().getMessage() );
            }
        });
    }

    public void sendRegister(String groupId, String clientId){
        if((groupId == null || groupId.isEmpty()) && (clientId == null || clientId.isEmpty())){
            return;
        }
        if(nettyClient == null) {
            throw new RuntimeException("发送失败，也许您没有创建WebsocketClient");
        }
        TextWebSocketFrame frame = new TextWebSocketFrame(MessageUtil.messageToJson(MessageModel.registerClient(groupId,clientId)));
        log.info("注册连接:" + clientId);
        nettyClient.writeAndFlush(frame).addListener((ChannelFutureListener) channelFuture -> {
            if(channelFuture.isSuccess()){
                log.info("用户已注册");
            }else{
                log.error("用户注册失败：" +channelFuture.cause().getMessage() );
            }
        });
    }

    public void shutdown(){
        if(nettyClient.isActive()){
            eventLoopGroup.shutdownGracefully();
        }
    }
}
