package com.example.userservice.service;

import com.example.userservice.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User create(User u);
    Optional<User> findById(Long id);
    List<User> findAll();
    void deleteById(Long id);
}
