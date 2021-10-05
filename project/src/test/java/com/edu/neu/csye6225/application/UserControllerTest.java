package com.edu.neu.csye6225.application;

import com.edu.neu.csye6225.application.user.User;
import com.edu.neu.csye6225.application.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
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
        User u = new User();

        u.setUsername("fname.lname@gmail.com");
        u.setFirst_name("fname");
        u.setLast_name("lname");
        u.setPassword("Fname@123");
        u.setAccount_updated(created_at);
        u.setAccount_created(created_at);

        userDao.save(u);

        Mockito.verify(userDao, Mockito.times(1)).save(u);
    }

}
