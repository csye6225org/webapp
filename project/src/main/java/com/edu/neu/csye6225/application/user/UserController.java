package com.edu.neu.csye6225.application.user;

//import org.omg.CORBA.OBJECT_NOT_EXIST;
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
@RequestMapping(path = "v1/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        if (user.getFirst_name() == null ||
                user.getLast_name() == null ||
                user.getUsername() == null ||
                user.getPassword() == null
        ) {
            System.out.println("A");
            return new ResponseEntity<Object>("Firstname, Lastname, Username and Password cannot be null",
                                                    HttpStatus.BAD_REQUEST);
        } else if (!EmailValidator.getInstance().isValid(user.getUsername())) {
            System.out.println("B");
            return new ResponseEntity<Object>("Username is not a valid Email", HttpStatus.BAD_REQUEST);
        } else if (userService.checkIfUserExists(user.getUsername())) {
            System.out.println("C");
            return new ResponseEntity<Object>("User Already Exists",HttpStatus.BAD_REQUEST);
        } else {

            User response_user = userService.createUser(user);

            Map<String, String> userDetails = userService.userResponseBody(response_user);

            return new ResponseEntity<Object>(userDetails, HttpStatus.CREATED);
        }
    }


    @GetMapping(path = "self")
    public ResponseEntity<Object> getUser(HttpServletRequest request){

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);

        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            return header_authentication_result;
        } else {
            String username = header_authentication_result.getBody().toString();
            User user = userService.getUserByUsername(username);
            return new ResponseEntity<Object>(userService.userResponseBody(user), HttpStatus.OK);
        }

    }


    @PutMapping(path = "self")
    public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestBody User user) {

        ResponseEntity<Object> header_authentication_result = userService.authenticateHeader(request);
        UUID uid = new UUID(0,0);
        System.out.println("uid = "+uid.toString());
        System.out.println("date created = "+user.getAccount_updated().toString());
        System.out.println("date updated = "+user.getAccount_created().toString());


        if(header_authentication_result.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
            return header_authentication_result;
        } else if(!userService.checkIfUserExists(user.getUsername())){
            // When username in JSON of Request payload is changed
            return new ResponseEntity<Object>(
                    "Username cannot be updated",
                    HttpStatus.BAD_REQUEST
            );
        } else if (    user.getFirst_name() == null ||
                        user.getLast_name() == null ||
                        user.getUsername() == null ||
                        user.getPassword() == null
        ) { // When any of the field is null
            return new ResponseEntity<Object>(
                    "Firstname, Lastname, Username and Password cannot be empty in JSON request body.",
                    HttpStatus.BAD_REQUEST);
        }
        else if
        (
             user.getAccount_updated().equals(ZonedDateTime.of(01,01,01,01,01,01,01, ZoneId.of("Z"))) &&
             user.getAccount_created().equals(ZonedDateTime.of(01,01,01,01,01,01,01, ZoneId.of("Z"))) &&
             user.getId().equals(uid)
        )
        { // When everything is correct.

            String username = header_authentication_result.getBody().toString();

            if(!user.getUsername().equals(username)){
                return new ResponseEntity<Object>(
                        "Username in body dont match with username in credentials.",
                        HttpStatus.BAD_REQUEST
                        );
            } else {
                userService.updateUser(user);

                return new ResponseEntity<Object>(
                        userService.userResponseBody(userService.getUserByUsername(username)),
                        HttpStatus.OK
                );
            }
        }
        else {
            return new ResponseEntity<Object>(
                    "You cannot update fields other than Firstname, Lastname and Password.",
                    HttpStatus.BAD_REQUEST
            );
        }

    }





}
