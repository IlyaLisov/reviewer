package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.EmployeeType;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.SlangRemover;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EmployeeReviewRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController extends com.example.reviewer.controller.Controller {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeReviewRepository employeeReviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityRepository entityRepository;

    private SlangRemover slangRemover = SlangRemover.getInstance();

    @GetMapping("/{id}")
    public String id(@PathVariable("id") Long id, @RequestParam(value = "mark", required = false) Integer mark, Model model) {
        Predicate<EmployeeReview> markFilter = review -> {
            if (mark != null && mark != 0) {
                return review.getMark().equals(mark);
            } else {
                return true;
            }
        };

        Optional<Employee> employee = employeeRepository.findById(id);
        User user = (User) model.getAttribute("user");
        if (employee.isPresent()) {
            List<EmployeeReview> reviews = employeeReviewRepository.findAllByEmployeeId(id);
            model.addAttribute("employee", employee.get());
            model.addAttribute("imageURL", employee.get().getImageURL() == null ? "default.jpg" : employee.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(markFilter)
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .collect(Collectors.toList()));
            if (user != null) {
                model.addAttribute("roles", user.getRolesInEntity(employee.get().getEntity().getId()));
            }
        } else {
            return "error/404";
        }
        model.addAttribute("mark", mark);
        return "employee/employee";
    }

    @PostMapping("/left-review/{id}")
    public String leftReview(@PathVariable("id") Long id, @RequestParam(value = "text", required = false) String text,
                             @RequestParam("role") String role, @RequestParam("mark") int mark, Model model) {
        EmployeeReview review = new EmployeeReview();
        Optional<Employee> employee = employeeRepository.findById(id);
        User author = (User) model.getAttribute("user");
        if (employee.isPresent() && author != null && author.hasRole(employee.get().getEntity().getId())) {
            Long reviewsFromUser = employeeReviewRepository.countAllByAuthorAndEmployee(author, employee.get());
            if (reviewsFromUser < MAX_REVIEW_PER_ENTITY) {
                review.setEmployee(employee.get());
                review.setAuthor(author);
                review.setMark(mark);
                review.setAuthorRole(Role.valueOf(role));
                if (text != null) {
                    if (text.length() < MAX_REVIEW_TEXT_LENGTH) {
                        review.setText(slangRemover.removeSlang(text));
                    } else {
                        model.addAttribute("error", "Превышен максимальный размер отзыва.");
                    }
                }
                if (model.getAttribute("error") == null) {
                    author.upRating(RATING_FOR_LEFTING_REVIEW);
                    userRepository.save(author);
                    employeeReviewRepository.save(review);
                    model.addAttribute("success", "Ваш отзыв был опубликован.");
                }
            } else {
                model.addAttribute("error", "Вы можете оставить максимум " + MAX_REVIEW_PER_ENTITY + " отзывов на одного сотрудника.");
            }
        }
        return "redirect:/employee/" + id;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            model.addAttribute("types", EmployeeType.values());
            model.addAttribute("imageURL", employee.get().getImageURL() == null ? "default.jpg" : employee.get().getImageURL());
            List<Entity> entities = (List<Entity>) entityRepository.findAll();
            model.addAttribute("entities", entities);
        } else {
            return "redirect:rating";
        }
        return "employee/edit";
    }

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable("id") Long id, @RequestParam("name") String name, @RequestParam("type") String type,
                         @RequestParam(value = "entity", required = false) Long entityId, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            employee.get().setName(name);
            employee.get().setType(EmployeeType.valueOf(type));
            Optional<Entity> entity = entityRepository.findById(entityId);
            employee.get().setEntity(entity.get());
            employeeRepository.save(employee.get());
            model.addAttribute("success", "Сотрудник успешно обновлен.");
        }
        return edit(id, model);
    }

    @PostMapping("/{id}/like/{reviewId}")
    public String likeReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                             HttpServletRequest request, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && user != null) {
            if (!user.getLikedEmployeeReviews().contains(review.get())) {
                user.addLikedEmployeeReview(review.get());
            } else {
                user.removeLikedEmployeeReview(review.get());
            }
            userRepository.save(user);
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/{id}/delete/{reviewId}")
    public String deleteReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                               HttpServletRequest request, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && user != null) {
            if (user.isAdmin() || user.isModerator() || (review.get().getAuthor() != null && review.get().getAuthor().equals(user))) {
                if (review.get().getAuthor() != null) {
                    Optional<User> author = userRepository.findById(review.get().getAuthor().getId());
                    author.get().upRating(-RATING_FOR_LEFTING_REVIEW);
                    userRepository.save(author.get());
                }
                employeeReviewRepository.delete(review.get());
            }
        }
        return "redirect:" + request.getHeader("referer");
    }
}
