package com.example.reviewer.model.report;

import com.example.reviewer.model.entity.Employee;
import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private EmployeeReportType type;

    @NotNull
    @ManyToOne
    private Employee employee;

    private String text;

    public EmployeeReportType getType() {
        return type;
    }

    public void setType(EmployeeReportType type) {
        this.type = type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
