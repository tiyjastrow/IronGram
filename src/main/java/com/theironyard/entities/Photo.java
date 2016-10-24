package com.theironyard.entities;

import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.servlet.annotation.MultipartConfig;

/**
 * Created by joshuakeough on 10/12/16.
 */
@Entity
@Table(name= "photos")
public class Photo {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;

    @Column(nullable = false)
    private String filename;

    @Column
    private int displayTime;

    @Column
    private Boolean isPublic;


    public Photo() {
    }

    public Photo(User sender, User receiver, String filename, int displayTime, Boolean isPublic) {
        this.sender = sender;
        this.receiver = receiver;
        this.filename = filename;
        this.displayTime = displayTime;
        this.isPublic = isPublic;
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

    public int getDisplayTime() {
        return displayTime;
    }

    public void setDisplayTime(int displayTime) {
        this.displayTime = displayTime;
    }

    public Boolean isPublic() {
        return isPublic;
    }

    public void setPublic(String aPublic) {
        if(aPublic == null) {
            isPublic = false;
        } else isPublic = true;

//        if (aPublic.equalsIgnoreCase("true")){
//            isPublic = true;
//        }else isPublic = false;
    }
}
