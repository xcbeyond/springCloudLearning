package com.xcbeyond.springcloud.kafka.producer.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * kafka工具类
 * @author xcbeyond
 * 2018年9月17日下午4:40:19
 */
@Component
public class KafkaUtil {
	protected final static Logger log = LoggerFactory.getLogger(KafkaUtil.class);
	
    @Autowired
    private KafkaTemplate<?, String> kafkaTemplate;
    
    /**
     * 将数据异步推送至kafka,并且有结果回调,如果推送失败则重新推送
     * @param topic	主题
     * @param message 消息
     */
    @Async
    public void send(String topic, String message) {
        ListenableFuture<? extends SendResult<?, String>> future = kafkaTemplate.send(topic, message);
        future.addCallback(new ListenableFutureCallback<SendResult<?, String>>() {
            @Override
            public void onSuccess(SendResult<?, String> result) {
                log.info("消息推送成功.");
            }
            @Override
            public void onFailure(Throwable ex) {
                log.error("推送消息失败，重新发送...");
                send(topic, message);
            }
        });
    }
    
    /**
     * 消息监听。</br>
     * 用于接收kafka推送过来的数据，并进行相应的数据处理。
     * @param message
     */
    @KafkaListener(topics = {"test-topic"})
	public void messageListener(String message){
    	//TODO
    	System.out.println(message);
	}
}
