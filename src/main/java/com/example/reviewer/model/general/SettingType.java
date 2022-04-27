package com.example.reviewer.model.general;

public enum SettingType {
    ENABLE_REGISTRATION("Регистрация активна"), ENABLE_LOGIN("Авторизация активна"), ENABLE_REVIEWS("Разрешено сохранение отзывов");

    private String name;

    SettingType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
