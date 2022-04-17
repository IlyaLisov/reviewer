package com.example.reviewer.model.review;

import com.example.reviewer.model.entity.Entity;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Formula;

import javax.persistence.ManyToOne;
import java.util.Objects;

@javax.persistence.Entity
public class EntityReview extends Review {
    @NotNull
    @Formula("(SELECT COUNT(*) FROM rdb.user_liked_entity_reviews r WHERE r.liked_entity_reviews_id = id)")
    private Integer rating;

    @NotNull
    @ManyToOne
    private Entity entity;

    public EntityReview() {
        this.rating = 0;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntityReview)) return false;
        EntityReview review = (EntityReview) o;
        return this.id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
