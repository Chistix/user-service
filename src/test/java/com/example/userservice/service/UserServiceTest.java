package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy; // <— для негативного кейса
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

    @Mock
    UserDao dao;

    @InjectMocks
    UserServiceImpl service;

    @Test
    @DisplayName("create: делегирует в dao.create и возвращает результат")
    void create_ok() {
        User input = new User();
        User expected = new User();

        when(dao.create(input)).thenReturn(expected);

        User actual = service.create(input);

        assertThat(actual).isSameAs(expected);
        verify(dao).create(input);
        verifyNoMoreInteractions(dao);
    }

    @Test
    @DisplayName("create: бросает IllegalArgumentException при null")
    void create_null_throws() {
        assertThatThrownBy(() -> service.create(null))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(dao);
    }

    @Test
    @DisplayName("findById: возвращает Optional с найденным пользователем")
    void findById_found() {
        long id = 42L;
        User u = new User();
        when(dao.findById(id)).thenReturn(Optional.of(u));

        Optional<User> actual = service.findById(id);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isSameAs(u);
        verify(dao).findById(id);
        verifyNoMoreInteractions(dao);
    }

    @Test
    @DisplayName("findById: возвращает пустой Optional, если ничего не найдено")
    void findById_notFound() {
        long id = 99L;
        when(dao.findById(id)).thenReturn(Optional.empty());

        Optional<User> actual = service.findById(id);

        assertThat(actual).isEmpty();
        verify(dao).findById(id);
        verifyNoMoreInteractions(dao);
    }

    @Test
    @DisplayName("findAll: делегирует в dao.findAll и возвращает список")
    void findAll_ok() {
        var u1 = new User();
        var u2 = new User();
        when(dao.findAll()).thenReturn(List.of(u1, u2));

        List<User> list = service.findAll();

        assertThat(list).containsExactly(u1, u2);
        verify(dao).findAll();
        verifyNoMoreInteractions(dao);
    }

    @Test
    @DisplayName("deleteById: делегирует в dao.deleteById")
    void deleteById_ok() {
        long id = 7L;

        service.deleteById(id);

        verify(dao).deleteById(id);
        verifyNoMoreInteractions(dao);
    }
}
