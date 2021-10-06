package com.edu.neu.csye6225.application;

import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;


@SpringBootTest
public class UserControllerTest {

    @Test
    public void contextLoads() {
    }

    @Mock
    UserRepository userDao = Mockito.mock(UserRepository.class);

    @Test
    public void testAddUser() {
        UUID id = UUID.randomUUID();
        LocalDateTime created_at = LocalDateTime.now();
        ZonedDateTime created_at_zoned =
                created_at.atZone(ZoneId.systemDefault())
                .withZoneSameInstant(ZoneId.of("Z"));

        User u = new User();

        u.setUsername("fname.lname@gmail.com");
        u.setFirst_name("fname");
        u.setLast_name("lname");
        u.setPassword("Fname@123");
        u.setAccount_updated(created_at_zoned);
        u.setAccount_created(created_at_zoned);

        userDao.save(u);

        Mockito.verify(userDao, Mockito.times(1)).save(u);
    }

}
