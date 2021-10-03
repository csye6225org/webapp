package com.edu.neu.csye6225.application.user;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.*;

import java.time.LocalDateTime;
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

        user.setId(uuid);
        user.setAccount_created(created_at);
        user.setAccount_updated(created_at);

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
//        System.out.println("userService.updateUser: A");
//        if (    user.getFirst_name() == null ||
//                user.getLast_name() == null ||
//                user.getUsername() == null ||
//                user.getPassword() == null
//        ){
//            System.out.println("userService.updateUser: B");
//            return HttpStatus.BAD_REQUEST;
//        } else if(!user.getUsername().equals(getUserByUsername(user.getUsername()).getUsername())){
//            System.out.println("userService.updateUser: C");
//            return HttpStatus.BAD_REQUEST;
//        } else {
            User u = getUserByUsername(user.getUsername());

            System.out.println("1: "+u.toString());
            u.setFirst_name(user.getFirst_name());
            u.setLast_name(user.getLast_name());
//            u.setPassword(user.getPassword());
            u.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            u.setAccount_updated(LocalDateTime.now());

            System.out.println("2: "+u.toString());
            userRepository.save(u);

//            return true;
//        }
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

}
