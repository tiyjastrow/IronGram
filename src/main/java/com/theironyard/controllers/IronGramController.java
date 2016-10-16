package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.utilities.*;

import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import org.h2.jdbc.JdbcSQLException;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.h2.tools.Server;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Created by joe on 12/10/2016.
 */

/**
 * Hint for today’s assignment: Remember in the AjaxChat project in the chat.js we used
 * setInterval(getMessages, 1000) to pull the messages every second.
 * (might could make use of that if you want to fiddle with updating the webpage photo)
 */

@RestController
public class IronGramController {

    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws java.sql.SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(HttpSession session, HttpServletResponse response, String username, String password) throws Exception {
        User user = users.findFirstByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws Exception {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String userName = (String) session.getAttribute("username");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, MultipartFile photo, int ttl, String isPublic
    ) throws Exception {

        boolean newPublic = true;

        if(isPublic == null || !isPublic.equals("public")) {
            newPublic = false;
        }

        System.out.println(newPublic);

        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in");
        }
        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null) {
            throw new Exception("Receiver does not exist");
        }

        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Only images are allowed");
        }

        ttl = ttl * 1000;

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setReceiver(receiverUser);
        p.setFilename(photoFile.getName());
        p.setTimeToLive(ttl);
        p.setIsPublic(newPublic);
        photos.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public ArrayList<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            throw new Exception("not logged in");
        }

        User user = users.findFirstByName(username);
        return photos.findByReceiver(user);
    }

    @RequestMapping(path = "/delete")
    public void delete(HttpSession session)throws InterruptedException, IOException {

        String username = (String) session.getAttribute("username");

        User receiver = users.findFirstByName(username);

        ArrayList<Photo> tempPhoto = photos.findByReceiver(receiver);

        int timer = tempPhoto.get(0).getTimeToLive();

        Thread.sleep(timer);

        deletePhotos(username);
    }

    public void deletePhotos(String username)throws InterruptedException, IOException{
        User user = users.findFirstByName(username);
        ArrayList<Photo> photoList = photos.findByReceiver(user);

        for (Photo photo: photoList){
            Path path = Paths.get("public/", photo.getFilename());
            Files.delete(path);
            photos.delete(photo);
        }
    }

    @RequestMapping(path = "/public{username}", method = RequestMethod.GET)
    public ArrayList<Photo> publicPhotos(@PathVariable("username") String username){
        User user = users.findFirstByName(username);
        return photos.findBySenderAndIspublic(user, true);
    }

}

