package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by jakefroeb on 10/12/16.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    List<Photo> findAllByReceiver(User user);
    List<Photo> findAllBySenderAndPub(User user,boolean pub);
}