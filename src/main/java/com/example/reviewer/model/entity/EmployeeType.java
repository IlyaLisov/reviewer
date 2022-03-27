package com.example.reviewer.model.entity;

public enum EmployeeType {
    EMPLOYEE("Сотрудник"), TEACHER("Преподаватель"), SOCIAL_TEACHER("Социальный педагог"),
    VICE_DIRECTOR("Заместитель директора"), DIRECTOR("Директор"), VICE_DEAN("Заместитель декана"),
    DEAN("Декан"), VICE_RECTOR("Проректор"), RECTOR("Ректор"), HOSTEL_DIRECTOR("Заведующий общежитием");
    private String name;

    EmployeeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}