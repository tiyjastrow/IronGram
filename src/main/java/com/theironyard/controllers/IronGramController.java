package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.utilitites.PasswordStorage;
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
import java.util.List;

/**
 * Created by jeremypitt on 10/12/16.
 */
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
    public void destroy(){
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(HttpSession session, String username, String password, HttpServletResponse response) throws Exception{
        User user = users.findFirstByName(username);
        if (user == null){
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }
        else if (! PasswordStorage.verifyPassword(password, user.getPassword())){
            throw new Exception("wrong password");
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
    public User getUser(HttpSession session){
        String userName = (String) session.getAttribute("username");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, MultipartFile photo) throws Exception{
        String username = (String) session.getAttribute("username");
        if (username == null){
            throw new Exception("not logged in");
        }

        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null){
            throw new Exception("receiver doesn't exist");
        }

        if (! photo.getContentType().startsWith("image")){
            throw new Exception("only images are allowed");
        }

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setReceiver(receiverUser);
        p.setFilename(photoFile.getName());
        photos.save(p);

        response.sendRedirect("/");

        return p;
    }

    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("not logged in");
        }
        User user = users.findFirstByName(username);
        return photos.findByReceiver(user);
    }
    
}
