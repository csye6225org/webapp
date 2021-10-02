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
     * Get User by Id
     * @param username
     * @return User
     */
    public User getUserByUsername(String username){
        if(!userRepository.userExists(username)){
            return new User("User Not Found");
        }
        return userRepository.findUserByUsername(username);
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
    public HttpStatus updateUser(User user){
        if (    user.getFirst_name() == null &&
                user.getLast_name() == null &&
                user.getUsername() == null &&
                user.getPassword() == null
        ){
            return HttpStatus.NO_CONTENT;
        } else if(user.getUsername() != userRepository.findUserByUsername(user.getUsername()).getUsername()){
            return HttpStatus.BAD_REQUEST;
        } else {
            User u = userRepository.findUserByUsername(user.getUsername());

            u.setFirst_name(user.getFirst_name());
            u.setLast_name(user.getLast_name());
            u.setPassword(user.getPassword());
            u.setAccount_updated(LocalDateTime.now());

            userRepository.save(u);

            return HttpStatus.OK;
        }
    }

    public boolean emailStringValidator(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    public boolean checkIfUserExists(String username){
        List<User> users = new ArrayList<>();
        for(User u:users){
            if(u.getUsername() == username){
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
        return userCredentials;
    }

    public String[] validateUserCredentials_1(String user_header){
        Map<String, HttpStatus> result = new HashMap<>();

        String[] userHeaderSplit = user_header.split(" ");

        byte[] decodedBytes = Base64.decodeBase64(userHeaderSplit[1]);
        String decodedString = new String(decodedBytes);
        String[] userCredentials = decodedString.split(":");

        String username = userCredentials[0];
        String password = userCredentials[1];

//        User u = userRepository.findUserByUsername(username);
//
//        if(u.equals(null)){
//            return false;
//        } else if(u.getPassword() != password){
//            return false;
//        } else {
//            return true;
//        }
            return userCredentials;
    }

}
