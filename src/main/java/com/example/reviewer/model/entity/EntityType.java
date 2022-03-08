package com.example.reviewer.model.entity;

public enum EntityType {
    KINDER_GARTEN("Детский сад"), SCHOOL("Школа"), GYMNASIUM("Гимназия"), COLLEGE("Колледж"), LYCEUM("Лицей"), UNIVERSITY("Университет");

    private String name;

    EntityType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
