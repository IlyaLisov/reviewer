package com.example.reviewer.controller;

import com.example.reviewer.model.report.ReviewReport;
import com.example.reviewer.model.report.ReviewReportType;
import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.review.Review;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reviews")
public class ReviewsController extends com.example.reviewer.controller.Controller {
    @GetMapping()
    public String index(Model model) {
        List<EntityReview> entityReviews = (List<EntityReview>) entityReviewRepository.findAll();
        model.addAttribute("entityReviews", entityReviews.stream()
                .filter(review -> !review.getText().isEmpty())
                .filter(Review::getVisible)
                .filter(review -> !review.getDeleted())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        List<EmployeeReview> employeeReviews = (List<EmployeeReview>) employeeReviewRepository.findAll();
        model.addAttribute("employeeReviews", employeeReviews.stream()
                .filter(review -> !review.getText().isEmpty())
                .filter(Review::getVisible)
                .filter(review -> !review.getDeleted())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        return "reviews/index";
    }

    @GetMapping("/report/entity-review/{id}")
    public String entityReport(@PathVariable("id") Long id, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(id);
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            model.addAttribute("types", ReviewReportType.values());
            return "reviews/report-entity-review";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/report/entity-review/{id}")
    public String doEntityReport(@PathVariable("id") Long id, @RequestParam("theme") String theme,
                                 @RequestParam(value = "text", required = false) String text, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(id);
        if (review.isPresent()) {
            ReviewReport report = new ReviewReport();
            report.setType(ReviewReportType.valueOf(theme));
            report.setEntityReview(review.get());
            report.setText(text);
            reviewReportRepository.save(report);

            review.get().increaseReportCounter();
            if (review.get().getReportCounter() > AMOUNT_OF_REVIEW_REPORTS_TO_HIDE && review.get().getVisible()) {
                review.get().setVisible(false);
            }
            entityReviewRepository.save(review.get());

            model.addAttribute("success", "Ваша жалоба успешно принята. При наличии определенного количества жалоб, мы заблокируем данный отзыв.");
            return entityReport(id, model);
        } else {
            return "error/404";
        }
    }

    @GetMapping("/report/entity-review/block/{id}")
    public String blockEntityReview(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<EntityReview> review = entityReviewRepository.findById(id);
        if (review.isPresent()) {
            review.get().setVisible(!review.get().getVisible());
            entityReviewRepository.save(review.get());
        }
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/report/employee-review/{id}")
    public String employeeReport(@PathVariable("id") Long id, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(id);
        if (review.isPresent()) {
            model.addAttribute("review", review.get());
            model.addAttribute("types", ReviewReportType.values());
            return "reviews/report-employee-review";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/report/employee-review/{id}")
    public String doEmployeeReport(@PathVariable("id") Long id, @RequestParam("theme") String theme,
                                   @RequestParam(value = "text", required = false) String text, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(id);
        if (review.isPresent()) {
            ReviewReport report = new ReviewReport();
            report.setType(ReviewReportType.valueOf(theme));
            report.setEmployeeReview(review.get());
            report.setText(text);
            reviewReportRepository.save(report);

            review.get().increaseReportCounter();
            if (review.get().getReportCounter() > AMOUNT_OF_REVIEW_REPORTS_TO_HIDE && review.get().getVisible()) {
                review.get().setVisible(false);
            }
            employeeReviewRepository.save(review.get());

            model.addAttribute("success", "Ваша жалоба успешно принята. При наличии определенного количества жалоб, мы заблокируем данный отзыв.");
            return employeeReport(id, model);
        } else {
            return "error/404";
        }
    }

    @GetMapping("/report/employee-review/block/{id}")
    public String blockEmployeeReview(@PathVariable("id") Long id, HttpServletRequest request) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(id);
        if (review.isPresent()) {
            review.get().setVisible(!review.get().getVisible());
            employeeReviewRepository.save(review.get());
        }
        return "redirect:" + request.getHeader("referer");
    }
}
