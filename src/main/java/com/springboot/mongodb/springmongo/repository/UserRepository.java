package com.springboot.mongodb.springmongo.repository;


import com.springboot.mongodb.springmongo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

// Here @Repository is not added because we implemented MongoRepository,
// but if you used custom you should add @Repository annotation here.

public interface UserRepository extends MongoRepository<User, String> {
    // custom queries if needed
}