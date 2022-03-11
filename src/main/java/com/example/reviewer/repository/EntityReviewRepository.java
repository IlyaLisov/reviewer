package com.example.reviewer.repository;

import com.example.reviewer.model.review.EntityReview;
import org.springframework.data.repository.CrudRepository;

public interface EntityReviewRepository extends CrudRepository<EntityReview, Long> {
}
