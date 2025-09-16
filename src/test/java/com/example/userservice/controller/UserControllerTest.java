package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.UserUpsertDto;
import com.example.userservice.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String BASE = "/api/v1/users";

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @MockBean UserService userService;

    @Test
    void get_shouldReturnUser() throws Exception {
        Mockito.when(userService.findById(1L))
                .thenReturn(new UserDto(1L, "John", "john@ex.com", 30));

        mvc.perform(get(BASE + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.email", is("john@ex.com")))
                .andExpect(jsonPath("$.age", is(30)));
    }

    @Test
    void list_shouldReturnUsers() throws Exception {
        Mockito.when(userService.findAll())
                .thenReturn(List.of(new UserDto(1L, "John", "john@ex.com", 30)));

        mvc.perform(get(BASE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("John")));
    }

    @Test
    void create_shouldReturnCreated() throws Exception {
        var req = new UserUpsertDto("Jane", "jane@ex.com", 25);
        var res = new UserDto(10L, "Jane", "jane@ex.com", 25);

        Mockito.when(userService.create(any(UserUpsertDto.class))).thenReturn(res);

        mvc.perform(post(BASE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", BASE + "/10"))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Jane")));
    }

    @Test
    void update_shouldReturnOk() throws Exception {
        var req = new UserUpsertDto("Jack", "jack@ex.com", 28);
        var res = new UserDto(2L, "Jack", "jack@ex.com", 28);

        Mockito.when(userService.update(eq(2L), any(UserUpsertDto.class))).thenReturn(res);

        mvc.perform(put(BASE + "/{id}", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.email", is("jack@ex.com")));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {
        mvc.perform(delete(BASE + "/{id}", 3))
                .andExpect(status().isNoContent());
    }

}
