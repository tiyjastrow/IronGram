package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by jeremypitt on 10/12/16.
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

    @Column
    int delay;

    @Column
    String isPublic;

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public Photo(User sender, User receiver, String filename, int delay, String isPublic) {

        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.delay = delay;
        this.isPublic = isPublic;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public Photo() {
    }

    public Photo(User sender, User receiver, String filename) {

        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
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
