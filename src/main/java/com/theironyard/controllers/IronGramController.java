package com.theironyard.controllers;


import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by jakefroeb on 10/12/16.
 */
@RestController
public class IronGramController {
    @Autowired
    UserRepository users;

    @Autowired
    PhotoRepository photos;
    Server dbui = null;

    @PostConstruct
    public void init() throws Exception {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping("/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws Exception {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }
    @RequestMapping("/upload")
    public Photo upload(
            HttpSession session,
            HttpServletResponse response,
            String receiver,
            MultipartFile photo,
            String timer,
            boolean pub
    ) throws Exception {
        int secondsUntilDelete;
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);
        if (receiverUser == null) {
            throw new Exception("No reciever specified");
        }
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Only images are allowed.");
        }
        if(timer == null){
            secondsUntilDelete = 10;
        }else{
            secondsUntilDelete = Integer.parseInt(timer);
        }
        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        Photo p = new Photo();
        p.setSender(senderUser);
        p.setReceiver (receiverUser);
        p.setFilename(photoFile.getName());
        p.setSecondsUntilDelete(secondsUntilDelete);
        p.setPub(pub);
        photos.save(p);
        response.sendRedirect("/");
        return p;
    }
    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in.");
        }
        User user = users.findFirstByName(username);
        List<Photo> pList;
        pList = photos.findAllByReceiver(user);
        return pList;
    }
    @RequestMapping("/delete")
    public void deletePhoto(int id)throws Exception{
        String fileName = photos.findOne(id).getFilename();
        Path filePath = Paths.get("public/", fileName);
        Files.delete(filePath);
        photos.delete(id);
    }
    @RequestMapping("/search-publicphotos")
    public List<Photo> viewPhotos(String searchname, HttpSession session, HttpServletResponse response)throws Exception{
        List<Photo> pubPhotos = photos.findAllBySenderAndPub(users.findFirstByName(searchname),true);
        return pubPhotos;
    }
}