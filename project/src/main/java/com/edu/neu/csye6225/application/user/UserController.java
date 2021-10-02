package com.edu.neu.csye6225.application.user;

//import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping(path = "v1/user")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
    @GetMapping(path = "self/")
    public ResponseEntity<Object> getUser(HttpServletRequest request){
        //to store user credentials
        String[] user_credentials = new String[2];
        String username;
        String password;

        //variables to store update values from user
        String userHeader = request.getHeader("Authorization");
        boolean validated;

        //no credentials provided
        if(userHeader.endsWith("Og==")) {
            return new ResponseEntity<Object>("No Credentials sent",HttpStatus.BAD_REQUEST);
        }
        else if (userHeader!=null && userHeader.startsWith("Basic")) {
            user_credentials = userService.getUserCredentials(userHeader);
        }
        else {
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

//        if(validated){
//            userService.
//        }
        if(userService.checkIfUserExists())

        User u = userService.getUserByUsername(username);
        if(u.getFirst_name() == "User Not Found"){
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(u, HttpStatus.ACCEPTED);
        }
    }

     **/

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
        } else if (!userService.checkIfUserExists(user.getUsername())) {
            System.out.println("C");
            return new ResponseEntity<Object>("User Already Exists",HttpStatus.BAD_REQUEST);
        } else {

            User response_user = userService.createUser(user);

            HashMap<String, String> userDetails = new HashMap<>();

            userDetails.put("id", response_user.getId().toString());
            userDetails.put("firstName", response_user.getFirst_name());
            userDetails.put("lastName", response_user.getLast_name());
            userDetails.put("emailId", response_user.getUsername());
            userDetails.put("account_created", response_user.getAccount_created().toString());
            userDetails.put("account_updated", response_user.getAccount_updated().toString());

            return new ResponseEntity<Object>(userDetails, HttpStatus.CREATED);
        }
    }


}
