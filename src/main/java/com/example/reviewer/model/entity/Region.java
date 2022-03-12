package com.example.reviewer.model.entity;

public enum Region {
    BREST("Брестская"), VITEBSK("Витебская"), GOMEL("Гомельская"), GRODNO("Гродненская"),
    MINSK("Минская"), MOGILEV("Могилевская");
    private String name;

    Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
