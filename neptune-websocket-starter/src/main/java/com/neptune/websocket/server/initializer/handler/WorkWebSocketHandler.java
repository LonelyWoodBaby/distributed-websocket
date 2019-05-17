package com.neptune.websocket.server.initializer.handler;

import com.neptune.websocket.entity.ChannelClient;
import com.neptune.websocket.entity.ChannelClientPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@ChannelHandler.Sharable
@Slf4j
public class WorkWebSocketHandler extends BaseWebSocketHandler{

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {
        log.debug("接收到推送消息");
        log.info("发来的消息是：" + textWebSocketFrame.text());
        Channel msgComer = channelHandlerContext.channel();
        List<ChannelClient> allChannelList = ChannelClientPool.INSTANCE.getAllClientEntity();
        for(ChannelClient channelClient: allChannelList){
            if(msgComer != channelClient.getChannel()){
                log.debug("给其他人发消息");
//                channelClient.getChannel().writeAndFlush(new TextWebSocketFrame("[" + msgComer.remoteAddress() + "]" + textWebSocketFrame.text()));
                channelClient.getChannel().writeAndFlush(new TextWebSocketFrame(textWebSocketFrame.text()));
            }else{
                log.debug("给自己发消息");
                channelClient.getChannel().writeAndFlush(new TextWebSocketFrame("我自己发了一段消息，就像这样子：" + textWebSocketFrame.text()));
            }
        }
//        channelHandlerContext.channel().writeAndFlush(new TextWebSocketFrame("来自服务端: " + LocalDateTime.now()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("WebSocket服务器获取新的连接：" + ctx.channel().id());
        ChannelClientPool.INSTANCE.addNewChannel(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("Websocket检测到" + ctx.channel().id() + "执行退出");
        ChannelClientPool.INSTANCE.removeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                System.out.println("没事儿干就走！！！！！！");
                ChannelClientPool.INSTANCE.removeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }
}
