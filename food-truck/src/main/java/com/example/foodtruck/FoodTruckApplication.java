package com.example.foodtruck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@SpringBootApplication
@ComponentScan({"com.example.logtracing", "com.example.foodtruck"})
public class FoodTruckApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodTruckApplication.class, args);
	}

}
