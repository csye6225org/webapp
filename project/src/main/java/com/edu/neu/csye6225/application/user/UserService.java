package com.edu.neu.csye6225.application.user;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.*;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserService {

    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get User by username.
     * This method will search through all users present in database
     * and will return the user object whose username matches the
     * username parameter sent to this method.
     * Time complexity is O(n)
     * @param username
     * @return User
     */
    public User getUserByUsername(String username){
        User user = new User();
        List<User> users = userRepository.findAll();
        for(User u:users){
            if(u.getUsername().equals(username)){
                user = u;
            }
        }
        return user;
    }

    /**
     * Add user to database
     * @param user
     * @return HttpStatus
     */
    public User createUser(User user) {


        UUID uuid = UUID.randomUUID();
        LocalDateTime created_at = LocalDateTime.now();

        ZonedDateTime created_at_zoned = created_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));
//		ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("Z"));
        System.out.println(created_at_zoned);

        user.setId(uuid);
        user.setAccount_created(created_at_zoned);
        user.setAccount_updated(created_at_zoned);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        userRepository.save(user);
        return user;

    }

    /**
     * Update User information
     * @param user
     * @return HttpStatus
     */
    public void updateUser(User user){

            User u = getUserByUsername(user.getUsername());

        LocalDateTime updated_at = LocalDateTime.now();
        ZonedDateTime updated_at_zoned = updated_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

            System.out.println("1: "+u.toString());
            u.setFirst_name(user.getFirst_name());
            u.setLast_name(user.getLast_name());
            u.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            u.setAccount_updated(updated_at_zoned);

            System.out.println("2: "+u.toString());
            userRepository.save(u);

    }


    public boolean checkIfUserExists(String username){
        if(username == null){
            return false;
        }
        System.out.println("checkIfUserExists:username"+username);
        List<User> users = userRepository.findAll();
        for(User u:users){
            System.out.println(u.getUsername());
            if(u.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public String[] getUserCredentials(String userHeader){

        String[] userHeaderSplit = userHeader.split(" ");
        String decodedString;
        byte[] decodedBytes;
        decodedBytes = Base64.decodeBase64(userHeaderSplit[1]);
        decodedString = new String(decodedBytes);
        String[] userCredentials = decodedString.split(":");

        System.out.println("UserService.getUserCredentials: userCredentials"
                +userCredentials[0]
                +"->"
                +userCredentials[1]);
        return userCredentials;
    }

    public Map<String, String> userResponseBody(User user){

        Map<String, String> userDetails = new HashMap<>();

        userDetails.put("id", user.getId().toString());
        userDetails.put("firstName", user.getFirst_name());
        userDetails.put("lastName", user.getLast_name());
        userDetails.put("emailId", user.getUsername());
        userDetails.put("account_created", user.getAccount_created().toString());
        userDetails.put("account_updated", user.getAccount_updated().toString());

        return userDetails;
    }

    public ResponseEntity<Object> authenticateHeader(HttpServletRequest request){

        String[] user_credentials; // Array of Strings to store user credentials.
        String userHeader = request.getHeader("Authorization");

        if(userHeader.endsWith("Og==")) { // When No credentials are provided.
            return new ResponseEntity<Object>("No credentials sent",HttpStatus.BAD_REQUEST);
        }
        else if (userHeader!=null && userHeader.startsWith("Basic")) { // When Header is correct
            user_credentials = getUserCredentials(userHeader);
        }
        else { // When authentication type is correct.
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        User user_from_database;
        if(!checkIfUserExists(user_credentials[0])){ // When user does not exist in database.
            return new ResponseEntity<Object>("User dont Exists",HttpStatus.BAD_REQUEST);
        } else { // When correct user existing in database is getting requested for update or get.
            user_from_database = getUserByUsername(user_credentials[0]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (    !encoder.matches(user_credentials[1],
                    user_from_database.getPassword())) { // When password is not correct.
                return new ResponseEntity<Object>("Invalid Password", HttpStatus.BAD_REQUEST);
            } else { // When everything is correct.
                return new ResponseEntity<Object>(user_from_database.getUsername(), HttpStatus.OK);
            }
        }
    }

}
