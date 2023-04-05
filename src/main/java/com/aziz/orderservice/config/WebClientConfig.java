package com.aziz.orderservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced//allowing client-side load balancing capabilities to the web client builder.
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
