package com.example.reviewer.model.entity;

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

    @Formula("(SELECT COUNT(*) FROM rdb.review r WHERE r.employee_id = id)")
    private Integer reviewsAmount;

    @Formula("(SELECT COUNT(DISTINCT r.author_id) FROM rdb.review r WHERE r.employee_id = id)")
    private Integer peopleEnvolved;

    @Formula("(SELECT SUM(r.rating) FROM rdb.review r WHERE r.employee_id = id)")
    private Integer rating;

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getAverageRating() {
        return (peopleEnvolved != null && rating != null && peopleEnvolved != 0) ? String.format("%f.1", 1.0f * rating / peopleEnvolved) : "Нет оценок";
    }
}
