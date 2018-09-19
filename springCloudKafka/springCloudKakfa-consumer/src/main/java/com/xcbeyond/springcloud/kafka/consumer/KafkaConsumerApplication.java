package com.xcbeyond.springcloud.kafka.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * kafka消费者 启动类
 * @author xcbeyond
 * 2018年9月17日下午5:05:06
 */
@SpringBootApplication
@EnableEurekaClient
public class KafkaConsumerApplication {
	public static void main(String[] args) {
		SpringApplication.run(KafkaConsumerApplication.class, args);
	}
}
