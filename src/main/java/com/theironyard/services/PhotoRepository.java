package com.theironyard.services;


import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Integer>{

    List<Photo> findByReceiver(User user);

    List<Photo> findByReceiverAndMakePublicIsTrue(User user);

    // todo custom query getting showTime from photo object



    @Transactional
    Long deleteByReceiver(User user);

}
