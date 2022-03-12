package com.example.reviewer.controller;

import com.example.reviewer.model.entity.District;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.entity.EntityType;
import com.example.reviewer.model.entity.Region;
import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import com.example.reviewer.model.user.UserRole;
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

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/account/admin")
public class AdminController {
    private static final int RATING_FOR_CREATION_ENTITY = 10;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EntityRepository entityRepository;

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
            if (!user.getName().equals(name)) {
                user.setName(name);
            }
            if (password != null && passwordConfirmation != null && !password.isEmpty() && !passwordConfirmation.isEmpty() &&
                    password.equals(passwordConfirmation) && !user.getPassword().equals(Crypter.crypt(password, String.valueOf(user.getRegisterDate())))) {
                user.setPassword(Crypter.crypt(password, String.valueOf(user.getRegisterDate())));
            }
            if (!user.getUserRole().name().equals(userRole)) {
                user.setUserRole(UserRole.valueOf(userRole));
            }
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
            userRepository.delete(userToBeBlocked.get());
            model.addAttribute("success", "Пользователь " + userToBeBlocked.get().getName() + " заблокирован.");
        } else {
            model.addAttribute("login", login);
            model.addAttribute("error", "Пользователя с таким логином не существует.");
        }
        return blockUser(model);
    }

    @GetMapping("/add-entity")
    public String addEntity(Model model) {
        List<Entity> universities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entityTypes", EntityType.values());
        model.addAttribute("regions", Region.values());
        model.addAttribute("districts", District.values());
        model.addAttribute("parentEntities", universities.stream()
                .filter(e -> e.getType().equals(EntityType.UNIVERSITY))
                .collect(Collectors.toList()));
        return "account/admin/add-entity";
    }

    @PostMapping("/add-entity")
    public String doAddEntity(@RequestParam("name") String name, @RequestParam("type") String type,
                              @RequestParam(value = "parentEntity", required = false) Long entityId, @RequestParam("region") String region,
                              @RequestParam("district") String district, @RequestParam("address") String address,
                              @RequestParam("siteURL") String siteURL, HttpSession session, Model model) {
        Entity entity = new Entity();
        if (!entityRepository.findByName(name).isPresent()) {
            entity.setName(name);
            entity.setType(EntityType.valueOf(type));
            if (type.equals("FACULTY")) {
                entity.setParentEntity(entityRepository.findById(entityId).get());
            }
            entity.setRegion(Region.valueOf(region));
            entity.setDistrict(District.valueOf(district));
            if (address != null && !address.isEmpty()) {
                entity.setAddress(address);
            }
            if (siteURL != null && !siteURL.isEmpty()) {
                entity.setSiteURL(siteURL);
            }
            User user = (User) session.getAttribute("user");
            user.upRating(RATING_FOR_CREATION_ENTITY);
            entity.setAuthor(user);

            userRepository.save(user);
            entityRepository.save(entity);
            model.addAttribute("success", "Учреждение образования успешно создано.");
        } else {
            model.addAttribute("error", "Учреждение образования с таким названием уже существует.");
        }
        return addEntity(model);
    }
}
