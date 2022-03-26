package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntityReviewRepository extends CrudRepository<EntityReview, Long> {

    List<EntityReview> findAllByEntityId(Long id);

    List<EntityReview> findAllByAuthorId(Long id);

    Long countAllByAuthorAndEntity(User author, Entity entity);
}
