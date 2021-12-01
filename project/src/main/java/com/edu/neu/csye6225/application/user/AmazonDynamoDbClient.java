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
package com.edu.neu.csye6225.application.user;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class AmazonDynamoDbClient {

    @Value("${amazonDynamodb.serviceEndpoint}")
    private String dynamodb_service_endpoint;

    @Value("${amazonDynamodb.tableName}")
    String dynamodb_tablename;

    @Value("${amazonProperties.region}")
    private String awsRegion;

    Logger logger = LoggerFactory.getLogger(AmazonDynamoDbClient.class);

    AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
//            .withEndpointConfiguration(
//                    new AwsClientBuilder
//                    .EndpointConfiguration(dynamodb_service_endpoint, awsRegion)
//            )
            .build();

    DynamoDB dynamoDB = new DynamoDB(client);
    Table table = dynamoDB.getTable(dynamodb_tablename);
    Item outcome;

    public AmazonDynamoDbClient() {
    }

    public Item get_item(String token){
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("id", token);
        try {
            logger.info("Attempting to read the item...");
            outcome = table.getItem(spec);
            logger.info("GetItem succeeded: " + outcome);

        }
        catch (Exception e) {
            logger.error("Unable to read item: " + token);
            logger.error(e.getMessage());
        }

        return outcome;
    }
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
