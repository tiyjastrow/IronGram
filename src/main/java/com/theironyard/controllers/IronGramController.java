package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.utilitites.PasswordStorage;
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

    public static Integer timer;

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
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, String delay, String isPublic, MultipartFile photo) throws Exception{
        String username = (String) session.getAttribute("username");

        timer = Integer.parseInt(delay);

        if (username == null){
            throw new Exception("not logged in");
        }

        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);

        if (receiverUser == null){
            throw new Exception("receiver doesn't exist");
        }

        Photo p = savePhoto(delay, isPublic, photo, senderUser, receiverUser);

        response.sendRedirect("/");

        return p;
    }

    private Photo savePhoto(String delay, String isPublic, MultipartFile photo, User senderUser, User receiverUser) throws Exception {
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
        p.setDelay(Integer.parseInt(delay));
        p.setIsPublic(isPublic);
        photos.save(p);
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

    @RequestMapping(path = "/delete")
    public void deletePhoto(HttpSession session) throws Exception {
        Thread.sleep(timer * 1000);
        String username = (String) session.getAttribute("username");
        deleteFiles(username);
    }

    private void deleteFiles(String username) throws Exception {
        User receiverUser = users.findFirstByName(username);
        List<Photo> photoList = photos.findByReceiver(receiverUser);
        for (Photo p: photoList){
            String fileName = p.getFilename();
            Path filePath = Paths.get("public/", fileName);
            Files.delete(filePath);
            photos.delete(p);
        }
    }

    @RequestMapping(path = "/public-photos/{username}", method = RequestMethod.GET)
    public List<Photo> getPublicPhotos(HttpSession session, @PathVariable("username") String username){
        username = (String) session.getAttribute("username");
        User user = users.findFirstByName(username);
        List<Photo> publicPhotos = photos.findByReceiver(user);

        return publicPhotos;
    }
}
