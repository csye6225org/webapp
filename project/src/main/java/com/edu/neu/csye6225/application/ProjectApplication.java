package com.edu.neu.csye6225.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

@SpringBootApplication
public class ProjectApplication {

	public static void main(String[] args) {

		SpringApplication.run(ProjectApplication.class, args);

//		TimeZone tz = TimeZone.getTimeZone("UTC");
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
//		df.setTimeZone(tz);
//		System.out.println(df.format(new Date()));
//
//		LocalDateTime created_at = LocalDateTime.now();
//		ZonedDateTime created_at_zoned = created_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
////		ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("Z"));
//		System.out.println(created_at_zoned);

		System.out.print("Hello World");
	}

}
