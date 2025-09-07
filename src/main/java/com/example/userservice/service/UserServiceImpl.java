package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao dao;

    public UserServiceImpl(UserDao dao) {
        this.dao = dao;
    }

    @Override
    public User create(User u) {
        if (u == null) {
            throw new IllegalArgumentException("User must not be null");
        }
        // при необходимости можно добавить доп. проверки полей:
        // if (u.getEmail() == null || u.getEmail().isBlank()) throw new IllegalArgumentException("email required");
        return dao.create(u);
    }

    @Override
    public Optional<User> findById(Long id) {
        return dao.findById(id);
    }

    @Override
    public List<User> findAll() {
        return dao.findAll();
    }

    @Override
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
