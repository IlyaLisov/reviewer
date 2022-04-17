package com.example.reviewer.model.report;

public enum EntityReportType {
    INCORRECT("Устаревшая информация"), HARMFUL("Данная публикация порочит честь учреждения образования");

    private String name;

    EntityReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
