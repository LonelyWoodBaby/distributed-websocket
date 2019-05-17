package com.neptune.websocket.server;

import com.neptune.websocket.server.initializer.WebSocketChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyWebSocketServer {

    private final int webSocketPort;
    private final WebSocketChannelInitializer webSocketChannelInitializer;

    /**
     * NettyServer实例
     */
    private boolean isRunning = false;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;


    public NettyWebSocketServer(int webSocketPort,WebSocketChannelInitializer webSocketChannelInitializer) {
        this.webSocketPort = webSocketPort;
        this.webSocketChannelInitializer = webSocketChannelInitializer;
    }

    public void startWebSocketServer(){
        log.info("开始启动WebsocketServer服务器...");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            isRunning = true;
            log.info("Netty推送服务器已经开启...");
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(webSocketChannelInitializer);
            ChannelFuture channelFuture = serverBootstrap.bind(webSocketPort).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("Netty推送服务启动失败，错误信息为：" + e.getMessage());
        } finally {
            log.info("Netty推送服务器开始关闭...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            isRunning = false;
        }
    }

    public void shutdown(){
        log.info("Websocket服务器关闭中...");
        if(workerGroup != null){
            workerGroup.shutdownGracefully();
        }
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        workerGroup = null;
        bossGroup = null;
        isRunning = false;
        log.info("Websocket服务器已关闭...");
    }

    public boolean serverIsRunning(){
        return this.isRunning;
    }
}
