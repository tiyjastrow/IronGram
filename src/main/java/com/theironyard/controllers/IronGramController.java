package com.theironyard.controllers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepository;
import com.theironyard.services.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
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
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

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

    //HttpServletResponse send user record back to
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if (user == null) {
            //handles when there is no user
            //set user = to new user
            user = new User(username, PasswordStorage.createHash(password));
            //add to db
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong Password");
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

    //returns valid user that is logged in
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public User getUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        return users.findFirstByName(username);
    }

    @RequestMapping(path = "/upload")
    public Photo upload(MultipartFile photo, HttpSession session, HttpServletResponse response, String receiver, String seconds, String privacy) throws Exception {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            throw new Exception("Not logged in");
        }
        User senderUser = users.findFirstByName(username);
        User receiverUser = users.findFirstByName(receiver);
        if (receiverUser == null) {
            throw new Exception("receiver name doesn't exist");
        }
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Only images are allowed.");
        }

        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        if(privacy == null){
            p.setPrivacy(false);
        }else{
            p.setPrivacy(true);
        }
        p.setSeconds(Integer.parseInt(seconds));
        p.setReceiver(receiverUser);
        p.setSender(senderUser);
        p.setFilename(photoFile.getName());
        photos.save(p);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping("/photos")
    public List<Photo> showPhotos(HttpSession session, HttpServletResponse response) throws Exception {
        String username = (String) session.getAttribute("username");//this is the receiver
        if (username == null) {
            throw new Exception("Not logged in");
        }
        User user = users.findFirstByName(username);
        LocalDateTime currentTime = LocalDateTime.now();
        List<Photo> displayPhotos = photos.findByReceiverAndPrivacyTrue(user);
        for(Photo p : displayPhotos){
            LocalDateTime beginView = currentTime.plusSeconds(p.getSeconds());
            p.setTimeStamp(beginView);
            photos.save(p);
        }
        return displayPhotos;
    }

    @RequestMapping("/deletePhotos")
    public List<Integer> deletePhotos(HttpSession session) throws Exception {
        LocalDateTime currentTime = LocalDateTime.now();
        String username = (String) session.getAttribute("username");//this is the receiver
        if (username == null) {
            throw new Exception("Not logged in");
        }
        User user = users.findFirstByName(username);
        List<Photo> displayPhotos = photos.findByReceiverAndPrivacyTrue(user);
        ArrayList<Integer> deletedPhotoId = new ArrayList();

        for(Photo p : displayPhotos) {
            if (currentTime.isAfter(p.getTimeStamp()) && p.getPrivacy()){
                photos.delete(p);
                deletedPhotoId.add(p.getId());
                File deleteDbPhoto = new File("public/" + p.getFilename());
                deleteDbPhoto.delete();
            }
        }
        return deletedPhotoId;
    }

//    @RequestMapping(value = "/public-photos", method = RequestMethod.GET)
//    public List<Photo> showPublicPhotos(HttpSession session, HttpServletResponse response) throws Exception {
////        String username = (String) session.getAttribute("username");//this is the receiver
////        if (username == null) {
////            throw new Exception("Not logged in");
////        }
////        User user = users.findFirstByName(username);
//        return photos.findByPrivacyFalseAndSender();
//    }
}
