package com.example.userservice;

import com.example.userservice.console.ConsoleUI;
import com.example.userservice.dao.UserDao;
import com.example.userservice.dao.UserDaoImpl;
import com.example.userservice.entity.User;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private static final String TITLE = "=== User Service (Hibernate + PostgreSQL) ===";

    public static void main(String[] args) { run(); }

    private static void run() {
        UserDao dao = new UserDaoImpl();
        ConsoleUI ui = new ConsoleUI(new Scanner(System.in));

        boolean running = true;
        while (running) {
            ui.printTitle(TITLE);
            ui.printMenu();
            switch (ui.readMenuChoice()) {
                case 1 -> handleCreate(ui, dao);
                case 2 -> handleFindById(ui, dao);
                case 3 -> handleFindAll(ui, dao);
                case 4 -> handleUpdate(ui, dao);
                case 5 -> handleDelete(ui, dao);
                case 0 -> running = false;
                default -> ui.println("Неизвестный пункт меню\n");
            }
        }
        ui.println("Пока!");
    }

    private static void handleCreate(ConsoleUI ui, UserDao dao) {
        String name = ui.readNonEmpty("Имя: ");
        String email = ui.readEmail("Email: ");          // <--- заменили
        Integer age = ui.readAge("Возраст (опционально): ");

        try {
            User created = dao.create(new User(name, email, age));
            ui.println("Создано с id=" + created.getId() + "\n");
        } catch (ConstraintViolationException e) {
            ui.println("Ошибка: email уже существует.\n");
        } catch (Exception e) {
            log.error("Create failed", e);
            ui.println("Не удалось создать пользователя: " + e.getMessage() + "\n");
        }
    }

    private static void handleFindById(ConsoleUI ui, UserDao dao) {
        long id = ui.readId("ID: ");
        Optional<User> found = dao.findById(id);
        if (found.isPresent()) printUser(ui, found.get());
        else ui.println("Не найдено.\n");
    }

    private static void handleFindAll(ConsoleUI ui, UserDao dao) {
        List<User> users = dao.findAll();
        if (users.isEmpty()) { ui.println("Список пуст.\n"); return; }
        for (User u : users) printUser(ui, u);
        ui.println("");
    }

    private static void handleUpdate(ConsoleUI ui, UserDao dao) {
        long id = ui.readId("ID пользователя для обновления: ");
        Optional<User> found = dao.findById(id);
        if (found.isEmpty()) { ui.println("Не найдено.\n"); return; }

        User u = found.get();
        String name = ui.readNonEmpty("Новое имя: ");
        String email = ui.readEmail("Новый email: ");    // <--- заменили
        Integer age = ui.readAge("Новый возраст (опционально): ");

        u.setName(name);
        u.setEmail(email);
        u.setAge(age);

        try {
            dao.update(u);
            ui.println("Обновлено.\n");
        } catch (ConstraintViolationException e) {
            ui.println("Ошибка: email уже существует.\n");
        } catch (Exception e) {
            log.error("Update failed", e);
            ui.println("Не удалось обновить: " + e.getMessage() + "\n");
        }
    }

    private static void handleDelete(ConsoleUI ui, UserDao dao) {
        long id = ui.readId("ID для удаления: ");
        try {
            boolean ok = dao.deleteById(id);
            ui.println(ok ? "Удалено.\n" : "Не найдено.\n");
        } catch (Exception e) {
            log.error("Delete failed", e);
            ui.println("Не удалось удалить: " + e.getMessage() + "\n");
        }
    }

    private static void printUser(ConsoleUI ui, User u) {
        ui.println("#" + u.getId() + ": " + u.getName() +
                ", email=" + u.getEmail() +
                ", age=" + (u.getAge() == null ? "-" : u.getAge()) +
                ", createdAt=" + u.getCreatedAt());
    }
}

