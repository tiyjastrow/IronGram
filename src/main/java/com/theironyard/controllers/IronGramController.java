package com.theironyard.controllers;
import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.utilities.PasswordStorage;
import org.h2.tools.Server;
import com.theironyard.services.PhotoRepo;
import com.theironyard.services.UserRepo;
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

/**
 * Created by joshuakeough on 10/12/16.
 */
@RestController
public class IronGramController {
    @Autowired
    UserRepo users;

    @Autowired
    PhotoRepo photos;

    Server dbui;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public User login(String username, String password, HttpSession session, HttpServletResponse response) throws Exception {
        User user = users.findFirstByName(username);
        if(user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        }else if(! PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong password");
        }
        session.setAttribute("userName", username);
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
        String userName = (String)session.getAttribute("userName");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, String reciever, MultipartFile photo) throws Exception {
        String userName = (String)session.getAttribute("userName");
        if(userName == null) {
            throw new Exception("Not logged in");
        }
        User senderUser = users.findFirstByName(userName);
        User receiverUser = users.findFirstByName(reciever);
        if (receiverUser == null) {
            throw new Exception("No receiver exists");
        }
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("Only images are aloud");
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
        String username = (String) session.getAttribute("userName");
        if(username == null) {
            throw new Exception("Not Logged in");
        }

        User user = users.findFirstByName(username);
        return photos.findByReceiver(user);
    }


}
