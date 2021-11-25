package com.edu.neu.csye6225.application.picture;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonS3Client {

    @Value("${amazonProperties.region}")
    private String awsRegion;

    @Bean
    @Primary
    AmazonS3 generateS3Client(){
        return AmazonS3ClientBuilder
                .standard()
                .withRegion(awsRegion)
                .build();
    }

}
