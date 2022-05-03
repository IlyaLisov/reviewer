package com.example.reviewer.model.entity;

import com.example.reviewer.model.user.User;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Formula;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EmployeeType type;

    @NotNull
    private String name;

    @NotNull
    @ManyToOne
    private Entity entity;

    @NotNull
    @ManyToOne
    private User author;

    private String imageURL;

    @Formula("(SELECT COUNT(*) FROM rdb.review r WHERE r.employee_id = id AND r.is_deleted != 1)")
    private Integer reviewsAmount;

    @Formula("(SELECT COUNT(DISTINCT r.author_id) FROM rdb.review r WHERE r.employee_id = id AND r.is_deleted != 1)")
    private Integer peopleInvolved;

    @Formula("(SELECT SUM(r.mark) FROM rdb.review r WHERE r.employee_id = id AND r.is_deleted != 1)")
    private Integer rating;

    @NotNull
    private Boolean isVisible;

    @NotNull
    private Integer reportCounter;

    public Employee() {
        this.imageURL = "default.jpg";
        this.isVisible = true;
        this.reportCounter = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType employeeType) {
        this.type = employeeType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getReviewsAmount() {
        return reviewsAmount;
    }

    public void setReviewsAmount(Integer reviewsAmount) {
        this.reviewsAmount = reviewsAmount;
    }

    public Integer getPeopleInvolved() {
        return peopleInvolved;
    }

    public void setPeopleInvolved(Integer peopleInvolved) {
        this.peopleInvolved = peopleInvolved;
    }

    public Integer getRating() {
        return rating != null ? rating : 0;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getAverageRating() {
        return (reviewsAmount != null && rating != null && reviewsAmount != 0) ? String.format("%.1f", 1.0f * rating / reviewsAmount) : "Нет оценок";
    }

    public Boolean getVisible() {
        return isVisible != null ? isVisible : true;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Integer getReportCounter() {
        return reportCounter;
    }

    public void setReportCounter(Integer reportCounter) {
        this.reportCounter = reportCounter;
    }

    public void increaseReportCounter() {
        if (reportCounter != null) {
            reportCounter++;
        } else {
            reportCounter = 1;
        }
    }
}
