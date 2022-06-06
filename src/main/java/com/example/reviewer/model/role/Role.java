package com.example.reviewer.model.role;

public enum Role {
    ANONYMOUS("Роль не выбрана"), STUDENT("Учащийся"), FORMER_STUDENT("Выпускник"), EMPLOYEE("Сотрудник"), FORMER_EMPLOYEE("Бывший сотрудник"),
    RESIDENT("Проживающий в общежитии"), FORMER_RESIDENT("Раньше проживал в общежитии");

    private String name;

    Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
