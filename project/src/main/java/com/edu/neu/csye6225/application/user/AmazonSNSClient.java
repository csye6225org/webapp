package com.edu.neu.csye6225.application.user;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonSNSClient {

    @Value("${amazonProperties.region}")
    private String awsRegion;

    @Bean
    @Primary
    AmazonSNS generateSNSClient(){
        return AmazonSNSClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();
    }
}
