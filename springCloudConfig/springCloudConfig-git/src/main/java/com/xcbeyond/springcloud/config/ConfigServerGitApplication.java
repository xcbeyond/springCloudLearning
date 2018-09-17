package com.xcbeyond.springcloud.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 配置中心。</br>
 * 以git方式存储配置
 * @author xcbeyond
 * 2018年9月17日下午11:44:10
 */
@SpringBootApplication
//开启配置服务器的支持
@EnableConfigServer
//开启作为Eureka Server的客户端的支持
@EnableEurekaClient
public class ConfigServerGitApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConfigServerGitApplication.class, args);
	}
}
