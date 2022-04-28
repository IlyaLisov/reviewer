package com.example.reviewer.model.report;

import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.sun.istack.NotNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class ReviewReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private ReviewReportType type;

    @NotNull
    @ManyToOne
    private EntityReview entityReview;

    @NotNull
    @ManyToOne
    private EmployeeReview employeeReview;

    private String text;

    public ReviewReportType getType() {
        return type;
    }

    public void setType(ReviewReportType type) {
        this.type = type;
    }

    public EntityReview getEntityReview() {
        return entityReview;
    }

    public void setEntityReview(EntityReview entityReview) {
        this.entityReview = entityReview;
    }

    public EmployeeReview getEmployeeReview() {
        return employeeReview;
    }

    public void setEmployeeReview(EmployeeReview employeeReview) {
        this.employeeReview = employeeReview;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
