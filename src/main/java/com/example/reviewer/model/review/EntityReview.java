package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Entity;
import com.sun.istack.NotNull;

import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class EntityReview extends Review {
    @NotNull
    @ManyToOne
    private Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
