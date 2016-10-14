package com.theironyard.services;

import com.theironyard.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by jakefroeb on 10/12/16.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    User findFirstByName(String name);
}
