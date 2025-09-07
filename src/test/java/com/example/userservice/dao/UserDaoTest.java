package com.example.userservice.dao;

import com.example.userservice.entity.User;
import com.example.userservice.util.HibernateUtil;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDaoTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    private SessionFactory testSessionFactory;
    private UserDao dao;

    @BeforeAll
    void setUp() {
        // Создаем тестовый SessionFactory
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        cfg.setProperty("hibernate.connection.url", POSTGRES.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", POSTGRES.getUsername());
        cfg.setProperty("hibernate.connection.password", POSTGRES.getPassword());
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.show_sql", "false");
        cfg.setProperty("hibernate.current_session_context_class", "thread");
        cfg.addAnnotatedClass(User.class);

        testSessionFactory = cfg.buildSessionFactory();

        // Заменяем SessionFactory в HibernateUtil на тестовый
        HibernateUtil.setSessionFactory(testSessionFactory);

        // Создаем DAO - теперь он будет использовать тестовый SessionFactory
        dao = new UserDaoImpl();
    }

    @AfterAll
    void tearDown() {
        // Восстанавливаем оригинальный SessionFactory
        HibernateUtil.resetSessionFactory();

        if (testSessionFactory != null) {
            testSessionFactory.close();
        }
    }

    @BeforeEach
    void cleanDb() {
        var session = testSessionFactory.getCurrentSession();
        var tx = session.getTransaction().isActive() ? session.getTransaction() : session.beginTransaction();
        session.createMutationQuery("delete from User").executeUpdate();
        tx.commit();
    }

    @Test
    void create_and_findById() {
        User u = new User("Test User", "test@example.com", 25);

        User saved = dao.create(u);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test User");
        assertThat(saved.getEmail()).isEqualTo("test@example.com");

        var found = dao.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getName()).isEqualTo("Test User");
    }

    @Test
    void findAll_returnsInserted() {
        dao.create(new User("User 1", "user1@example.com", 20));
        dao.create(new User("User 2", "user2@example.com", 30));

        List<User> all = dao.findAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(User::getName).containsExactlyInAnyOrder("User 1", "User 2");
    }

    @Test
    void deleteById_removesRow() {
        User saved = dao.create(new User("To Delete", "delete@example.com", 25));
        boolean deleted = dao.deleteById(saved.getId());
        assertThat(deleted).isTrue();

        assertThat(dao.findById(saved.getId())).isEmpty();
    }

    @Test
    void deleteById_returnsFalseWhenNotFound() {
        boolean deleted = dao.deleteById(999L);
        assertThat(deleted).isFalse();
    }

    @Test
    void update_modifiesExistingUser() {
        User saved = dao.create(new User("Original", "original@example.com", 25));

        saved.setName("Updated");
        saved.setEmail("updated@example.com");
        saved.setAge(30);

        User updated = dao.update(saved);

        assertThat(updated.getName()).isEqualTo("Updated");
        assertThat(updated.getEmail()).isEqualTo("updated@example.com");
        assertThat(updated.getAge()).isEqualTo(30);

        var found = dao.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Updated");
    }
}