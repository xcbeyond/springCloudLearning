package com.xcbeyond.springcloud.kafka.producer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xcbeyond.springcloud.kafka.producer.utils.KafkaUtil;

/**
 * 消息发送
 * @author xcbeyond
 * 2018年9月17日下午4:52:57
 */
@RestController
@RequestMapping("/kafka")
public class MsgSendController {
	@Autowired
	public KafkaUtil kafkaUtil;
	
	@RequestMapping(value="/msgSend", method=RequestMethod.GET)
	public String MsgSend(@RequestParam("msg") String msg) {
		kafkaUtil.send("test-topic", msg);
		return "消息发送完毕!";
	}
}
