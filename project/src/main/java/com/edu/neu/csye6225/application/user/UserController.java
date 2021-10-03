package com.edu.neu.csye6225.application.user;

//import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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


    @GetMapping(path = "self")
    public ResponseEntity<Object> getUser(HttpServletRequest request){

        String[] user_credentials; // Array of Strings to store user credentials.

        String userHeader = request.getHeader("Authorization");

        if(userHeader.endsWith("Og==")) { // When No credentials are provided.
            return new ResponseEntity<Object>("No credentials sent",HttpStatus.BAD_REQUEST);
        }
        else if (userHeader!=null && userHeader.startsWith("Basic")) { // When Header is correct
            user_credentials = userService.getUserCredentials(userHeader);
        }
        else { // When authentication type is correct.
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        User user;
        if(!userService.checkIfUserExists(user_credentials[0])){ // When user does not exist in database.
            return new ResponseEntity<Object>("User dont Exists",HttpStatus.BAD_REQUEST);
        } else { // When correct user is getting requested.
            user = userService.getUserByUsername(user_credentials[0]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if(!encoder.matches(user_credentials[1],user.getPassword())){ // When password is not correct.
                return new ResponseEntity<Object>("Invalid Password",HttpStatus.BAD_REQUEST);
            } else { // When everything is correct.
                return new ResponseEntity<Object>(userService.createUserResponseBody(user), HttpStatus.CREATED);
            }
        }
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
