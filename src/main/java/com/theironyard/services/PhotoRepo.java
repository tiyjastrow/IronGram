package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by joshuakeough on 10/12/16.
 */
public interface PhotoRepo extends CrudRepository<Photo, Integer> {

    List<Photo> findByReceiver(User name);

    List<Photo> findBySender(User sender);

    List<Photo> findByIsPublic(Boolean b);
}

