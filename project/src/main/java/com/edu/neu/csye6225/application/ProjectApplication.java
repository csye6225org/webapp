package com.edu.neu.csye6225.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextInstanceDataAutoConfiguration;


@SpringBootApplication
		//(exclude = ContextInstanceDataAutoConfiguration.class)
//		(exclude = {DataSourceAutoConfiguration.class})
public class ProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProjectApplication.class, args);

		System.out.print("Hello World");
	}

}
