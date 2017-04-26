package com.bharris.controllers;

import com.bharris.entities.Photo;
import com.bharris.entities.User;
import com.bharris.services.PhotoRepository;
import com.bharris.services.UserRepository;
import com.bharris.utilities.PasswordStorage;
import org.h2.tools.Server;
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
import java.sql.SQLException;
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
    public void destroy(){
        dbui.stop();
    }

    @RequestMapping(path="/login", method= RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if(user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }else if (! PasswordStorage.verifyPassword(password,user.getPassword())) {
            throw new Exception("Invalid login");
        }

        session.setAttribute("username", username);
        response.sendRedirect("/");
        return user;
    }

    @RequestMapping(path="/logout")
    public void logout(HttpSession session, HttpServletResponse response) throws IOException {
        session.invalidate();
        response.sendRedirect("/");
    }

    @RequestMapping(path="/user", method=RequestMethod.GET)
    public User getUser(HttpSession session){
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping(path="/upload")
    public Photo getPhotos(MultipartFile photo, HttpSession session, HttpServletResponse response, String receiver,Boolean pub,  String seconds) throws Exception {
        String username = (String)session.getAttribute("username");
        if(username == null) {
            throw new Exception("Not logged in...");
        }
        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);
        if(receiverUser == null) {
            throw new Exception("Receiver name doesn't exist");
        }
        if(! photo.getContentType().startsWith("image")){
            throw new Exception("Only images are allowed");
        }
        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());
        fos.close();
        Photo p = null;
        if(pub == null && seconds.isEmpty()) {
            p = new Photo(photoFile.getName(), senderUser, receiverUser);
        }else if(pub != null && seconds.isEmpty()){
            p = new Photo(photoFile.getName(),pub, senderUser, receiverUser);
        }else if(pub == null && !seconds.isEmpty()){
            p = new Photo(photoFile.getName(), Integer.parseInt(seconds),senderUser,receiverUser);
        }else if(pub != null && !seconds.isEmpty()){
            p = new Photo(photoFile.getName(), Integer.parseInt(seconds), pub, senderUser,receiverUser);
        }
        photos.save(p);
        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path="/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String username = (String) session.getAttribute("username");
        if(username == null){
            throw new Exception("Not logged in...");
        }
        User user = users.findFirstByName(username);
        return photos.findByReceiver(user);
    }

    public void deletePhoto(int id) throws IOException {
        Photo p = photos.findOne(id);
        String file = p.getFilename();
        photos.delete(id);

        File f = new File("public/" + file);
        f.delete();
    }

    @RequestMapping(path="/decrement-photo")
    public int decrementPhoto(int id, HttpServletResponse response) throws IOException {
        Photo photo = photos.findOne(id);
        photo.decreaseCount();
        if(photo.getTime() <= 0) {
            deletePhoto(id);
        }else {
            photos.save(photo);
        }

        return photo.getTime();
    }

    @RequestMapping(path="/public-photos")
    public List<Photo> showPublicPhotos(String username){
        User user = users.findFirstByName(username);
        List<Photo> publicPhotos = photos.findBySender_NameAndIsPublicTrue(username);
        return publicPhotos;
    }
}
