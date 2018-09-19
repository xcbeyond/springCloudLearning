package com.xcbeyond.springcloud.kafka.consumer.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 消息监听。</br>
 * 用于监听由kafka推送的消息
 * @author xcbeyond
 * 2018年9月17日下午5:09:17
 */
@Component
public class MsgListener {
	/**
     * 消息监听。</br>
     * 用于接收kafka推送过来的数据，并进行相应的数据处理。
     * @param message
     */
    @KafkaListener(topics = {"test-topic"})
	public void messageListener(String message){
    	System.out.println("收到kakfa推送的消息："+message);
	}

}
