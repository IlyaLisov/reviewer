package com.example.reviewer.repository;

import com.example.reviewer.model.feedback.Feedback;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FeedbackRepository extends CrudRepository<Feedback, Long> {

    List<Feedback> findAllByIsRead(boolean isRead);
}
