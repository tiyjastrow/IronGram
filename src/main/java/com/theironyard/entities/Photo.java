package com.theironyard.entities;

import javax.persistence.*;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    Integer id;

    @Column(nullable = false)
    String filename;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    public Photo(String filename, User sender, User receiver) {
        this.filename = filename;
        this.sender = sender;
        this.receiver = receiver;
    }

    public Photo() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
