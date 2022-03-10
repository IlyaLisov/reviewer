package com.example.reviewer.repository;

import com.example.reviewer.model.feedback.Feedback;
import org.springframework.data.repository.CrudRepository;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {
}
