package com.edu.neu.csye6225.application.user;

import com.amazonaws.services.sns.model.PublishRequest;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
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
    private AmazonSNSClient amazonSNSClient;

    @Value("${amazonProperties.snsTopicArn}")
    private String snsTopicArnValue;

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

    public void publishSNSTopic(User user){

        UUID user_verification_token = UUID.randomUUID();

        StringBuilder account_verification_link = new StringBuilder();
        account_verification_link.append("http://prod.varaddesai.me/v1/verifyUserEmail?email=")
                .append(user.getUsername())
                .append("&token=")
                .append(user_verification_token);

        StringBuilder message = new StringBuilder();
        message.append("Hello ")
                .append(user.getFirst_name())
                .append(" ")
                .append(user.getLast_name())
                .append(",");
        message.append("\n");
        message.append("Please verify your account using the following link:").append(" \n");
        message.append(account_verification_link).append("\n");
        message.append("Thank you and Best Regards,").append("\n");
        message.append("CSYE6225 Webapp");

        PublishRequest publishRequest =
                new PublishRequest(snsTopicArnValue,
                        message.toString(),
                        "Please Verify Your Account");

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






}
