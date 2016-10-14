package com.theironyard.contollers;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import com.theironyard.services.PhotoRepo;
import com.theironyard.services.UserRepo;
import com.theironyard.utilities.PasswordStorage;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 10/12/16.
 */

@RestController
public class IronGramController {

    public static Integer sleepTimer;

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
    public User login(HttpSession session, HttpServletResponse response, String userName, String password) throws Exception {
        User user = users.findFirstByName(userName);
        if (user == null) {
            user = new User(userName, PasswordStorage.createHash(password));
            users.save(user);
        } else if (!PasswordStorage.verifyPassword(password, user.getPassword())) {
            throw new Exception("Wrong Password");
        }
        session.setAttribute("userName", userName);
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
    public Photo upload(HttpSession session, HttpServletResponse response, String receiver, Integer timer, MultipartFile photo, String isPublic) throws Exception {
        sleepTimer = timer;
        String userName = (String) session.getAttribute("userName");
        User senderUser = users.findFirstByName(userName);
        User receiverUser = users.findFirstByName(receiver);

        validateUser(userName, receiverUser);

        Photo p = savePhoto(photo, isPublic, senderUser, receiverUser);

        response.sendRedirect("/");
        return p;
    }

    @RequestMapping(path = "/photos")
    public List<Photo> showPhotos(HttpSession session, HttpServletResponse response) throws Exception {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            throw new Exception("user null");
        }
        User user = users.findFirstByName(userName);

        List<Photo> publicPhotos = photos.findByIsPublic("notNull");

        List<Photo> receiverPhotos = photos.findByReceiver(user);

        List<Photo> listAll = new ArrayList<>(publicPhotos);
        listAll.addAll(receiverPhotos);

        return listAll;
    }

    @RequestMapping(path = "/delete")
    public void delete(HttpSession session) throws InterruptedException, IOException {
        String userName = (String) session.getAttribute("userName");

        Thread.sleep(sleepTimer * 1000);
        deleteFiles(userName);

    }

    @RequestMapping(path = "/public-photos/{userName}", method = RequestMethod.GET)
    public List<Photo> getPublicPhotos(HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        User user = users.findFirstByName(userName);
        List<Photo> publicPhotos = photos.findByReceiver(user);

        return publicPhotos;
    }

    private void validateUser(String userName, User receiverUser) throws Exception {
        if (userName == null) {
            throw new Exception("null userName");
        }
        if (receiverUser == null) {
            throw new Exception("null receiver");
        }
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
        p.setIsPublic(isPublic);
        photos.save(p);
        return p;
    }

    private void deleteFiles(String userName) throws IOException {
        User receiverUser = users.findFirstByName(userName);
        List<Photo> pList = photos.findByReceiver(receiverUser);
        for (Photo p : pList) {
            String fileName = p.getFilename();
            Path filePath = Paths.get("public/", fileName);
            Files.delete(filePath);
            photos.delete(p);
        }
    }


}
