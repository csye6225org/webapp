package com.edu.neu.csye6225.application.user;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.sns.model.PublishRequest;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.*;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

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
    private AmazonSNSClient amazonSNSClient;

//    @Autowired
    private AmazonDynamoDbClient amazonDynamoDbClient;

    @Value("${amazonProperties.snsTopicArn}")
    private String snsTopicArnValue;
//
//    @Value("${amazonDynamodb.tableName}")
//    String dynamodb_tablename;

    @Autowired
    public UserService(UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    public UserService() {
    }


    public void saveUser(User u){
        userRepository.save(u);
    }

    /**
     * Add user to database
     * @param user
     * @return HttpStatus
     */
    public User createUser(User user) throws JSONException {
        logger.info("Inside user service method createUser");
        logger.info("Creating user information.");
        UUID uuid = UUID.randomUUID();
        LocalDateTime created_at = LocalDateTime.now();

        ZonedDateTime created_at_zoned = created_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

        user.setId(uuid);
        user.setAccount_created(created_at_zoned);
        user.setAccount_updated(created_at_zoned);
        user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        user.setVerified(false);

        logger.info("Saving user information to database.");

        long start_time_save_user = System.currentTimeMillis();
        saveUser(user);
        long end_time_save_user = System.currentTimeMillis();
        long elapsedTime = end_time_save_user - start_time_save_user;
        statsd.recordExecutionTime("save_user_et", elapsedTime);

        publishSNSTopic(user);

        return user;
    }

    public void publishSNSTopic(User user) throws JSONException {

        UUID user_verification_token = UUID.randomUUID();

        StringBuilder account_verification_link = new StringBuilder();
        account_verification_link
                .append("http://prod.varaddesai.me/v1/verifyUserEmail?email=")
                .append(user.getUsername())
                .append("&token=")
                .append(user_verification_token);

        StringBuilder message = new StringBuilder();
        message.append("Hello ")
                .append(user.getFirst_name())
                .append(" ")
                .append(user.getLast_name())
                .append(",");
        message.append("<br><br>");
        message.append("Please verify your account using the following link:").append(" <br>");
        message.append(account_verification_link).append("<br><br>");
        message.append("Thank you and Best Regards,").append("<br>");
        message.append("Webapp Csye6225");


        String jsonString = new JSONObject()
                .put("username", user.getUsername())
                .put("email_body", message.toString())
                .put("token_uuid", user_verification_token.toString())
                .toString();

        System.out.println(jsonString);

        PublishRequest publishRequest =
                new PublishRequest(
                        snsTopicArnValue,
                        jsonString,
                        "PublishRequest"
                );

        amazonSNSClient.generateSNSClient().publish(publishRequest);
    }

    /**
     * Update User information
     * @param u_from_db
     * @param new_u
     * @return HttpStatus
     */
    public void updateUser(User u_from_db, User new_u){
        logger.info("Inside user service method updateUser");
        logger.info("Updating user information.");
//        User u = getUserByUsername(user.getUsername());

        LocalDateTime updated_at = LocalDateTime.now();
        ZonedDateTime updated_at_zoned = updated_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

//        System.out.println("1: "+u.toString());
        u_from_db.setFirst_name(new_u.getFirst_name());
        u_from_db.setLast_name(new_u.getLast_name());
        u_from_db.setPassword(BCrypt.hashpw(new_u.getPassword(), BCrypt.gensalt()));
        u_from_db.setAccount_updated(updated_at_zoned);

        logger.info("Saving updated user information to database.");
        saveUser(u_from_db);
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
        userDetails.put("verified", user.getVerified().toString());
        userDetails.put("verified_on",user.getVerified_on().toString());

        logger.info("User response body successfully generated.");
        logger.info("Returning user response body.");
        return userDetails;
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

    public List<User> getUsers(){
        return userRepository.findAll();
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
        logger.info("Inside user service method getUserByUsername");
        logger.info("Getting user by username.");

        User user = new User();

        long start_time_get_all_users = System.currentTimeMillis();

        List<User> users = getUsers();

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

    public boolean checkIfUserExists(String username){
        logger.info("Inside user service method checkIfUserExists");
        if(username == null){
            logger.warn("Username cannot be null.");
            return false;
        }
        logger.info("Getting all users from database.");
        List<User> users = getUsers();
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

    public ResponseEntity<Object> authenticateHeader(HttpServletRequest request){
        logger.info("Inside user service method authenticateHeader");
        logger.info("Authenticating request header.");
        String[] user_credentials; // Array of Strings to store user credentials.
        logger.info("Getting Authorization part of request header.");
        String userHeader = request.getHeader("Authorization");

        if(userHeader.endsWith("Og==")) { // When No credentials are provided.
            logger.warn("No credentials were sent in request.");
            return new ResponseEntity<Object>("No credentials sent", HttpStatus.BAD_REQUEST);
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

    public boolean checkIfTtlHasPassed(String token){

        logger.info("token = "+token);

        Item item = amazonDynamoDbClient.get_item(token);
        logger.info("this is item from dynamodb ->"+item.toJSON());

        return true;
    }

    public boolean checkIfUserIsVerified(String username){
        logger.info("Checking if user is verified");
        User user = getUserByUsername(username);
        logger.info("User verification status"+user.getVerified());
        return user.getVerified();
    }

    public boolean verifyUser(String username, String token){

        logger.info("Inside verifyUser");

        checkIfTtlHasPassed(token);

        User user = this.getUserByUsername(username);

        LocalDateTime verified_at = LocalDateTime.now();
        ZonedDateTime verified_at_zoned = verified_at.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("Z"));

        user.setVerified(true);
        user.setVerified_on(verified_at_zoned);
        user.setAccount_updated(verified_at_zoned);

        logger.info(user.toString());

        saveUser(user);

        return true;
    }

}
