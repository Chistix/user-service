package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserUpsertDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    // Константы
    private static final String USER_NOT_FOUND = "User not found: ";
    private static final String EMAIL_EXISTS = "Email already exists: ";

    // Поля
    private final UserRepository userRepository;

    // Публичные методы
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto findById(Long id) {
        return toDto(require(id));
    }

    @Override
    @Transactional
    public UserDto create(UserUpsertDto dto) {
        ensureEmailUnique(dto.email(), null);
        User entity = toNewEntity(dto);
        return toDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserUpsertDto dto) {
        ensureEmailUnique(dto.email(), id);
        User entity = require(id);
        apply(dto, entity);
        return toDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User entity = require(id);
        userRepository.delete(entity);
    }

    // Приватные методы
    private User require(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND + id));
    }

    private void ensureEmailUnique(String email, Long selfId) {
        var existing = userRepository.findByEmail(email);
        if (existing.isPresent() && (selfId == null || !existing.get().getId().equals(selfId))) {
            throw new IllegalArgumentException(EMAIL_EXISTS + email);
        }
    }

    private UserDto toDto(User e) {
        return new UserDto(e.getId(), e.getName(), e.getEmail(), e.getAge());
    }

    private User toNewEntity(UserUpsertDto dto) {
        User e = new User();
        apply(dto, e);
        return e;
    }

    private void apply(UserUpsertDto dto, User e) {
        e.setName(dto.name());
        e.setEmail(dto.email());
        e.setAge(dto.age());
    }
}
