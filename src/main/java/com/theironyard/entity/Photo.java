package com.theironyard.entity;
import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    @Column(nullable = true)
    String filename;

    @Column
    boolean shared;

    @Column(nullable = false)
    int seconds;

    public Photo(User sender, User receiver, String filename, boolean shared, int seconds) {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.shared = shared;
        this.seconds = seconds;
    }

    public Photo() {
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public boolean isShared() {
        return shared;
    }
    public boolean getShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
}
