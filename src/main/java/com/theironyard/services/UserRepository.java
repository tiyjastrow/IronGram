package com.theironyard.services;

import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by joe on 12/10/2016.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    User findFirstByName(String name);
    //User findByReceiver(String name);

}
