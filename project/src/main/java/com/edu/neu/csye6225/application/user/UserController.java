package com.edu.neu.csye6225.application.user;

//import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

//            userDetails.put("id", response_user.getId().toString());
//            userDetails.put("firstName", response_user.getFirst_name());
//            userDetails.put("lastName", response_user.getLast_name());
//            userDetails.put("emailId", response_user.getUsername());
//            userDetails.put("account_created", response_user.getAccount_created().toString());
//            userDetails.put("account_updated", response_user.getAccount_updated().toString());

            return new ResponseEntity<Object>(userDetails, HttpStatus.CREATED);
        }
    }

    @GetMapping(path = "self")
//    public ResponseEntity<Object> getUser(HttpServletRequest request){
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
                return new ResponseEntity<Object>(userService.userResponseBody(user), HttpStatus.OK);
            }
        }
    }

    @PutMapping(path = "self")
    public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestBody User user){
        String[] user_credentials; // Array of Strings to store user credentials.
        System.out.println("updateUser: A");
        String userHeader = request.getHeader("Authorization");

        if(userHeader.endsWith("Og==")) { // When No credentials are provided.
            System.out.println("updateUser: B");
            return new ResponseEntity<Object>("No credentials sent",HttpStatus.BAD_REQUEST);
        }
        else if (userHeader!=null && userHeader.startsWith("Basic")) { // When Header is correct
            System.out.println("updateUser: C");
            user_credentials = userService.getUserCredentials(userHeader);
        }
        else { // When authentication type is correct.
            System.out.println("updateUser: D");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        System.out.println("updateUser: E");
        if(!userService.checkIfUserExists(user.getUsername())){ // When username in JSON of Request payload is changed
            System.out.println("updateUser: F");
            return new ResponseEntity<Object>("Username should not change in JSON of Request payload",
                                                HttpStatus.BAD_REQUEST
                                            );
        }

        User user_from_database;
        if(!userService.checkIfUserExists(user_credentials[0])){ // When user does not exist in database.
            System.out.println("updateUser: G");
            return new ResponseEntity<Object>("User dont Exists",HttpStatus.BAD_REQUEST);
        } else { // When correct user existing in database is getting requested for update.
            user_from_database = userService.getUserByUsername(user_credentials[0]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if(!encoder.matches(user_credentials[1],user_from_database.getPassword())){ // When password is not correct.
                System.out.println("updateUser: H");
                return new ResponseEntity<Object>("Invalid Password",HttpStatus.BAD_REQUEST);
            } else { // When everything is correct.
                System.out.println("updateUser: I");
                HttpStatus user_update_httpresponse = userService.updateUser(user);

                if(user_update_httpresponse.equals(HttpStatus.BAD_REQUEST)){
                    System.out.println("updateUser: J");
                    return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
                } else {
                    System.out.println("updateUser: K");
                    return new ResponseEntity<Object>(userService.userResponseBody(
                            userService.getUserByUsername(user_credentials[0])),
                            user_update_httpresponse
                    );
                }
            }
        }

    }

}
