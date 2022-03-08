package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Employee;
import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeReview extends Review {
    @NotNull
    @ManyToOne
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
