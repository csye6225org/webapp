package com.edu.neu.csye6225.application.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u.username FROM User u where u.username = ?1")
    User findUserByUsername(String username);

    @Query("SELECT u.username FROM User u where u.username = ?1")
    Boolean userExists(String username);

}
