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

        logger.info("username from email ="+username);
        logger.info("token from verification link="+token_string);


        boolean verification_completed = userService.verifyUser(username, token_string);

        if(!verification_completed) {
            return new ResponseEntity<Object>("User cannot be verified", HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<Object>(
                    "token = "+token_string+" username = "+username,
                    HttpStatus.OK
            );
        }
    }
}
