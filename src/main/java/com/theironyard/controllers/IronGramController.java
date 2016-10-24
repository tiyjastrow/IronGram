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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
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

    static private int displayTime;

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
        if (user == null) {
            user = new User(username, PasswordStorage.createHash(password));
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
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
        String userName = (String) session.getAttribute("userName");
        return users.findFirstByName(userName);
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, int display, MultipartFile photo, String isPublic) throws Exception {
        String userName = (String) session.getAttribute("userName");
        displayTime = display;
        User senderUser = users.findFirstByName(userName);
        User receiverUser = users.findFirstByName(receiver);

        validateUser(userName, receiverUser);

        Photo p = savePhoto(photo, isPublic, senderUser, receiverUser);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photos")
    public List<Photo> showPhotos(HttpSession session) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("user null");
        }
        User user = users.findFirstByName(userName);

        List<Photo> publicPhotos = photos.findByIsPublic(true);

        List<Photo> receiverPhotos = photos.findByReceiver(user);

        List<Photo> listAll = new ArrayList<>(publicPhotos);
        listAll.addAll(receiverPhotos);

        return listAll;
    }

    @RequestMapping(path = "/public-photos/{userName}", method = RequestMethod.GET)
    public List<Photo> getPublicPhotos(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        List<Photo> publicPhotos = photos.findByReceiver(user);

        return publicPhotos;
    }

    @RequestMapping(path = "/delete")
    public void deletePhoto(HttpServletResponse response, HttpSession session) throws InterruptedException, IOException {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        List<Photo> yourPhotos = (List) photos.findByReceiver(user);
        Thread.sleep(displayTime * 1000);
        for (Photo p : yourPhotos) {
            String fileName = p.getFilename();
            Path filePath = Paths.get("public/", (fileName));
            Files.delete(filePath);
            photos.delete(p);
        }
        response.sendRedirect("/");

    }

    private Photo savePhoto(MultipartFile photo, String isPublic, User senderUser, User receiverUser) throws Exception {
        if (!photo.getContentType().startsWith("image")) {
            throw new Exception("not an image file");
        }
        File photoFile = File.createTempFile("photo", photo.getOriginalFilename(), new File("public"));
        FileOutputStream fos = new FileOutputStream(photoFile);
        fos.write(photo.getBytes());

        Photo p = new Photo();
        p.setSender(senderUser);
        p.setReceiver(receiverUser);
        p.setFilename(photoFile.getName());
        p.setPublic(isPublic);
        photos.save(p);
        return p;
    }

    private void validateUser(String userName, User receiverUser) throws Exception {
        if (userName == null) {
            throw new Exception("null userName");
        }
        if (receiverUser == null) {
            throw new Exception("null receiver");
        }
    }


}
