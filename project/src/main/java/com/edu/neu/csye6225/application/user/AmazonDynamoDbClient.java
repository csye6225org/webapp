package com.edu.neu.csye6225.application.user;

/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AmazonDynamoDbClient {

    @Value("${amazonProperties.region}")
    private String awsRegion;

    @Value("amazonDynamodb.serviceEndpoint")
    private String dynamodb_service_endpoint;


    @Bean
    @Primary
    AmazonDynamoDB generateDynamoDbClient(){
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(dynamodb_service_endpoint, awsRegion)
                )
                .build();

        return client;
    }

//    DynamoDB dynamoDB = new DynamoDB(client);
//    Table table = dynamoDB.getTable(dynamodb_tablename);

//    int year = 2015;
//    String title = "The Big New Movie";

//        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", year, "title", title);
//
//        try {
//            System.out.println("Attempting to read the item...");
//            Item outcome = table.getItem(spec);
//            System.out.println("GetItem succeeded: " + outcome);
//
//        }
//        catch (Exception e) {
//            System.err.println("Unable to read item: " + year + " " + title);
//            System.err.println(e.getMessage());
//        }

}
