package com.theironyard.services;

import com.theironyard.entity.Photo;
import com.theironyard.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by halleyfroeb on 10/12/16.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {

    List<Photo> findByReceiver(User user);
    List<Photo> findBySender(User user);

    Photo findById(Integer id);

}
