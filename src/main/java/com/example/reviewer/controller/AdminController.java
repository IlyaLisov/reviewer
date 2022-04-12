package com.example.reviewer.controller;

import com.example.reviewer.model.entity.District;
import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.EmployeeType;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.entity.EntityType;
import com.example.reviewer.model.entity.Region;
import com.example.reviewer.model.feedback.Feedback;
import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.review.Review;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.role.RoleDocument;
import com.example.reviewer.model.role.RoleEntity;
import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import com.example.reviewer.model.user.UserRole;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/account/admin")
public class AdminController extends com.example.reviewer.controller.Controller {
    @GetMapping()
    public String index() {
        return "account/admin/index";
    }

    @GetMapping("/add-user")
    public String addUser(Model model) {
        return "account/admin/add-user";
    }

    @PostMapping("/add-user")
    public String doAddUser(@RequestParam("name") String name, @RequestParam("login") String login,
                            @RequestParam("password") String password, @RequestParam("passwordConfirmation") String passwordConfirmation,
                            Model model) {
        if (password.equals(passwordConfirmation)) {
            Optional<User> userFromDatabase = userRepository.getByLogin(login);
            if (userFromDatabase.isPresent()) {
                model.addAttribute("error", "Пользователь с таким логином уже существует.");
                model.addAttribute("name", name);
                model.addAttribute("login", login);
            } else {
                User user = new User();
                user.setName(name);
                user.setLogin(login);
                user.setUserRole(UserRole.USER);
                String generatedPassword = Crypter.crypt(password, user.getRegisterDate().toString());
                user.setPassword(generatedPassword);
                userRepository.save(user);
                model.addAttribute("success", "Пользователь " + login + " успешно создан.");
            }
        } else {
            model.addAttribute("error", "Введенные пароли не совпадают.");
            model.addAttribute("name", name);
            model.addAttribute("login", login);
        }
        return addUser(model);
    }

    @GetMapping("/edit-user")
    public String editUser(Model model) {
        return "account/admin/edit-user";
    }


    @PostMapping("/edit-user")
    public String doEditUser(@RequestParam("login") String login, Model model) {
        if (userRepository.existsByLogin(login)) {
            return "redirect:edit-user/" + login;
        } else {
            model.addAttribute("error", "Пользователь с таким логином не найден.");
            return editUser(model);
        }
    }

    @GetMapping("/edit-user/{login}")
    public String editSelectedUser(@PathVariable("login") String login, Model model) {
        model.addAttribute("userRoles", UserRole.values());
        Optional<User> user = userRepository.getByLogin(login);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            return "account/admin/edit-selected-user";
        } else {
            model.addAttribute("error", "Пользователь с таким логином не найден.");
            return editUser(model);
        }
    }

    @PostMapping("/edit-user/{login}")
    public String doEditSelectedUser(@PathVariable("login") String path, @RequestParam("name") String name,
                                     @RequestParam("login") String login, @RequestParam("password") String password,
                                     @RequestParam("passwordConfirmation") String passwordConfirmation,
                                     @RequestParam("userRole") String userRole, Model model) {
        Optional<User> userToBeEdited = userRepository.getByLogin(path);
        if (userToBeEdited.isPresent()) {
            User user = userToBeEdited.get();
            user.setName(name);
            if (password != null && passwordConfirmation != null && !password.isEmpty() && !passwordConfirmation.isEmpty() &&
                    password.equals(passwordConfirmation) && !user.getPassword().equals(Crypter.crypt(password, String.valueOf(user.getRegisterDate())))) {
                user.setPassword(Crypter.crypt(password, String.valueOf(user.getRegisterDate())));
            }
            user.setUserRole(UserRole.valueOf(userRole));
            userRepository.save(user);
            model.addAttribute("success", "Пользователь успешно обновлен.");
        }
        return editSelectedUser(path, model);
    }

    @GetMapping("/block-user")
    public String blockUser(Model model) {
        return "account/admin/block-user";
    }

    @PostMapping("/block-user")
    public String doBlockUser(@RequestParam("login") String login, Model model) {
        Optional<User> userToBeBlocked = userRepository.getByLogin(login);
        if (login.equals("admin")) {
            model.addAttribute("login", login);
            model.addAttribute("error", "У вас нет полномочий на данное действие.");
            return blockUser(model);
        }
        if (userToBeBlocked.isPresent()) {
            if (userToBeBlocked.get().getBlocked() != null && !userToBeBlocked.get().getBlocked()) {
                userToBeBlocked.get().setBlocked(true);
                updateReviewVisibility(userToBeBlocked.get(), false);
                model.addAttribute("success", "Пользователь " + userToBeBlocked.get().getName() + " заблокирован.");
            } else {
                userToBeBlocked.get().setBlocked(false);
                updateReviewVisibility(userToBeBlocked.get(), true);
                model.addAttribute("success", "Пользователь " + userToBeBlocked.get().getName() + " разблокирован.");
            }
            userRepository.save(userToBeBlocked.get());
        } else {
            model.addAttribute("login", login);
            model.addAttribute("error", "Пользователя с таким логином не существует.");
        }
        return blockUser(model);
    }

    private void updateReviewVisibility(User user, boolean status) {
        List<EntityReview> entityReviews = entityReviewRepository.findAllByAuthorId(user.getId());
        for(EntityReview review : entityReviews) {
            review.setVisible(status);
            entityReviewRepository.save(review);
        }
        List<EmployeeReview> employeeReviews = employeeReviewRepository.findAllByAuthorId(user.getId());
        for(EmployeeReview review : employeeReviews) {
            review.setVisible(status);
            employeeReviewRepository.save(review);
        }
    }

    @GetMapping("/add-entity")
    public String addEntity(Model model) {
        List<Entity> universities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entityTypes", EntityType.values());
        model.addAttribute("regions", Region.values());
        model.addAttribute("districts", District.values());
        model.addAttribute("parentEntities", universities.stream()
                .filter(e -> e.getType().equals(EntityType.UNIVERSITY) || e.getType().equals(EntityType.COLLEGE))
                .collect(Collectors.toList()));
        return "account/admin/add-entity";
    }

    @PostMapping("/add-entity")
    public String doAddEntity(@RequestParam("name") String name, @RequestParam("abbreviation") String abbreviation,
                              @RequestParam("type") String type, @RequestParam(value = "parentEntity", required = false) Long parentEntity,
                              @RequestParam("region") String region, @RequestParam("district") String district,
                              @RequestParam("address") String address, @RequestParam(value = "siteURL", required = false) String siteURL,
                              Model model) {
        Entity entity = new Entity();
        if (!entityRepository.findByName(name).isPresent()) {
            entity.setName(name);
            entity.setAbbreviation(abbreviation);
            entity.setType(EntityType.valueOf(type));
            if (type.equals("FACULTY") || type.equals("STRUCTURE_UNIT") || type.equals("HOSTEL")) {
                entity.setParentEntity(entityRepository.findById(parentEntity).get());
            }
            entity.setRegion(Region.valueOf(region));
            entity.setDistrict(District.valueOf(district));
            if (address != null && !address.isEmpty()) {
                entity.setAddress(address);
            }
            if (siteURL != null && !siteURL.isEmpty()) {
                entity.setSiteURL(siteURL);
            }
            User user = (User) model.getAttribute("user");
            user.upRating(RATING_FOR_CREATION_ENTITY);
            entity.setAuthor(user);

            userRepository.save(user);
            entityRepository.save(entity);
            model.addAttribute("success", "Учреждение образования успешно создано.");
        } else {
            model.addAttribute("error", "Учреждение образования с таким названием уже существует.");
            model.addAttribute("name", name);
            model.addAttribute("abbreviation", abbreviation);
        }
        return addEntity(model);
    }

    @GetMapping("/add-employee")
    public String addEmployee(Model model) {
        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entities", entities.stream()
                .sorted(Comparator.comparing(Entity::getName))
                .collect(Collectors.toList()));
        model.addAttribute("employeeTypes", EmployeeType.values());
        return "account/admin/add-employee";
    }

    @PostMapping("/add-employee")
    public String doAddEmployee(@RequestParam("name") String name, @RequestParam("type") String type,
                                @RequestParam(value = "entity", required = false) Long id, Model model) {
        Employee employee = new Employee();
        Optional<Entity> entity = entityRepository.findById(id);
        if (!employeeRepository.findByNameAndEntity(name, entity.get()).isPresent()) {
            employee.setName(name);
            employee.setType(EmployeeType.valueOf(type));
            employee.setEntity(entity.get());
            User user = (User) model.getAttribute("user");
            user.upRating(RATING_FOR_CREATION_EMPLOYEE);
            employee.setAuthor(user);

            userRepository.save(user);
            employeeRepository.save(employee);
            model.addAttribute("success", "Сотрудник успешно создан.");
        } else {
            model.addAttribute("error", "Сотрудник с таким именем в этом учреждении образования уже существует.");
        }
        model.addAttribute("name", name);
        model.addAttribute("type", type);
        model.addAttribute("entityId", id);
        return addEmployee(model);
    }

    @GetMapping("/verify")
    public String verify(Model model) {
        List<RoleDocument> roleDocuments = (List<RoleDocument>) roleDocumentRepository.findAll();
        model.addAttribute("roleDocuments", roleDocuments);
        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entities", entities);
        return "account/admin/verify";
    }

    @PostMapping("/verify/submit/{id}")
    public String verifySubmit(@PathVariable("id") Long id, @RequestParam("entity") Long entityId, Model model) {
        Optional<RoleDocument> roleDocument = roleDocumentRepository.findById(id);
        if (roleDocument.isPresent()) {
            User user = roleDocument.get().getUser();
            Entity entity = entityRepository.findById(entityId).get();

            user.addRole(roleDocument.get().getRole(), entity);
            if (entity.getParentEntity() != null) {
                user.addRole(roleDocument.get().getRole(), entity.getParentEntity());
            }
            userRepository.save(user);
            File file = new File(uploadPath + "/" + roleDocument.get().getPhotoId());
            file.delete();
            roleDocumentRepository.delete(roleDocument.get());
            model.addAttribute("success", "Роль была подтверждена.");
        }
        return verify(model);
    }

    @PostMapping("/verify/discard/{id}")
    public String verifyDiscard(@PathVariable("id") Long id, Model model) {
        Optional<RoleDocument> roleDocument = roleDocumentRepository.findById(id);
        if (roleDocument.isPresent()) {
            File file = new File(uploadPath + "/" + roleDocument.get().getPhotoId());
            file.delete();
            roleDocumentRepository.delete(roleDocument.get());
            model.addAttribute("success", "Роль была отклонена.");
        }
        return verify(model);
    }

    @GetMapping("/add-role-to-user")
    public String addRoleToUser(Model model) {
        model.addAttribute("roles", Role.values());
        return "account/admin/add-role-to-user";
    }

    @PostMapping("/add-role-to-user")
    public String doAddRoleToUser(@RequestParam("login") String login, @RequestParam("entityId") Long entityId,
                                  @RequestParam("role") String role, Model model) {
        Optional<User> user = userRepository.getByLogin(login);
        Optional<Entity> entity = entityRepository.findById(entityId);
        model.addAttribute("login", login);
        if (user.isPresent()) {
            if (entity.isPresent()) {
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setEntity(entity.get());
                roleEntity.setUser(user.get());
                roleEntity.setRole(Role.valueOf(role));
                if (user.get().getRoles().stream()
                        .anyMatch(r -> r.getUser().equals(user.get()) && r.getEntity().equals(entity.get()))) {
                    model.addAttribute("error", "У этого пользователя уже существует эта роль.");
                } else {
                    user.get().addRole(Role.valueOf(role), entity.get());
                    userRepository.save(user.get());
                    model.addAttribute("success", "Роль " + Role.valueOf(role).getName() + " в "
                            + entity.get().getName() + " успешно добавлена.");
                }
            } else {
                model.addAttribute("error", "Учреждение образования с таким id не найдено.");
            }
        } else {
            model.addAttribute("error", "Пользователь с таким логином не найден.");
        }
        return addRoleToUser(model);
    }

    @GetMapping("/feedback")
    public String feedback(Model model) {
        List<Feedback> unreadFeedback = feedbackRepository.findAllByIsRead(false);
        List<Feedback> readFeedback = feedbackRepository.findAllByIsRead(true);
        model.addAttribute("unreadFeedback", unreadFeedback);
        model.addAttribute("readFeedback", readFeedback);
        return "account/admin/feedback";
    }

    @PostMapping("/feedback/read/{id}")
    public String feedbackRead(@PathVariable("id") Long id, Model model) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isPresent()) {
            feedback.get().setRead(true);
            feedbackRepository.save(feedback.get());
        }
        return feedback(model);
    }

    @PostMapping("/feedback/unread/{id}")
    public String feedbackUnread(@PathVariable("id") Long id, Model model) {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isPresent()) {
            feedback.get().setRead(false);
            feedbackRepository.save(feedback.get());
        }
        return feedback(model);
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        return "account/admin/statistics";
    }
}
