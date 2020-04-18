package com.example.cliente.config;

import feign.Client;
import feign.Feign;
import feign.Logger;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableFeignClients
public class FeignConfig {

    @Bean
    @Primary
    public Feign myappFeignBuilder() {
        return Feign.builder().contract(new SpringMvcContract()).build();
    }

    @Bean
    @Primary
    public Client feignClient() {

        //return new feign.httpclient.ApacheHttpClient();
        return new ApacheHttpClient();
    }

    @Bean
    @Primary
    public Logger.Level feighLoggerLevel() {
        return Logger.Level.FULL;
    }

}
