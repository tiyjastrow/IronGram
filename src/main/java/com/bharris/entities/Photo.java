package com.bharris.entities;

import javax.persistence.*;

/**
 * Created by BHarris on 3/15/17.
 */
@Entity
@Table(name="photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    Integer time = 10;

    @Column(nullable = false)
    boolean isPublic = false;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    public Photo(String filename, User sender, User receiver) {
        this.filename = filename;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Photo(String filename, Integer time, User sender, User receiver) {
        this.filename = filename;
        this.time = time;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Photo(String filename, Integer time, boolean isPublic, User sender, User receiver) {
        this.filename = filename;
        this.time = time;
        this.isPublic = isPublic;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Photo(String filename, boolean isPublic, User sender, User receiver) {
        this.filename = filename;
        this.isPublic = isPublic;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public Photo() {
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

    public void decreaseCount(){
        this.time--;
    }
}
