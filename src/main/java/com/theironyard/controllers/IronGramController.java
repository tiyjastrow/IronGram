package com.theironyard.controllers;

import com.theironyard.entity.Photo;
import com.theironyard.entity.User;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class IronGramController {

    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;

    Server dbui;

    @PostConstruct
    public void init() throws Exception {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy // stops web server before it crashes
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String userName, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(password));
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("wrong password");
        }
        session.setAttribute("userName", userName);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path = "/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }


    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, MultipartFile photo, boolean shared, String timer) throws Exception {
        String userName = (String) session.getAttribute("userName");
        User senderUser = users.findFirstByName(userName);
        User receiverUser = users.findFirstByName(receiver);
        if (receiverUser == null) {
            throw new Exception("receiver does not exist");
        }
//is it an image type file?
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("only images are allowed");
        }
// new file public: creates directory if it doesn't already have one
        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        if (shared != true){
            shared = false;
        } else {
            shared = true;
        }
        Integer seconds = Integer.parseInt(timer);


        Photo p = new Photo(senderUser, receiverUser, photoFile.getName(), shared, seconds);
        photos.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photos", method = RequestMethod.GET)
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        return photos.findByReceiver(user);
    }

    //public = shared
    @RequestMapping(path = "/public-photos", method = RequestMethod.GET)
    public List<Photo> showSharedPhotos(HttpSession session) throws Exception {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        List<Photo> userPhotos = photos.findBySender(user);
        List<Photo> sharedPhotos = new ArrayList<>();
        for (Photo photo: userPhotos){
            if (photo.isShared()){
                sharedPhotos.add(photo);
            }
        }
        return sharedPhotos;
    }

    @RequestMapping(path = "/delete", method = RequestMethod.POST)
    public void deletePhoto(Integer id) throws IOException {
        Photo photo = photos.findById(id);
        String fileName = photo.getFilename();
        Path filePath = Paths.get("public/", fileName);
        Files.delete(filePath);
        photos.delete(id);
    }



}


