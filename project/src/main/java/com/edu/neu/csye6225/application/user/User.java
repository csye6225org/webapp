package com.edu.neu.csye6225.application.user;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "User")
@Table( name = "\"user\"",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "user_email_unique",
                    columnNames = "username"
                )
        }
    )
public class User {

    @Id
    private UUID id;

    @Column(
            name = "first_name",
            columnDefinition = "TEXT"
    )
    private String first_name;

    @Column(
            name = "last_name",
            columnDefinition = "TEXT"
    )
    private String last_name;

    @Column(
            name = "username",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String password;

    @Column(
            name = "account_created",
            nullable = false,
            columnDefinition = "DATE",
            updatable = false
    )
    private LocalDateTime account_created;

    @Column(
            name = "account_updated",
            columnDefinition = "DATETIME"
    )
    private LocalDateTime account_updated;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getAccount_created() {
        return account_created;
    }

    public void setAccount_created(LocalDateTime account_created) {
        this.account_created = account_created;
    }

    public LocalDateTime getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(LocalDateTime account_updated) {
        this.account_updated = account_updated;
    }

    public User() {
    }

    public User(String first_name, String last_name, String username, String password) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.password = password;
    }

    public User(String first_name) {
        this.first_name = first_name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", username='" + username + '\'' +
                ", account_created=" + account_created +
                ", account_updated=" + account_updated +
                '}';
    }
}
