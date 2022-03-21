package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Employee;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Formula;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeReview extends Review {
    @NotNull
    @Formula("(SELECT COUNT(*) FROM rdb.user_liked_employee_reviews r WHERE r.liked_employee_reviews_id = id)")
    private Integer rating;

    @NotNull
    @ManyToOne
    private Employee employee;

    public EmployeeReview() {
        this.rating = 0;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
