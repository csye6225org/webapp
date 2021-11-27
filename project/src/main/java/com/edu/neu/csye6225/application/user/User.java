package com.edu.neu.csye6225.application.user;

import javax.persistence.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
            columnDefinition = "TIMESTAMP",
            updatable = false
    )
    private ZonedDateTime account_created;

    @Column(
            name = "account_updated",
            columnDefinition = "TIMESTAMP"
    )
    private ZonedDateTime account_updated;

    @Column(
            name = "verified"
    )
    private boolean verified;

    @Column(
            name = "verified_on",
            columnDefinition = "TIMESTAMP"
    )
    private ZonedDateTime verified_on;

    public UUID getId() {
        UUID uid = new UUID(0,0);
        if (id == null) return uid;
        else return id;
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

    public ZonedDateTime getAccount_created() {
        ZonedDateTime zdt = ZonedDateTime.of(01,01,01,01,01,01,01,ZoneId.of("Z"));
        if(account_created == null) return zdt;
        else return account_created.withZoneSameInstant(ZoneId.of("Z"));
    }

    public void setAccount_created(ZonedDateTime account_created) {
        this.account_created = account_created;
    }

    public ZonedDateTime getAccount_updated() {
        ZonedDateTime zdt = ZonedDateTime.of(01,01,01,01,01,01,01,ZoneId.of("Z"));
        if(account_updated == null) return zdt;
        else return account_updated.withZoneSameInstant(ZoneId.of("Z"));
    }

    public void setAccount_updated(ZonedDateTime account_updated) {
        this.account_updated = account_updated;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public ZonedDateTime getVerified_on() {
        ZonedDateTime zdt = ZonedDateTime.of(01,01,01,01,01,01,01,ZoneId.of("Z"));
        if(verified_on == null) return zdt;
        else return verified_on.withZoneSameInstant(ZoneId.of("Z"));
    }

    public void setVerified_on(ZonedDateTime verified_on) {
        this.verified_on = verified_on;
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
                "id=" + this.getId().toString() +
                ", first_name='" + this.getFirst_name() + '\'' +
                ", last_name='" + this.getLast_name() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", account_created=" + this.getAccount_created() +
                ", account_updated=" + this.getAccount_updated() +
                ", verified=" + this.getVerified() +
                ", verified_on=" + this.getVerified_on() +
                '}';
    }
}
