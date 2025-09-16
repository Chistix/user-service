package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserDaoTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void saveAndFindByEmail_works() {

        User u = new User();
        u.setName("Repo");
        u.setEmail("repo@ex.com");
        u.setAge(20);

        User saved = userRepository.save(u);

        assertThat(saved.getId()).isNotNull();
        assertThat(userRepository.existsByEmail("repo@ex.com")).isTrue();
        assertThat(userRepository.findByEmail("repo@ex.com")).isPresent();
    }
}