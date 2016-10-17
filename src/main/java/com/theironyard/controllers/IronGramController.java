package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.utilities.PasswordStorage;
import com.theironyard.entities.User;
import org.h2.tools.Server;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String userName, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (! PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("userName", userName);
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
        String userName = (String) session.getAttribute("userName");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.GET)
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver,
            boolean isPublic, MultipartFile photo, int seconds) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("not logged in.");
        }
        User sender = users.findFirstByName(userName);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null) {
            throw new Exception("Receiver does not exist.");
        }

        if (! photo.getContentType().startsWith("image")) {
            throw new Exception("Only images are allowed.");
        }

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        if(seconds <= 0) {
            seconds = 10;
        }
        Photo p = new Photo();
        p.setSender(sender);
        p.setReceiver(receiverUser);
        p.setFilename(photoFile.getName());
        p.setSecondsToDelete(seconds);
        if (isPublic) {
            p.setpublic(true);
        }
        photos.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("Not logged in.");
        }

        User user = users.findFirstByName(userName);

        return photos.findByReceiver(user);
    }

    @RequestMapping("/delete")
    public void deletePhoto(int id, HttpServletResponse response) throws Exception {
        Photo p = photos.findOne(id);
        Path path = Paths.get("public/", p.getFilename());
        Files.delete(path);
        photos.delete(id);

        response.sendRedirect("/");
    }

    @RequestMapping(path = "/public/{username}", method = RequestMethod.GET)
    public List<Photo> getPublic(@PathVariable("username") String username) {
        User user = users.findFirstByName(username);
        return photos.findBySenderAndIspublic(user, true);
    }
}