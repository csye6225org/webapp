package com.edu.neu.csye6225.application.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

//    // JPQL
//    @Query("SELECT u FROM User u where u.id = ?1")
//    Optional<User> findUserById(UUID id);

    @Query("SELECT u.username FROM User u where u.username = ?1")
    User findUserByUsername(String username);

    @Query("SELECT u.username FROM User u where u.username = ?1")
    Boolean userExists(String username);



}
