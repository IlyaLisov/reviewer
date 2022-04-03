package com.example.reviewer.controller;

import com.example.reviewer.model.entity.District;
import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.entity.EntityType;
import com.example.reviewer.model.entity.Region;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.review.SlangRemover;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/entity")
public class EntityController extends com.example.reviewer.controller.Controller {
    private SlangRemover slangRemover = SlangRemover.getInstance();

    @GetMapping("/{id}")
    public String id(@PathVariable("id") Long id, @RequestParam(value = "mark", required = false) Integer mark, Model model) {
        Predicate<EntityReview> markFilter = review -> {
            if (mark != null && mark != 0) {
                return review.getMark().equals(mark);
            } else {
                return true;
            }
        };

        Optional<Entity> entity = entityRepository.findById(id);
        User user = (User) model.getAttribute("user");
        if (entity.isPresent()) {
            List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
            List<Employee> employees = employeeRepository.findAllByEntityId(entity.get().getId());
            model.addAttribute("entity", entity.get());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(markFilter)
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .sorted((review1, review2) -> review2.getReviewDate().compareTo(review1.getReviewDate()))
                    .collect(Collectors.toList()));
            model.addAttribute("employees", employees.stream()
                    .sorted(Comparator.comparing(Employee::getName))
                    .collect(Collectors.toList()));
            if (user != null) {
                model.addAttribute("roles", user.getRolesInEntity(entity.get().getId()));
            }
            List<Entity> childEntities = entityRepository.findByParentEntity(entity.get());
            model.addAttribute("childEntities", childEntities.stream()
                    .sorted(Comparator.comparing(Entity::getName))
                    .collect(Collectors.toList()));
        } else {
            return "error/404";
        }
        model.addAttribute("mark", mark);
        return "entity/entity";
    }

    @PostMapping("/left-review/{id}")
    public String leftReview(@PathVariable("id") Long id, @RequestParam(value = "text", required = false) String text,
                             @RequestParam("role") String role, @RequestParam("mark") int mark, Model model) {
        ;
        EntityReview review = new EntityReview();
        Optional<Entity> entity = entityRepository.findById(id);
        User author = (User) model.getAttribute("user");
        if (entity.isPresent() && author != null && author.hasRole(entity.get().getId())) {
            Long reviewsFromUser = entityReviewRepository.countAllByAuthorAndEntity(author, entity.get());
            if (reviewsFromUser < MAX_REVIEW_PER_ENTITY) {
                review.setEntity(entity.get());
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
                    entityReviewRepository.save(review);
                    model.addAttribute("success", "Ваш отзыв был опубликован.");
                }
            } else {
                model.addAttribute("error", "Вы можете оставить максимум " + MAX_REVIEW_PER_ENTITY + " отзывов на одно учреждение образования.");
            }
        }
        return "redirect:/entity/" + id;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            model.addAttribute("entity", entity.get());
            model.addAttribute("types", EntityType.values());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            model.addAttribute("regions", Region.values());
            model.addAttribute("districts", District.values());
            List<Entity> universities = (List<Entity>) entityRepository.findAll();
            model.addAttribute("parentEntities", universities.stream()
                    .filter(e -> e.getType().equals(EntityType.UNIVERSITY) || e.getType().equals(EntityType.COLLEGE))
                    .collect(Collectors.toList()));
        } else {
            return "redirect:/rating";
        }
        return "entity/edit";
    }

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable("id") Long id, @RequestParam("name") String name, @RequestParam("abbreviation") String abbreviation,
                         @RequestParam("type") String type, @RequestParam(value = "parentEntity", required = false) Long parentEntityId,
                         @RequestParam("region") String region, @RequestParam("district") String district,
                         @RequestParam("address") String address, @RequestParam(value = "siteURL", required = false) String siteURL,
                         Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            entity.get().setName(name);
            entity.get().setAbbreviation(abbreviation);
            entity.get().setType(EntityType.valueOf(type));
            if (parentEntityId == 0) {
                entity.get().setParentEntity(null);
            } else {
                Optional<Entity> parentEntity = entityRepository.findById(parentEntityId);
                entity.get().setParentEntity(parentEntity.get());
            }
            entity.get().setRegion(Region.valueOf(region));
            entity.get().setDistrict(District.valueOf(district));
            entity.get().setAddress(address);
            entity.get().setSiteURL(siteURL);
            entityRepository.save(entity.get());
            model.addAttribute("success", "Учреждение образования успешно обновлено.");
        }
        return edit(id, model);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            model.addAttribute("entity", entity.get());
        } else {
            return "redirect:/rating";
        }
        return "entity/delete";
    }

    @PostMapping("/delete/{id}")
    public String doDelete(@PathVariable("id") Long id) {
        Optional<Entity> entity = entityRepository.findById(id);
        if(entity.isPresent()) {
            User author = entity.get().getAuthor();
            if(author != null) {
                author.upRating(-RATING_FOR_CREATION_ENTITY);
                userRepository.save(author);
            }
            entityRepository.delete(entity.get());
        }
        return "redirect:/rating";
    }

    @PostMapping("/{id}/like/{reviewId}")
    public String likeReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                             HttpServletRequest request, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && user != null) {
            if (!user.getLikedEntityReviews().contains(review.get())) {
                user.addLikedEntityReview(review.get());
            } else {
                user.removeLikedEntityReview(review.get());
            }
            userRepository.save(user);
        }
        return "redirect:" + request.getHeader("referer");
    }

    @PostMapping("/{id}/delete/{reviewId}")
    public String deleteReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                               HttpServletRequest request, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && user != null) {
            if (user.isAdmin() || user.isModerator() || (review.get().getAuthor() != null && review.get().getAuthor().equals(user))) {
                if (review.get().getAuthor() != null) {
                    Optional<User> author = userRepository.findById(review.get().getAuthor().getId());
                    author.get().upRating(-RATING_FOR_LEFTING_REVIEW);
                    userRepository.save(author.get());
                }
                entityReviewRepository.delete(review.get());
            }
        }
        return "redirect:" + request.getHeader("referer");
    }
}
