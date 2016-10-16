package com.theironyard.services;

import com.theironyard.entities.*;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

/**
 * Created by joe on 12/10/2016.
 */
public interface PhotoRepository extends CrudRepository<Photo, Integer> {


    ArrayList<Photo> findByReceiver(User user);
    ArrayList<Photo> findFirstByReceiver(User user);
    ArrayList<Photo> findBySenderAndIspublic(User sender, boolean isPublic);
}
