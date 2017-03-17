package com.bharris.services;

import com.bharris.entities.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by BHarris on 3/15/17.
 */
public interface UserRepository extends CrudRepository<User, Integer>{
    User findFirstByName(String name);
}
