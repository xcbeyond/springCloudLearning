package com.xcbeyond.springcloud.eureka.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 启动类
 * @author xcbeyond
 * 2018年9月28日下午4:36:44
 */
@SpringBootApplication
@EnableEurekaServer
class EurekaRestApplication {
	public static void main(String[] args) {
		SpringApplication.run(EurekaRestApplication.class, args);
	}
}
