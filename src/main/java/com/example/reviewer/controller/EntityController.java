package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/entity")
public class EntityController extends com.example.reviewer.controller.Controller {
    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private EntityReviewRepository entityReviewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/{id}")
    public String id(@PathVariable("id") Long id, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
            List<Employee> employees = employeeRepository.findAllByEntityId(entity.get().getId());
            model.addAttribute("entity", entity.get());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .collect(Collectors.toList()));
            model.addAttribute("employees", employees.stream()
                    .sorted(Comparator.comparing(Employee::getName))
                    .collect(Collectors.toList()));
        } else {
            return "error/404";
        }
        return "entity/entity";
    }
}
