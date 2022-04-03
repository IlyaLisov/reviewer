package com.example.reviewer.controller;

import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reviews")
public class ReviewsController extends com.example.reviewer.controller.Controller {
    @GetMapping()
    public String index(Model model) {
        List<EntityReview> entityReviews = (List<EntityReview>) entityReviewRepository.findAll();
        model.addAttribute("entityReviews", entityReviews.stream()
                .filter(review -> !review.getText().isEmpty())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        List<EmployeeReview> employeeReviews = (List<EmployeeReview>) employeeReviewRepository.findAll();
        model.addAttribute("employeeReviews", employeeReviews.stream()
                .filter(review -> !review.getText().isEmpty())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        return "reviews/index";
    }

}
