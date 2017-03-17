package com.bharris.services;

import com.bharris.entities.Photo;
import com.bharris.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by BHarris on 3/15/17.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    List<Photo> findByReceiver(User user);
    List<Photo> findBySender_NameAndIsPublicTrue(String name);

}
