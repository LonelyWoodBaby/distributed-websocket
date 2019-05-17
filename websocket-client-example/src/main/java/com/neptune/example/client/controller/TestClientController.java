package com.neptune.example.client.controller;

import com.neptune.example.client.producer.MessageModelProducer;
import com.neptune.websocket.client.NettyWebSocketClient;
import com.neptune.websocket.entity.message.MessageModel;
import com.neptune.websocket.properties.WebSocketProperties;
import com.neptune.websocket.utils.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author lonely lee
 * @date 2019/05/15 11:04
 * @since v1.0
 */
@RestController
@RequestMapping("/websocket")
@Slf4j
public class TestClientController {
    @Autowired
    private WebSocketProperties webSocketProperties;
    @Autowired
    private NettyWebSocketClient nettyWebSocketClient;
    @Autowired
    private MessageModelProducer messageModelProducer;

    @RequestMapping("/create")
    public String testCreateClient(){
        String websocketServerUri = webSocketProperties.getFullWebsocketUri();
        String clientId;
        for(int i = 0; i < 3; i++){
            clientId = "client000" + i;
            NettyWebSocketClient nettyWebSocketClient = new NettyWebSocketClient(websocketServerUri);
            nettyWebSocketClient.createWebSocketClient();
            nettyWebSocketClient.sendRegister("group0001",clientId);
        }
        for(int i = 0; i < 2; i++){
            clientId = "client100" + i;
            NettyWebSocketClient nettyWebSocketClient = new NettyWebSocketClient(websocketServerUri);
            nettyWebSocketClient.createWebSocketClient();
            nettyWebSocketClient.sendRegister("group0002",clientId);
        }

        for(int i = 0; i < 2; i++){
            NettyWebSocketClient nettyWebSocketClient = new NettyWebSocketClient(websocketServerUri);
            nettyWebSocketClient.createWebSocketClient();
        }
        return "hahaha";
    }


    @RequestMapping("/group/{groupId}")
    public String sendMessage(@PathVariable String groupId){
        MessageModel messageModel = new MessageModel("测试信息",groupId);
        nettyWebSocketClient.sendMessage(messageModel);
        return "hahahah";
    }

    @RequestMapping("/client/{clientId}")
    public String sendMessage2(@PathVariable String clientId){
        MessageModel messageModel = new MessageModel("测试信息","group0001",clientId);
        nettyWebSocketClient.sendMessage(messageModel);
        return "hahahah";
    }

    @RequestMapping("/mq")
    public String sendMessageByMQ() throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        MessageModel messageModel = new MessageModel("MQ消息发送","group0001");
        messageModelProducer.sendMessage(messageModel);
        return "hjahahaha";
    }

    public static void main(String[] args) {
        String message = MessageUtil.messageToJson(MessageModel.registerClient("group0003","client0001"));
        System.out.println(message);
    }
}
