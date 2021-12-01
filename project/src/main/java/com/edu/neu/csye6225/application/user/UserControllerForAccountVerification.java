package com.edu.neu.csye6225.application.user;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "v1/verifyUserEmail")
@Transactional
public class UserControllerForAccountVerification {

    UserService userService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private StatsDClient statsd = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    @Autowired
    public UserControllerForAccountVerification(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Object> verifyUser(
            @RequestParam("email") String username,
            @RequestParam("token") String token_string) {

        logger.info("Inside verify user controller");

//        boolean has_ttl_passed = userService.checkIfTtlHasPassed(token_string);
        logger.info("username from email ="+username);
        logger.info("token from verification link="+token_string);

        userService.verifyUser(username, token_string);

        return new ResponseEntity<Object>(
                "token = "+token_string+" username = "+username,
                HttpStatus.OK
        );
    }
}
