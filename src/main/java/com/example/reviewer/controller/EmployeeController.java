package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.EmployeeType;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.report.EmployeeReport;
import com.example.reviewer.model.report.EmployeeReportType;
import com.example.reviewer.model.review.EmployeeReview;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employee")
public class EmployeeController extends com.example.reviewer.controller.Controller {
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
        if (employee.isPresent() && (employee.get().getVisible() || (user != null && (user.isModerator() || user.isAdmin())))) {
            List<EmployeeReview> reviews = employeeReviewRepository.findAllByEmployeeId(id);
            model.addAttribute("employee", employee.get());
            model.addAttribute("imageURL", employee.get().getImageURL() == null ? "default.jpg" : employee.get().getImageURL());
            model.addAttribute("reviews", reviews.stream()
                    .filter(markFilter)
                    .filter(review -> user != null && (user.isModerator() || user.isAdmin()) || review.getVisible())
                    .filter(review -> !review.getDeleted())
                    .filter(review -> review.getText() != null && !review.getText().isEmpty())
                    .collect(Collectors.toList()));
            model.addAttribute("roles", Role.values());
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
        if (employee.isPresent() && author != null) {
            Long reviewsFromUser = employeeReviewRepository.countAllByAuthorAndEmployeeAndIsDeleted(author, employee.get(), false);
            if (reviewsFromUser < MAX_REVIEW_PER_ENTITY) {
                review.setEmployee(employee.get());
                review.setAuthor(author);
                review.setMark(mark);
                if (!role.equals("ANONYMOUS")) {
                    review.setAuthorRole(Role.valueOf(role));
                }
                if (author.hasRole(employee.get().getEntity().getId())) {
                    review.setConfirmed(true);
                }
                if (!role.equals("ANONYMOUS") && !author.getRolesInEntity(id).contains(Role.valueOf(role))) {
                    author.addRole(Role.valueOf(role), employee.get().getEntity());
                }
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
            return "redirect:/rating";
        }
        return "employee/edit";
    }

    @PostMapping("/edit/{id}")
    public String doEdit(@PathVariable("id") Long id, @RequestParam("name") String name, @RequestParam("type") String type,
                         @RequestParam(value = "entity", required = false) Long entityId,
                         @RequestParam(value = "file", required = false) MultipartFile file, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            if (file != null && !file.isEmpty()) {
                if (Arrays.asList(contentTypes).contains(file.getContentType())) {
                    try {
                        if (file.getSize() > MAX_UPLOAD_SIZE) {
                            model.addAttribute("error", "Превышен допустимый размер файла.");
                        } else {
                            String uuid = String.valueOf(UUID.randomUUID());
                            File convertFile = new File(employeesPath + "/" + uuid + "." + file.getContentType().replace("image/", ""));
                            convertFile.createNewFile();
                            FileOutputStream fout = new FileOutputStream(convertFile);
                            fout.write(file.getBytes());
                            fout.close();
                            employee.get().setImageURL(uuid + "." + file.getContentType().replace("image/", ""));
                        }
                    } catch (IOException e) {
                        model.addAttribute("error", "Произошла ошибка при загрузке документа.");
                    }
                } else {
                    model.addAttribute("error", "Вы пытаетесь загрузить файл с неподходящим расширением." + file.getContentType());
                }
            }
            employee.get().setName(name);
            employee.get().setType(EmployeeType.valueOf(type));
            Optional<Entity> entity = entityRepository.findById(entityId);
            employee.get().setEntity(entity.get());
            employeeRepository.save(employee.get());
            model.addAttribute("success", "Сотрудник успешно обновлен.");
        }
        return edit(id, model);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
        } else {
            return "redirect:/rating";
        }
        return "employee/delete";
    }

    @PostMapping("/delete/{id}")
    public String doDelete(@PathVariable("id") Long id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if(employee.isPresent()) {
            User author = employee.get().getAuthor();
            if(author != null) {
                author.upRating(RATING_FOR_CREATION_EMPLOYEE);
                userRepository.save(author);
            }
            employeeRepository.delete(employee.get());
        }
        return "redirect:/rating";
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
                    if (author.isPresent()) {
                        author.get().upRating(-RATING_FOR_LEFTING_REVIEW);
                        userRepository.save(author.get());
                    }
                }
                review.get().setDeleted(true);
                employeeReviewRepository.save(review.get());
            }
        }
        return "redirect:" + request.getHeader("referer");
    }


    @GetMapping("/{id}/edit/{reviewId}")
    public String editReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId,
                             HttpServletRequest request, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(reviewId);
        User user = (User) model.getAttribute("user");
        if (review.isPresent() && (user != null && (user.isAdmin() || user.isModerator() || user.equals(review.get().getAuthor())))) {
            model.addAttribute("employee", review.get().getEmployee());
            model.addAttribute("review", review.get());
            model.addAttribute("roles", user.getRolesInEntity(review.get().getEmployee().getEntity().getId()));
            return "employee/edit-review";
        } else {
            return "redirect:" + request.getHeader("referer");
        }
    }

    @PostMapping("/{id}/edit/{reviewId}")
    public String doEditReview(@PathVariable("id") Long id, @PathVariable("reviewId") Long reviewId, @RequestParam("text") String text,
                               @RequestParam("mark") int mark, @RequestParam(value = "role", required = false) String role, Model model) {
        Optional<EmployeeReview> review = employeeReviewRepository.findById(reviewId);
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
            employeeReviewRepository.save(review.get());
            return "redirect:/employee/" + id;
        } else {
            return "redirect:/rating";
        }
    }

    @GetMapping("/report/{id}")
    public String report(@PathVariable("id") Long id, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            model.addAttribute("employee", employee.get());
            model.addAttribute("types", EmployeeReportType.values());
            model.addAttribute("imageURL", employee.get().getImageURL() == null ? "default.jpg" : employee.get().getImageURL());
            return "employee/report";
        } else {
            return "error/404";
        }
    }

    @PostMapping("/report/{id}")
    public String doReport(@PathVariable("id") Long id, @RequestParam("theme") String theme,
                           @RequestParam(value = "text", required = false) String text, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            EmployeeReport employeeReport = new EmployeeReport();
            employeeReport.setType(EmployeeReportType.valueOf(theme));
            employeeReport.setEmployee(employee.get());
            employeeReport.setText(text);
            employeeReportRepository.save(employeeReport);

            employee.get().increaseReportCounter();
            if (employee.get().getReportCounter() > AMOUNT_OF_EMPLOYEE_REPORTS_TO_HIDE && !employee.get().getVisible()) {
                employee.get().setVisible(false);
                List<EmployeeReview> reviews = employeeReviewRepository.findAllByEmployeeId(id);
                for (EmployeeReview review : reviews) {
                    review.setVisible(false);
                    employeeReviewRepository.save(review);
                }
            }
            employeeRepository.save(employee.get());

            model.addAttribute("success", "Ваша жалоба успешно принята. При наличии определенного количества жалоб, мы заблокируем данного сотрудника.");
            return report(id, model);
        } else {
            return "error/404";
        }
    }

    @GetMapping("/block/{id}")
    public String block(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (employee.isPresent()) {
            if (employee.get().getVisible()) {
                employee.get().setVisible(false);
                List<EmployeeReview> reviews = employeeReviewRepository.findAllByEmployeeId(id);
                for (EmployeeReview review : reviews) {
                    review.setVisible(false);
                    employeeReviewRepository.save(review);
                }
            } else {
                employee.get().setVisible(true);
                List<EmployeeReview> reviews = employeeReviewRepository.findAllByEmployeeId(id);
                for (EmployeeReview review : reviews) {
                    review.setVisible(true);
                    employeeReviewRepository.save(review);
                }
            }
            employeeRepository.save(employee.get());
        }
        return "redirect:" + request.getHeader("referer");
    }
}
