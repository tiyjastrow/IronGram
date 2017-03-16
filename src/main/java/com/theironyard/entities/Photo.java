package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    Integer id;

    @Column(nullable = false)
    String filename;

    @Column(nullable = false)
    long timer;

    @Column
    String isPublic;

    @Column
    LocalDateTime timeOfFirstView;

    @Column
    LocalDateTime timeOfExpiration;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

    public Photo() {
    }

    public Photo(String filename, long timer, String isPublic, User sender, User receiver) {
        this.filename = filename;
        this.timer = timer;
        this.isPublic = isPublic;
        this.sender = sender;
        this.receiver = receiver;
    }

    public LocalDateTime getTimeOfExpiration() {
        return timeOfExpiration;
    }

    public void setTimeOfExpiration() {
        this.timeOfExpiration = this.timeOfFirstView.plusSeconds(this.timer);
    }

    public LocalDateTime getTimeOfFirstView() {
        return timeOfFirstView;
    }

    public void setTimeOfFirstView() {
        this.timeOfFirstView = LocalDateTime.now();
        setTimeOfExpiration();
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
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
