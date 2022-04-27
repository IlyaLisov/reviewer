package com.example.reviewer.model.entity;

public enum Region {
    BREST("Брестская", Country.BELARUS), VITEBSK("Витебская", Country.BELARUS), GOMEL("Гомельская", Country.BELARUS), GRODNO("Гродненская", Country.BELARUS),
    MINSK("Минская", Country.BELARUS), MOGILEV("Могилевская", Country.BELARUS);
    private String name;
    private Country country;

    Region(String name, Country country) {
        this.name = name;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public Country getCountry() {
        return country;
    }
}
