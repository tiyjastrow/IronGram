package com.theironyard.services;

import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Zach on 10/12/16.
 */

public interface UserRepo extends CrudRepository<User,Integer> {
    User findFirstByName(String name);
}
