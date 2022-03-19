package com.example.reviewer.repository;

import com.example.reviewer.model.review.EntityReview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EntityReviewRepository extends CrudRepository<EntityReview, Long> {

    List<EntityReview> findAllByEntityId(Long id);

    List<EntityReview> findAllByAuthorId(Long id);
}
