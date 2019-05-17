package com.neptune.websocket.server.initializer.handler;

import com.neptune.websocket.entity.ChannelManagerPool;
import com.neptune.websocket.entity.message.MessageModel;
import com.neptune.websocket.entity.message.SpreadType;
import com.neptune.websocket.utils.MessageUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class CustomWebSocketHandler extends BaseWebSocketHandler{
    /**
     * 作为自己是否可以收到此消息
     */
    private final boolean selfReceive = true;
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) {
        String messageJson = textWebSocketFrame.text();
        MessageModel messageModel = MessageUtil.transJsonToModel(messageJson);
        if(messageModel.getSpreadType() == SpreadType.INITIAL){
            ChannelManagerPool.INSTANCE.addNewChannel(channelHandlerContext.channel(),messageModel.getGroupId(),messageModel.getClientId());
            log.info("用户：" + messageModel.getClientId() + "执行注册，组名：" + messageModel.getGroupId());
        }else{
            ChannelManagerPool.INSTANCE.sendMessageModel(messageModel,selfReceive);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        log.info("WebSocket服务器获取新的连接：" + ctx.channel().id());
        //如果是新建连接，则可以作为一个无组别和无clientId的普通链接进入，而且只能收到广播消息
        ChannelManagerPool.INSTANCE.addNewChannel(ctx.channel(),"","");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        log.info("Websocket检测到" + ctx.channel().id() + "执行退出");
        ChannelManagerPool.INSTANCE.removeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
                log.info("用户：" + ctx.channel() + "执行退出操作");
                ChannelManagerPool.INSTANCE.removeChannel(ctx.channel());
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println(cause.getMessage());
        log.error("捕获到异常:" + cause.getMessage());
        ChannelManagerPool.INSTANCE.removeChannel(ctx.channel());
        super.exceptionCaught(ctx, cause);
    }
}
