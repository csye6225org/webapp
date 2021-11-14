package com.edu.neu.csye6225.application.user;

//import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "v2/user")
public class UserController {

    UserService userService;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        logger.info("Controller createUser: Inside this controller");

        if (user.getFirst_name() == null ||
                user.getLast_name() == null ||
                user.getUsername() == null ||
                user.getPassword() == null
        ) {
            logger.error("Controller createUser: Firstname, Lastname, Username or Password is null in Request body.");
            return new ResponseEntity<Object>("Firstname, Lastname, Username and Password cannot be null",
                                                    HttpStatus.BAD_REQUEST);

        } else if (!EmailValidator.getInstance().isValid(user.getUsername())) {
            logger.error("Controller createUser: User did not enter a valid email in Request body.");
            return new ResponseEntity<Object>("Username is not a valid Email", HttpStatus.BAD_REQUEST);

        } else if (userService.checkIfUserExists(user.getUsername())) {
            logger.error("Controller createUser: This user already Exists.");
            return new ResponseEntity<Object>("User Already Exists",HttpStatus.BAD_REQUEST);

        } else {
            logger.info("Controller createUser: User entered valid data in Request body.");
            User response_user = userService.createUser(user);

            logger.info("Controller createUser: Creating Response body for user.");
            Map<String, String> userDetails = userService.userResponseBody(response_user);

            logger.info("Controller createUser: Returning response for createUser controller.");
            return new ResponseEntity<Object>(userDetails, HttpStatus.CREATED);
        }
    }


    @GetMapping(path = "self")
    public ResponseEntity<Object> getUser(HttpServletRequest request){

        logger.info("Controller getUser: Inside this controller");
        logger.info("Controller getUser: Authenticating request header for username and password");
        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Controller getUser: User Credentials are incorrect.");
            return header_authentication_result;
        } else {

            logger.info("Controller getUser: Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            logger.info("Controller getUser: Getting user by username.");
            User user = userService.getUserByUsername(username);

            logger.info("Controller getUser: Returning response for getUser controller.");
            logger.info("Controller getUser: "+user.toString());
            return new ResponseEntity<Object>(userService.userResponseBody(user), HttpStatus.OK);
        }

    }


    @PutMapping(path = "self")
    public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestBody User user) {

        logger.info("Controller updateUser: Inside this controller");

        logger.info("Controller updateUser: Authenticating request header for username and password");
        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);
        UUID uid = new UUID(0,0);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Controller updateUser: User Credentials are incorrect.");
            return header_authentication_result;

        } else if(!userService.checkIfUserExists(user.getUsername())){
            // When username in JSON of Request payload is changed
            logger.error("Controller updateUser: User tried to change username.");
            return new ResponseEntity<Object>(
                    "Username cannot be updated",
                    HttpStatus.BAD_REQUEST
            );

        } else if (    user.getFirst_name() == null ||
                        user.getLast_name() == null ||
                        user.getUsername() == null ||
                        user.getPassword() == null
        ) { // When any of the field is null
            logger.error("Controller updateUser: User kept Firstname, " +
                    "Lastname, Username or Password empty in request body.");
            return new ResponseEntity<Object>(
                    "Firstname, Lastname, Username and Password cannot be empty in JSON request body.",
                    HttpStatus.BAD_REQUEST);

        }
        else if
        (
             user.getAccount_updated()
                     .equals(ZonedDateTime.of(01,01,01,01,01,01,01, ZoneId.of("Z"))) &&
             user.getAccount_created()
                     .equals(ZonedDateTime.of(01,01,01,01,01,01,01, ZoneId.of("Z"))) &&
             user.getId().equals(uid)
        )
        { // When everything is correct.

            logger.info("Controller updateUser: User request body and header is correct.");
            String username = header_authentication_result.getBody().toString();

            if(!user.getUsername().equals(username)){
                logger.error("Controller updateUser: User trying to update other user's information");
                return new ResponseEntity<Object>(
                        "Username in body dont match with username in credentials.",
                        HttpStatus.BAD_REQUEST
                        );
            } else {
                logger.info("Controller updateUser: Updating user information.");
                userService.updateUser(user);

                logger.info("Controller updateUser: User information updated successfully.");
                logger.info("Controller updateUser: Returning updated user information.");
                return new ResponseEntity<Object>(
                        userService.userResponseBody(userService.getUserByUsername(username)),
                        HttpStatus.OK
                );
            }
        }
        else {
            logger.error("Controller updateUser: User tried to update fields " +
                    "other than Firstname, Lastname or Password.");
            return new ResponseEntity<Object>(
                    "You cannot update fields other than Firstname, Lastname and Password.",
                    HttpStatus.BAD_REQUEST
            );
        }

    }





}
