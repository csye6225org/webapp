package com.edu.neu.csye6225.application.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserReadOnlyService {

    UserRepository userRepository;

    public UserReadOnlyService() {
    }

    @Autowired
    public UserReadOnlyService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
