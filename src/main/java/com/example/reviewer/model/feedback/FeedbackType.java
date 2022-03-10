package com.example.reviewer.model.feedback;

public enum FeedbackType {
    BUG("Баг"), INCORRECT("Устаревшая или некорректная информация"), WISH("Рекомендация"), NEW("Новое учреждение образования или сотрудник");

    private String name;

    FeedbackType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
