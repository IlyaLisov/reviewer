package com.example.reviewer.model.report;

public enum EmployeeReportType {
    INCORRECT("Устаревшая личная информация"), OUTDATED("Сотрудник больше не работает в данном УО"), HARMFUL("Данная публикация порочит честь сотрудника");

    private String name;

    EmployeeReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
