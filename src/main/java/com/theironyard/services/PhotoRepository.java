package com.theironyard.services;

import com.theironyard.entities.Photo;
import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Integer> {
    List<Photo> findByReceiverAndPrivacyTrue(User user);
    List<Photo> findByPrivacyFalseAndSender(User user);
    List<Photo> findBySender(User user);
    List<Photo> findByPrivacyFalse();
    //@Transactional
    //Long deleteByReceiver(User user);
}
