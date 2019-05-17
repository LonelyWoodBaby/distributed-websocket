package com.nptune.rocketmq.producer;

import com.nptune.rocketmq.exception.MQCreateException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * @author lonely lee
 * @date 2019/05/16 17:01
 * @since v1.0
 */
@Slf4j
public abstract class BaseProducer<T> {
    private String producerName;
    private String topicName;
    private String tagName;
    private DefaultMQProducer producer;
    private boolean isRunning = false;

    @Setter
    protected String mqAddress = "localhost:9876";
    @Setter
    protected int retryTimesWhenSendFailed = 2;

    public BaseProducer(String producerName, String topicName, String tagName) {
        this.producerName = producerName;
        this.topicName = topicName;
        this.tagName = tagName;
        producer = createProducer(this.producerName,mqAddress,this.topicName);
    }

    private DefaultMQProducer createProducer(String groupName, String address, String topicName){
        DefaultMQProducer producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(address);
        producer.setCreateTopicKey(topicName);
        return producer;
    }

    public void startProducerClient() throws MQCreateException {
        if(producer == null){
            producer = createProducer(this.producerName,this.mqAddress,this.topicName);
            producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
        }
        if(isRunning){
            return;
        }
        try {
            producer.start();
            log.info("生产者"+producerName+"已启动");
            isRunning = true;
        } catch (MQClientException e) {
            isRunning = false;
            e.printStackTrace();
            throw new MQCreateException("创建生产者" + producerName + "时出现错误：" + e.getMessage());
        }
    }

    public SendResult sendMessage(T t) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if(!isRunning){
            throw new InterruptedException("生产者处于关闭状态，无法发送消息");
        }
        byte[] messageBytes  = transformMessage(t);
        Message message = new Message(topicName,tagName,messageBytes);
        log.debug("开始发送日志");
        return producer.send(message);
    }

    public void shutdownProducer(){
        if(producer != null){
            producer.shutdown();
            log.info("生产者" + producerName + "已关闭");
        }
        this.isRunning = false;
    }

    /**
     * 将准备发送的对象进行处理，并转化为字节格式
     * @param t 要发送的对象
     * @return 转化后的字节组
     */
    abstract protected byte[] transformMessage(T t);
}
