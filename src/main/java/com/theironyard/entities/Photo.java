package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int Id;

    @Column(nullable = false)
    String filename;

    @Column
    int seconds = 10;

    @Column
    LocalDateTime timeStamp;

    @Column
    Boolean privacy;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    public Photo(String filename, User sender, User receiver, LocalDateTime timeStamp, Boolean privacy) {
        this.filename = filename;
        this.sender = sender;
        this.receiver = receiver;
        this.privacy = privacy;
        this.timeStamp = timeStamp;
        this.seconds = seconds;
    }

    public Photo() {
    }

    public void setPrivacy(Boolean privacy) {
        this.privacy = privacy;
    }

    public Boolean getPrivacy(){return privacy;}

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return Id;
    }


    public void setId(int id) {
        Id = id;
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
}
