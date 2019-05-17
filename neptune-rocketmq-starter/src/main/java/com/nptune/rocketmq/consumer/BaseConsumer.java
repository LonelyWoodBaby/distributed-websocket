package com.nptune.rocketmq.consumer;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * @author lonely lee
 * @date 2019/05/16 17:30
 * @since v1.0
 */
@Slf4j
public abstract class BaseConsumer {
    private String consumerName;
    private String topicName;
    private DefaultMQPushConsumer consumer;

    @Setter
    private String subExpression;
    @Setter
    protected String mqAddress = "localhost:9876";
    @Setter
    protected int batchMaxSize = 3;
    @Setter
    protected MessageModel messageModel = MessageModel.CLUSTERING;

    protected BaseConsumer(String consumerName, String topicName, String subExpression) {
        this.consumerName = consumerName;
        this.topicName = topicName;
        this.subExpression = subExpression;
        consumer = new DefaultMQPushConsumer(this.consumerName);
        consumer.setNamesrvAddr(mqAddress);
    }

    protected BaseConsumer(String consumerName, String topicName) {
        this(consumerName, topicName, "*");
    }

    public void startConsumerClient() throws MQClientException {
        consumer.setNamesrvAddr(this.mqAddress);
        consumer.subscribe(topicName, subExpression);

        consumer.setConsumeMessageBatchMaxSize(batchMaxSize);
        consumer.setMessageModel(messageModel);

        consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
            try {
                taskListHandler(list);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        consumer.start();
        log.info("消费者" + consumerName + "已经启动");
    }

    public void shutdownConsumer() {
        if (consumer != null){
            consumer.shutdown();
            log.info("消费者" + consumerName + "已关闭");
        }
    }

    /**
     * 将收到的报文队列进行处理，主要处理对象为List列表
     * @param resultList 收到的报文队列
     * @throws Exception 处理时如果遇见错误需要抛出异常
     */
    protected void taskListHandler(List<MessageExt> resultList) throws Exception {
        for(MessageExt messageExt : resultList){
            taskHandler(messageExt);
        }
    }

    /**
     * 如果需要对收到的报文进行处理，可以重写此方法，默认方法不做任何处理
     * @param messageExt 收到的报文完全体
     * @throws Exception 遇到异常时向上抛出
     */
    protected void taskHandler(MessageExt messageExt)throws Exception {
        //TODO 可以添加对于MessageExt的处理方法，将在messageBytesHandler方法前执行
        messageBytesHandler(messageExt.getBody());
    }

    /**
     * 对收到的报文的报文主体进行处理，如果需要整体上处理的话，建议重写其他方法
     * @param result 报文主体信息，byte格式
     * @throws Exception 遇到异常时向上抛出
     */
    protected abstract void messageBytesHandler(byte[] result)throws Exception;
}
