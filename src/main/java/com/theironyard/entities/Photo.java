package com.theironyard.entities;

import javax.persistence.*;

/**
 * Created by joe on 12/10/2016.
 */

@Entity
@Table(name = "morephotos")
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
    int timeToLive;

    @Column(nullable = false)
    boolean ispublic;

    public Photo() {
    }

    public Photo(User sender, User receiver, String filename, int timeToLive, boolean ispublic) {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.timeToLive = timeToLive;
        this.ispublic = ispublic;
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

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    /*public boolean getIsPublic() {
        return isPublic;
    }*/

    public void setIsPublic(boolean ispublic) {
        this.ispublic = ispublic;
    }
}
