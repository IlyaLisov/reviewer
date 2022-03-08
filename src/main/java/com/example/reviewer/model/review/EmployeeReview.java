package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Employee;

public class EmployeeReview extends Review {
    private Employee employee;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
