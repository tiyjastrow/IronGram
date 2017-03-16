package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.apache.tomcat.jni.Local;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

@RestController
public class IronGramController {

    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
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
            throw new Exception("wrong password");
        }
        session.setAttribute("username", username);


        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, MultipartFile photo, String receiver, Integer timer, String checkBox) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("User is not logged in.");
        }
        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);
        if (receiverUser == null) {
            throw new Exception("Receiver name does not exist.");
        }
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Only image files are allowed.");
        }

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        fos.close();

        Photo p = new Photo(photoFile.getName(), timer, checkBox, senderUser, receiverUser);
        photos.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photo", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("User is not logged in.");
        }
        User user = users.findFirstByName(username);
        List<Photo> usersPhotos = photos.findByReceiver(user);

        for (Photo photo : usersPhotos) {
            if (photo.getTimeOfFirstView() == null) {
                photo.setTimeOfFirstView();
                photos.save(photo);
            }
            if (LocalDateTime.now().isAfter(photo.getTimeOfExpiration())) {
                photos.delete(photo.getId());
            }
        }
        return photos.findByReceiver(user);
    }
}

//            System.out.println("currentTime = " + currentTime);
//            System.out.println("deleteTime = " + deleteTime);
//            System.out.println(" currentTime.isAfter(deleteTime) = " + currentTime.isAfter(deleteTime));


//    HashMap<String, LocalDateTime> timeMap = new HashMap<>();
//
//    LocalDateTime timeOfFirstView = LocalDateTime.now();
//
//        for (Photo photo : usersPhotos) {
//                timeMap.putIfAbsent(photo.getFilename(), timeOfFirstView);  //DATABASE
//                long offset = photo.getTimer();
//                LocalDateTime deleteTime = timeMap.get(photo.getFilename()).plusSeconds(offset);
//
//                if (LocalDateTime.now().isAfter(deleteTime)) {
//                photos.delete(photo.getId());
//                }
//                }