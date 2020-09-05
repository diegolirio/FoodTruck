package com.example.logtracing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfig {
    @Value("${token.client}")
    private String client;
    @Value("${token.secret}")
    private String secret;

}
