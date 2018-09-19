package com.xcbeyond.springcloud.kafka.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * kafka生产者 启动类
 * @author xcbeyond
 * 2018年9月17日下午4:32:15
 */
@SpringBootApplication
@EnableEurekaClient
public class KafkaProducerApplication {
	public static void main(String[] args) {
		SpringApplication.run(KafkaProducerApplication.class, args);
	}
}
