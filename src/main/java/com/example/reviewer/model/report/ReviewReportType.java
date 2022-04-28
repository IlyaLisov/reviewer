package com.example.reviewer.model.report;

public enum ReviewReportType {
    OUTDATED("Данный отзыв не относится к этому сотруднику или учреждению образования"), HARMFUL("Данный отзыв порочит честь сотрудника или учреждения образования");

    private String name;

    ReviewReportType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
