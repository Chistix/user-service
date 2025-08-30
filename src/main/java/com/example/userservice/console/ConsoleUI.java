package com.example.userservice.console;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsoleUI {

    private static final String MENU = """
=== User Service (Hibernate + PostgreSQL) ===
1. Создать пользователя
2. Получить пользователя по id
3. Список всех пользователей
4. Обновить пользователя
5. Удалить пользователя
0. Выход""";

    private static final int MAX_AGE = 150;
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public ConsoleUI(Scanner scanner) { this.scanner = scanner; }

    public void printTitle(String title) { println(title); }
    public void printMenu() { println(MENU); }

    public int readMenuChoice() {
        print("Выберите пункт: ");
        return readIntOrDefault(-1);
    }

    public long readId(String prompt) {
        print(prompt);
        return readLongOrDefault(-1);
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isBlank()) return s;
            println("Поле не может быть пустым.");
        }
    }

    public Integer readAge(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) return null;
            try {
                int age = Integer.parseInt(line);
                if (age < 0 || age > MAX_AGE) {
                    System.out.println("Возраст должен быть от 0 до " + MAX_AGE + ".");
                    continue;
                }
                return age;
            } catch (NumberFormatException e) {
                System.out.println("Введите целое число или оставьте пусто.");
            }
        }
    }

    public String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = scanner.nextLine().trim();
            if (isEmailValid(email)) return email;
            System.out.println("Некорректный email. Пример: user@example.com");
        }
    }

    private boolean isEmailValid(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public void println(String s) { System.out.println(s); }
    public void print(String s) { System.out.print(s); }

    private int readIntOrDefault(int def) {
        String raw = scanner.nextLine().trim();
        try { return Integer.parseInt(raw); } catch (NumberFormatException e) { return def; }
    }

    private long readLongOrDefault(long def) {
        String raw = scanner.nextLine().trim();
        try { return Long.parseLong(raw); } catch (NumberFormatException e) { return def; }
    }

    private final Scanner scanner;
}
