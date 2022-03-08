package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Entity;

public class EntityReview extends Review {
    private Entity entity;

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
