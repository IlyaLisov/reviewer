package com.example.reviewer.controller;

import com.example.reviewer.model.entity.District;
import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.entity.EntityType;
import com.example.reviewer.model.entity.Region;
import com.example.reviewer.model.report.EntityReport;
import com.example.reviewer.model.report.EntityReportType;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        if (entity.isPresent() && (entity.get().getVisible() || (user != null && (user.isModerator() || user.isAdmin())))) {
            List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
            List<Employee> employees = employeeRepository.findAllByEntityId(entity.get().getId());
            model.addAttribute("entity", entity.get());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(markFilter)
                    .filter(review -> user != null && (user.isModerator() || user.isAdmin()) || review.getVisible())
                    .filter(review -> !review.getDeleted())
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .sorted((review1, review2) -> review2.getReviewDate().compareTo(review1.getReviewDate()))
                    .collect(Collectors.toList()));
            model.addAttribute("employees", employees.stream()
                    .filter(Employee::getVisible)
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
                         @RequestParam(value = "file", required = false) MultipartFile file, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            if (file != null && !file.isEmpty()) {
                if (Arrays.asList(contentTypes).contains(file.getContentType())) {
                    try {
                        if (file.getSize() > MAX_UPLOAD_SIZE) {
                            model.addAttribute("error", "Превышен допустимый размер файла.");
                        } else {
                            String uuid = String.valueOf(UUID.randomUUID());
                            File convertFile = new File(entitiesPath + "/" + uuid + "." + file.getContentType().replace("image/", ""));
                            convertFile.createNewFile();
                            FileOutputStream fout = new FileOutputStream(convertFile);
                            fout.write(file.getBytes());
                            fout.close();
                            entity.get().setImageURL(uuid + "." + file.getContentType().replace("image/", ""));
                        }
                    } catch (IOException e) {
                        model.addAttribute("error", "Произошла ошибка при загрузке документа.");
                    }
                } else {
                    model.addAttribute("error", "Вы пытаетесь загрузить файл с неподходящим расширением." + file.getContentType());
                }
            }
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
                    if (author.isPresent()) {
                        author.get().upRating(-RATING_FOR_LEFTING_REVIEW);
                        userRepository.save(author.get());
                    }
                }
                review.get().setDeleted(true);
                entityReviewRepository.save(review.get());
            }
        }
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/{id}/edit/{reviewId}")
    public String editReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                             HttpServletRequest request, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && (user != null && (user.isAdmin() || user.isModerator() || user.equals(review.get().getAuthor())))) {
            model.addAttribute("entity", review.get().getEntity());
            model.addAttribute("review", review.get());
            model.addAttribute("roles", user.getRolesInEntity(id));
            return "entity/edit-review";
        } else {
            return "redirect:" + request.getHeader("referer");
        }
    }

    @PostMapping("/{id}/edit/{reviewId}")
    public String doEditReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId, @RequestParam("text") String text,
                               @RequestParam("mark") int mark, @RequestParam(value = "role", required = false) String role, Model model) {
        Optional<EntityReview> review = entityReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && (user != null && (user.isAdmin() || user.isModerator() || user.equals(review.get().getAuthor())))) {
            if (!review.get().getText().equals(text)) {
                if(user.isAdmin() || user.isModerator()) {
                    review.get().setText(text);
                } else {
                    review.get().setText(SlangRemover.removeSlang(text));
                }
                review.get().setEdited(true);
            }
            if (review.get().getMark() != mark) {
                review.get().setMark(mark);
                review.get().setEdited(true);
            }
            if (role != null && !review.get().getAuthorRole().equals(Role.valueOf(role))) {
                review.get().setAuthorRole(Role.valueOf(role));
                review.get().setEdited(true);
            }
            entityReviewRepository.save(review.get());
            return "redirect:/entity/" + id;
        } else {
            return "redirect:/rating";
        }
    }

    @GetMapping("/report/{id}")
    public String report(@PathVariable("id") Long id, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            model.addAttribute("entity", entity.get());
            model.addAttribute("types", EntityReportType.values());
            model.addAttribute("imageURL", entity.get().getImageURL() == null ? "default.png" : entity.get().getImageURL());
            return "entity/report";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/report/{id}")
    public String doReport(@PathVariable("id") Long id, @RequestParam("theme") String theme,
                           @RequestParam(value = "text", required = false) String text, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            EntityReport entityReport = new EntityReport();
            entityReport.setType(EntityReportType.valueOf(theme));
            entityReport.setEntity(entity.get());
            entityReport.setText(text);
            entityReportRepository.save(entityReport);

            entity.get().increaseReportCounter();
            if (entity.get().getReportCounter() > AMOUNT_OF_ENTITY_REPORTS_TO_HIDE && entity.get().getVisible()) {
                entity.get().setVisible(false);
                List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
                for (EntityReview review : reviews) {
                    review.setVisible(false);
                    entityReviewRepository.save(review);
                }
            }
            entityRepository.save(entity.get());

            model.addAttribute("success", "Ваша жалоба успешно принята. При наличии определенного количества жалоб, мы заблокируем данное учреждение образования.");
            return report(id, model);
        } else {
            return "error/404";
        }
    }

    @GetMapping("/block/{id}")
    public String block(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        Optional<Entity> entity = entityRepository.findById(id);
        if (entity.isPresent()) {
            if (entity.get().getVisible()) {
                entity.get().setVisible(false);
                List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
                for (EntityReview review : reviews) {
                    review.setVisible(false);
                    entityReviewRepository.save(review);
                }
            } else {
                entity.get().setVisible(true);
                List<EntityReview> reviews = entityReviewRepository.findAllByEntityId(id);
                for (EntityReview review : reviews) {
                    review.setVisible(true);
                    entityReviewRepository.save(review);
                }
            }
            entityRepository.save(entity.get());
        }
        return "redirect:" + request.getHeader("referer");
    }
}
