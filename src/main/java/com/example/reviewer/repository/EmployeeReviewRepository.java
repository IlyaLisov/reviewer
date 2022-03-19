package com.example.reviewer.repository;

import com.example.reviewer.model.review.EmployeeReview;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeReviewRepository extends CrudRepository<EmployeeReview, Long> {

    List<EmployeeReview> findAllByEmployeeId(Long id);

    List<EmployeeReview> findAllByAuthorId(Long id);

}
