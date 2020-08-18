package com.example.logtracing.config;

import feign.Client;
import feign.Feign;
import feign.Logger;
import feign.opentracing.TracingClient;
import io.opentracing.Tracer;
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
    public Feign myappFeignBuilder(Client client, Tracer tracer) {
        return Feign.builder()
                .contract(new SpringMvcContract())
                //.client()
                .build();
    }

    @Bean
    @Primary
    public Client feignClient() {

        //return new feign.httpclient.ApacheHttpClient();
        ApacheHttpClient apacheHttpClient = new ApacheHttpClient();
        //apacheHttpClient.
        return apacheHttpClient;
    }

    @Bean
    @Primary
    public Logger.Level feighLoggerLevel() {
        return Logger.Level.FULL;
    }

}
