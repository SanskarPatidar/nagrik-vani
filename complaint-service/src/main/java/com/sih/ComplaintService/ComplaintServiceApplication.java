package com.sih.ComplaintService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class ComplaintServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComplaintServiceApplication.class, args);
	}

}
