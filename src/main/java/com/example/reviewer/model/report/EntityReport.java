package com.example.reviewer.model.report;

import com.example.reviewer.model.entity.Entity;
import com.sun.istack.NotNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class EntityReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private EntityReportType type;

    @NotNull
    @ManyToOne
    private Entity entity;

    private String text;

    public EntityReportType getType() {
        return type;
    }

    public void setType(EntityReportType type) {
        this.type = type;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
