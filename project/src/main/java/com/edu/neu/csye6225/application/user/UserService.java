package com.edu.neu.csye6225.application.user;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class UserService {

    UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private StatsDClient statsd = new NonBlockingStatsDClient("statsd", "localhost", 8125);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserService() {
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
    @Transactional(readOnly = true)
    public User getUserByUsername(String username){
        logger.info("Inside user service method getUserByUsername");
        logger.info("Getting user by username.");

        User user = new User();

        long start_time_get_all_users = System.currentTimeMillis();
        
        List<User> users = userRepository.findAll();

        long end_time_get_all_users = System.currentTimeMillis();
        long elapsedTime = end_time_get_all_users - start_time_get_all_users;
        statsd.recordExecutionTime("get_all_users_et", elapsedTime);

        logger.info("Finding user from users present in the database.");
        for(User u:users){
            if(u.getUsername().equals(username)){
                user = u;
            }
        }
        logger.info("Returning user retrieved from database.");
        return user;
    }

    /**
     * Add user to database
     * @param user
     * @return HttpStatus
     */
    public User createUser(User user) {
        logger.info("Inside user service method createUser");
        logger.info("Creating user information.");
        UUID uuid = UUID.randomUUID();
        LocalDateTime created_at = LocalDateTime.now();

        ZonedDateTime created_at_zoned = created_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

        user.setId(uuid);
        user.setAccount_created(created_at_zoned);
        user.setAccount_updated(created_at_zoned);

        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

        logger.info("Saving user information to database.");

        long start_time_save_user = System.currentTimeMillis();
        userRepository.save(user);
        long end_time_save_user = System.currentTimeMillis();
        long elapsedTime = end_time_save_user - start_time_save_user;
        statsd.recordExecutionTime("save_user_et", elapsedTime);

        return user;
    }

    /**
     * Update User information
     * @param user
     * @return HttpStatus
     */
    public void updateUser(User user){
        logger.info("Inside user service method updateUser");
        logger.info("Updating user information.");
        User u = getUserByUsername(user.getUsername());

        LocalDateTime updated_at = LocalDateTime.now();
        ZonedDateTime updated_at_zoned = updated_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

        System.out.println("1: "+u.toString());
        u.setFirst_name(user.getFirst_name());
        u.setLast_name(user.getLast_name());
        u.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        u.setAccount_updated(updated_at_zoned);

        logger.info("Saving updated user information to database.");
        userRepository.save(u);

    }


    public boolean checkIfUserExists(String username){
        logger.info("Inside user service method checkIfUserExists");
        if(username == null){
            logger.warn("Username cannot be null.");
            return false;
        }
        logger.info("Getting all users from database.");
        List<User> users = userRepository.findAll();
        logger.info("Going through all users to find the user.");
        for(User u:users){

            if(u.getUsername().equals(username)){
                logger.info("User is present in database.");
                return true;
            }
        }
        logger.warn("User is not present in database.");
        return false;
    }

    public String[] getUserCredentials(String userHeader){
        logger.info("Inside user service method getUserCredentials");
        logger.info("Decoding user Credentials from header");
        String[] userHeaderSplit = userHeader.split(" ");
        String decodedString;
        byte[] decodedBytes;
        decodedBytes = Base64.decodeBase64(userHeaderSplit[1]);
        decodedString = new String(decodedBytes);
        String[] userCredentials = decodedString.split(":");

        logger.info("Successfully decoded user credentials from header");
        logger.info("Returning user credentials decoded from header");

        return userCredentials;
    }

    public Map<String, String> userResponseBody(User user){
        logger.info("Inside user service method userResponseBody");
        logger.info("Creating user response body.");
        Map<String, String> userDetails = new HashMap<>();

        userDetails.put("id", user.getId().toString());
        userDetails.put("firstName", user.getFirst_name());
        userDetails.put("lastName", user.getLast_name());
        userDetails.put("emailId", user.getUsername());
        userDetails.put("account_created", user.getAccount_created().toString());
        userDetails.put("account_updated", user.getAccount_updated().toString());

        logger.info("User response body successfully generated.");
        logger.info("Returning user response body.");
        return userDetails;
    }

    public ResponseEntity<Object> authenticateHeader(HttpServletRequest request){
        logger.info("Inside user service method authenticateHeader");
        logger.info("Authenticating request header.");
        String[] user_credentials; // Array of Strings to store user credentials.
        logger.info("Getting Authorization part of request header.");
        String userHeader = request.getHeader("Authorization");

        if(userHeader.endsWith("Og==")) { // When No credentials are provided.
            logger.warn("No credentials were sent in request.");
            return new ResponseEntity<Object>("No credentials sent",HttpStatus.BAD_REQUEST);
        }
        else if (userHeader!=null && userHeader.startsWith("Basic")) { // When Header is correct
            logger.info("Header is correct. Sending it to retrieve credentials from it.");
            user_credentials = getUserCredentials(userHeader);
        }
        else { // When authentication type is correct.
            logger.warn("Header is not correct.");
            return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
        }

        User user_from_database;
        if(!checkIfUserExists(user_credentials[0])){ // When user does not exist in database.
            logger.info("Checking if user exists in database.");
            return new ResponseEntity<Object>("User dont Exists",HttpStatus.BAD_REQUEST);
        } else { // When correct user existing in database is getting requested for update or get.
            logger.info("Get user by username from database.");
            user_from_database = getUserByUsername(user_credentials[0]);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (    !encoder.matches(user_credentials[1],
                    user_from_database.getPassword())) { // When password is not correct.
                logger.warn("Invalid password is entered for authentication.");
                return new ResponseEntity<Object>("Invalid Password", HttpStatus.BAD_REQUEST);
            } else { // When everything is correct.
                logger.warn("User credentials authenticated successfully.");
                return new ResponseEntity<Object>(user_from_database.getUsername(), HttpStatus.OK);
            }
        }
    }

}
