package com.example.reviewer.model.user;

public enum UserRole {
    USER("Пользователь", 5), MODERATOR("Модератор", 2), ADMIN("Администратор", 1);

    private String name;
    private int id;

    UserRole(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
