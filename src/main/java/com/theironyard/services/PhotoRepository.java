package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Integer>{

        List<Photo> findByReceiver(User user);
        List<Photo> findBySenderAndPublicityIsNotNull(User user);

}
