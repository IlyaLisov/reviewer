package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/entity")
public class EntityController extends com.example.reviewer.controller.Controller {
    private final static int MAX_REVIEW_TEXT_LENGTH = 1024;
    private final static int RATING_FOR_LEFTING_REVIEW = 1;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private EntityReviewRepository entityReviewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{id}")
    public String id(@PathVariable("id") Long id, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        User user = (User) model.getAttribute("user");
        if (entity.isPresent()) {
            List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
            List<Employee> employees = employeeRepository.findAllByEntityId(entity.get().getId());
            model.addAttribute("entity", entity.get());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .sorted((review1, review2) -> review2.getReviewDate().compareTo(review1.getReviewDate()))
                    .collect(Collectors.toList()));
            model.addAttribute("employees", employees.stream()
                    .sorted(Comparator.comparing(Employee::getName))
                    .collect(Collectors.toList()));
            if (user != null) {
                model.addAttribute("roles", user.getRolesInEntity(entity.get().getId()));
            }
        } else {
            return "error/404";
        }
        return "entity/entity";
    }

    @PostMapping("/left-review/{id}")
    public String leftReview(@PathVariable("id") Long id, @RequestParam(value = "text", required = false) String text,
                             @RequestParam("role") String role, @RequestParam("mark") int mark, Model model) {
        EntityReview review = new EntityReview();
        Optional<Entity> entity = entityRepository.findById(id);
        User author = (User) model.getAttribute("user");
        if (entity.isPresent() && author != null) {
            review.setEntity(entity.get());
            review.setAuthor(author);
            review.setMark(mark);
            review.setAuthorRole(Role.valueOf(role));
            if (text != null) {
                if (text.length() < MAX_REVIEW_TEXT_LENGTH) {
                    review.setText(text);
                } else {
                    model.addAttribute("error", "Превышен максимальный размер отзыва.");
                }
            }
            if (model.getAttribute("error") == null) {
                author.upRating(RATING_FOR_LEFTING_REVIEW);
                userRepository.save(author);
                entityReviewRepository.save(review);
                model.addAttribute("success", "Ваш отзыв был опубликован.");
            }
        }
        return id(id, model);
    }
}
