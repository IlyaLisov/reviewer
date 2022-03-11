package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EmployeeReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeReviewRepository employeeReviewRepository;

    @GetMapping("/{id}")
    public String id(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            List<EmployeeReview> reviews = (List<EmployeeReview>) employeeReviewRepository.findAll();
            model.addAttribute("employee", employee.get());
            model.addAttribute("imageURL", employee.get().getImageURL() == null ? "default.jpg" : employee.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .collect(Collectors.toList()));
        } else {
            return "error/404";
        }
        return "employee/employee";
    }
}
