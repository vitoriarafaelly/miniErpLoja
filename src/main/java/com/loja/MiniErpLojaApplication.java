package com.loja;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MiniErpLojaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MiniErpLojaApplication.class, args);
	}

}
