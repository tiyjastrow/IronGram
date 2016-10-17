package com.theironyard.entities;

import javax.persistence.*;


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
    int secondsToDelete;

    @Column(nullable = false)
    boolean ispublic = false;

    public Photo() {
    }

    public Photo(User sender, User receiver, String filename, int secondsToDelete) {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.secondsToDelete = secondsToDelete;
    }

    public int getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getFilename() {
        return filename;
    }

    public int getSecondsToDelete() {
        return secondsToDelete;
    }

    public boolean ispublic() {
        return ispublic;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setSecondsToDelete(int secondsToDelete) {
        this.secondsToDelete = secondsToDelete;
    }

    public void setpublic(boolean aPublic) {
        ispublic = aPublic;
    }
}