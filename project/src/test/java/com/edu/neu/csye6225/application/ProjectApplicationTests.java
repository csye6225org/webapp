package com.edu.neu.csye6225.application;

import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserReadOnlyService;
import com.edu.neu.csye6225.application.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Testable
class ProjectApplicationTests {

//	UserService userService = new UserService();
	UserReadOnlyService userReadOnlyService = new UserReadOnlyService();
	@Test
	public void testUserResponseBodyCreation(){

		UUID id = UUID.randomUUID();
		LocalDateTime created_at = LocalDateTime.now();
		ZonedDateTime created_at_zoned = created_at
				.atZone(ZoneId.systemDefault())
				.withZoneSameInstant(ZoneId.of("Z"));


		Map<String, String> userDetailsExpected = new HashMap<>();

		userDetailsExpected.put("id", id.toString());
		userDetailsExpected.put("firstName", "fname");
		userDetailsExpected.put("lastName", "lname");
		userDetailsExpected.put("emailId", "fname.lname@gmail.com");
		userDetailsExpected.put("account_created", created_at_zoned.toString());
		userDetailsExpected.put("account_updated", created_at_zoned.toString());

		User u = new User();

		u.setId(id);
		u.setUsername("fname.lname@gmail.com");
		u.setFirst_name("fname");
		u.setLast_name("lname");
		u.setPassword("Fname@123");
		u.setAccount_updated(created_at_zoned);
		u.setAccount_created(created_at_zoned);

		Map<String, String> userDetailsToCheck = userReadOnlyService.userResponseBody(u);

		assertEquals(userDetailsExpected, userDetailsToCheck);

	}
}
