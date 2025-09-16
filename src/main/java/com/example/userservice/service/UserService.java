package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserUpsertDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(UserUpsertDto dto);
    UserDto update(Long id, UserUpsertDto dto);
    void delete(Long id);

}
