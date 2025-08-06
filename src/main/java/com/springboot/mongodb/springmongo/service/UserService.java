package com.springboot.mongodb.springmongo.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.springboot.mongodb.springmongo.model.User;
import com.springboot.mongodb.springmongo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    public boolean isExistsById(String id){
        return userRepository.existsById(id);
    }
    public User patchUser(String id, Map<String, Object> updates) {
        log.info("patch User is executing .....");
        log.warn("patch User is executing .....");
        log.debug("patch User is executing .....");
        log.error("patch User is executing .....");
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            updates.forEach((k, v) -> {
                Field field = ReflectionUtils.findField(User.class, k);
                field.setAccessible(true);
                ReflectionUtils.setField(field, user, v);
            });
            return userRepository.save(user);
        }
      return null;
    }

    public User jsonPatchUpdate(String id, JsonPatch patch){
        Optional<User> optionalUser = userRepository.findById(id);
        User user = optionalUser.get();
        try {
            JsonNode userNode = objectMapper.convertValue(user, JsonNode.class);
            System.out.println("Original JSON before patch:");
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(userNode));
            JsonNode patched = patch.apply(userNode);
            User updatedUser = objectMapper.treeToValue(patched, User.class);
            userRepository.save(updatedUser);
            return updatedUser;
        } catch (JsonPatchException | JsonProcessingException e) {
            //log.info("Patch failed: " + e.getMessage());
            System.out.println(e.getMessage());
        }
        return null;
    }
}
