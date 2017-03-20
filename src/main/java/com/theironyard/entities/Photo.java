package com.theironyard.entities;


import org.springframework.boot.autoconfigure.domain.EntityScan;

import javax.persistence.*;
import java.time.LocalDateTime;

@EntityScan(basePackages = "pl.test.model")
@Entity
@Table(name = "photos")
public class Photo {

    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    int time;

    @Column
    String publicity;

    @Column
    LocalDateTime expiration;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    public Photo() {
    }


    public Photo(String filename, User sender, User receiver, int time, String publicity, LocalDateTime expiration) {

        this.filename = filename;
        this.sender = sender;
        this.receiver = receiver;
        this.time = time;
        this.publicity = publicity;
        this.expiration = expiration;


    }

    public LocalDateTime getExpiration() {
        return expiration;
    }

    public void setExpiration(LocalDateTime expiration) {
        this.expiration = expiration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getPublicity() {
        return publicity;
    }

    public void setPublicity(String publicity) {
        this.publicity = publicity;
    }

}
