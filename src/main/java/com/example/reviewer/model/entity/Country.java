package com.example.reviewer.model.entity;

public enum Country {
    BELARUS("Республика Беларусь");

    private String name;

    Country(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
