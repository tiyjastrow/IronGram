package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by jakefroeb on 10/12/16.
 */
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

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    int secondsUntilDelete;

    @Column(nullable = false)
    boolean pub;

    public Photo() {
    }

    public Photo(User sender, User receiver, String filename, int secondsUntilDelete, boolean pub) {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.secondsUntilDelete = secondsUntilDelete;
        this.pub = pub;
    }

    public boolean isPub() {
        return pub;
    }

    public void setPub(boolean pub) {
        this.pub = pub;
    }

    public int getSecondsUntilDelete() {
        return secondsUntilDelete;
    }

    public void setSecondsUntilDelete(int secondsUntilDelete) {
        this.secondsUntilDelete = secondsUntilDelete;
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

    public void setReceiver(User reciever) {
        this.receiver = reciever;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
