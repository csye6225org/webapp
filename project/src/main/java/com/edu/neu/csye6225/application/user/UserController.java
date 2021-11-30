package com.edu.neu.csye6225.application.user;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(path = "v1/user")
@Transactional
public class UserController {

    UserService userService;

    Logger logger = LoggerFactory.getLogger(UserController.class);

    private StatsDClient statsd = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody User user) throws JSONException {
        long start_createUser_controller = System.currentTimeMillis();
        logger.info("Inside Controller createUser");
        statsd.incrementCounter("createUserController");
        statsd.incrementCounter("apiCall");


        if (user.getFirst_name() == null ||
                user.getLast_name() == null ||
                user.getUsername() == null ||
                user.getPassword() == null
        ) {
            logger.error("Controller createUser: Firstname, Lastname, Username or Password is null in Request body.");
            long end_createUser_controller = System.currentTimeMillis();
            long elapsedTime = end_createUser_controller - start_createUser_controller;
            statsd.recordExecutionTime("createUser_controller_et", elapsedTime);
            return new ResponseEntity<Object>("Firstname, Lastname, Username and Password cannot be null",
                                                    HttpStatus.BAD_REQUEST);

        } else if (!EmailValidator.getInstance().isValid(user.getUsername())) {
            logger.error("Controller createUser: User did not enter a valid email in Request body.");
            long end_createUser_controller = System.currentTimeMillis();
            long elapsedTime = end_createUser_controller - start_createUser_controller;
            statsd.recordExecutionTime("createUser_controller_et", elapsedTime);
            return new ResponseEntity<Object>("Username is not a valid Email", HttpStatus.BAD_REQUEST);

        } else if (userService.checkIfUserExists(user.getUsername())) {
            logger.error("This user already Exists.");
            long end_createUser_controller = System.currentTimeMillis();
            long elapsedTime = end_createUser_controller - start_createUser_controller;
            statsd.recordExecutionTime("createUser_controller_et", elapsedTime);
            return new ResponseEntity<Object>("User Already Exists",HttpStatus.BAD_REQUEST);

        } else {
            logger.info("User entered valid data in Request body.");
            User response_user = userService.createUser(user);
            logger.info("Created this user: "+response_user.toString());

            logger.info("Creating Response body for user.");
            Map<String, String> userDetails = userService.userResponseBody(response_user);

            logger.info("Returning response for createUser controller.");
            long end_createUser_controller = System.currentTimeMillis();
            long elapsedTime = end_createUser_controller - start_createUser_controller;
            statsd.recordExecutionTime("createUser_controller_et", elapsedTime);
            return new ResponseEntity<Object>(userDetails, HttpStatus.CREATED);
        }
    }


    @GetMapping(path = "self")
    @Transactional(readOnly = true)
    public ResponseEntity<Object> getUser(HttpServletRequest request){
        long start_getUser_controller = System.currentTimeMillis();
        logger.info("Inside Controller getUser");
        logger.info("Authenticating request header for username and password");
        statsd.incrementCounter("getUserController");
        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){

            logger.error("User Credentials are incorrect.");
            long end_getUser_controller = System.currentTimeMillis();
            long elapsedTime = end_getUser_controller - start_getUser_controller;
            statsd.recordExecutionTime("getUser_controller_et", elapsedTime);

            return header_authentication_result;
        } else {

            logger.info("Authenticated header to be correct.");
            String username = header_authentication_result.getBody().toString();

            logger.info("Getting user by username.");
            User user = userService.getUserByUsername(username);

            logger.info("Returning response for getUser controller.");
            logger.info("Returning user information: "+user.toString());
            long end_getUser_controller = System.currentTimeMillis();
            long elapsedTime = end_getUser_controller - start_getUser_controller;
            statsd.recordExecutionTime("getUser_controller_et", elapsedTime);

            return new ResponseEntity<Object>(userService.userResponseBody(user), HttpStatus.OK);
        }

    }


    @PutMapping(path = "self")
    public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestBody User user) {

        long start_updateUser_controller = System.currentTimeMillis();
        logger.info("Inside Controller updateUser");
        logger.info("Controller updateUser: Authenticating request header for username and password");
        statsd.incrementCounter("updateUserController");
        statsd.incrementCounter("apiCall");

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);
        UUID uid = new UUID(0,0);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            logger.error("Controller updateUser: User Credentials are incorrect.");
            long end_updateUser_controller = System.currentTimeMillis();
            long elapsedTime = end_updateUser_controller - start_updateUser_controller;
            statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
            return header_authentication_result;

        } else if(!userService.checkIfUserExists(user.getUsername())){
            // When username in JSON of Request payload is changed
            logger.error("Controller updateUser: User tried to change username.");
            long end_updateUser_controller = System.currentTimeMillis();
            long elapsedTime = end_updateUser_controller - start_updateUser_controller;
            statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
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
            long end_updateUser_controller = System.currentTimeMillis();
            long elapsedTime = end_updateUser_controller - start_updateUser_controller;
            statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
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
                long end_updateUser_controller = System.currentTimeMillis();
                long elapsedTime = end_updateUser_controller - start_updateUser_controller;
                statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
                return new ResponseEntity<Object>(
                        "Username in body dont match with username in credentials.",
                        HttpStatus.BAD_REQUEST
                        );
            } else {
                logger.info("Controller updateUser: Updating user information.");

                User u = userService.getUserByUsername(user.getUsername());


                userService.updateUser(u, user);

                logger.info("Controller updateUser: User information updated successfully.");
                logger.info("Controller updateUser: Returning updated user information.");
                long end_updateUser_controller = System.currentTimeMillis();
                long elapsedTime = end_updateUser_controller - start_updateUser_controller;
                statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
                return new ResponseEntity<Object>(
                        userService.userResponseBody(userService.getUserByUsername(username)),
                        HttpStatus.OK
                );
            }
        }
        else {
            logger.error("Controller updateUser: User tried to update fields " +
                    "other than Firstname, Lastname or Password.");
            long end_updateUser_controller = System.currentTimeMillis();
            long elapsedTime = end_updateUser_controller - start_updateUser_controller;
            statsd.recordExecutionTime("updateUser_controller_et", elapsedTime);
            return new ResponseEntity<Object>(
                    "You cannot update fields other than Firstname, Lastname and Password.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}