package com.example.reviewer.controller;

import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.review.Review;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.role.RoleDocument;
import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller of /account pages.
 * Only logged-in users have access to these pages cause of filter.
 */
@Controller
@RequestMapping(value = "/account")
public class AccountController extends com.example.reviewer.controller.Controller {

    /**
     * Servlet for index page /account.
     * Get user`s position in rating.
     *
     * @param model is storing required for view attributes.
     * @return view of index page /account.
     */
    @GetMapping()
    public String index(Model model) {
        User user = (User) model.getAttribute("user");

        List<User> users = (List<User>) userRepository.findAll();
        users.sort((u1, u2) -> u2.getRating() - u1.getRating());
        int positionInRating = users.indexOf(user);

        model.addAttribute("index", positionInRating + 1);
        return "account/index";
    }

    /**
     * Servlet for user roles page /account/roles.
     * Get all roles that user has.
     *
     * @param model is storing required for view attributes.
     * @return view of user roles page /account/roles.
     */
    @GetMapping("/roles")
    public String roles(Model model) {
        User user = (User) model.getAttribute("user");
        if (user != null) {
            model.addAttribute("roles", user.getRoles());
        }
        return "account/roles/index";
    }

    /**
     * Servlet for user add roles page /account/roles/add.
     * Get amount of document for roles user send.
     *
     * @param model is storing required for view attributes.
     * @return view of user add roles page /account/roles/add.
     */
    @GetMapping("/roles/add")
    public String rolesAdd(Model model) {
        User user = (User) model.getAttribute("user");
        int requestAmount = 0;
        requestAmount = roleDocumentRepository.countAllByUserId(user.getId());
        model.addAttribute("requestAmount", requestAmount);
        model.addAttribute("roles", Role.values());
        return "account/roles/add";
    }

    /**
     * Servlet for user add roles page /account/roles/add.
     * Save user documents and data to server.
     *
     * @param name  entity name of document that user provides.
     * @param role  name of role user has in current entity.
     * @param file  photo of document.
     * @param model is storing required for view attributes.
     * @return view of user add roles page /account/roles/add.
     */
    @PostMapping("/roles/add")
    public String doRolesAdd(@RequestParam("name") String name, @RequestParam("role") String role, @RequestParam("file") MultipartFile file,
                             Model model) {
        User user = (User) model.getAttribute("user");
        if (!file.isEmpty()) {
            if (Arrays.asList(CONTENT_TYPES).contains(file.getContentType())) {
                try {
                    if (file.getSize() > MAX_UPLOAD_SIZE) {
                        model.addAttribute("error", "Превышен допустимый размер файла.");
                    } else {
                        String uuid = String.valueOf(UUID.randomUUID());
                        File convertFile = new File(UPLOAD_PATH + "/" + uuid + "." + Objects.requireNonNull(file.getContentType()).replace("image/", ""));
                        if (convertFile.createNewFile()) {
                            FileOutputStream outputStream = new FileOutputStream(convertFile);
                            outputStream.write(file.getBytes());
                            outputStream.close();
                            RoleDocument document = new RoleDocument(name, Role.valueOf(role.toUpperCase()), user, uuid + "." + file.getContentType().replace("image/", ""));
                            roleDocumentRepository.save(document);
                            model.addAttribute("success", "Документ загружен успешно.");
                            model.addAttribute("name", name);
                        } else {
                            model.addAttribute("error", "Не удалось загрузить файл.");
                        }
                    }
                } catch (IOException e) {
                    model.addAttribute("error", "Произошла ошибка при загрузке документа.");
                    e.printStackTrace();
                }
            } else {
                model.addAttribute("error", "Вы пытаетесь загрузить файл с неподходящим расширением." + file.getContentType());
            }
        } else {
            model.addAttribute("error", "Вы пытаетесь загрузить пустой файл.");
        }
        return rolesAdd(model);
    }

    /**
     * Servlet of user reviews page /account/reviews.
     * Get reviews that user had left.
     *
     * @param model is storing required for view attributes.
     * @return view of user reviews page /account/reviews.
     */
    @GetMapping("/reviews")
    public String reviews(Model model) {
        User user = (User) model.getAttribute("user");
        List<EntityReview> entityReviews = entityReviewRepository.findAllByAuthorId(user.getId());
        model.addAttribute("entityReviews", entityReviews.stream()
                .filter(Review::getVisible)
                .filter(review -> !review.getDeleted())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        List<EmployeeReview> employeeReviews = employeeReviewRepository.findAllByAuthorId(user.getId());
        model.addAttribute("employeeReviews", employeeReviews.stream()
                .filter(Review::getVisible)
                .filter(review -> !review.getDeleted())
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));
        return "account/reviews";
    }

    /**
     * Servlet of user liked reviews page /account/bookmarks.
     * Get reviews user liked.
     *
     * @param model is storing required for view attributes.
     * @return view of user liked reviews page /account/bookmarks.
     */
    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        User user = (User) model.getAttribute("user");
        model.addAttribute("likedEntityReviews", user.getLikedEntityReviews());
        model.addAttribute("likedEmployeeReviews", user.getLikedEmployeeReviews());
        return "account/bookmarks";
    }

    @GetMapping("/settings")
    public String settings() {
        return "account/settings";
    }

    /**
     * Servlet of user settings page /account/settings.
     *
     * @param password             new password user want to set.
     * @param passwordConfirmation confirmation of new password.
     * @param model                is storing required for view attributes.
     * @return view of user settings page /account/settings.
     */
    @PostMapping("/settings")
    public String doSettings(@RequestParam("password") String password, @RequestParam("passwordConfirmation") String passwordConfirmation,
                             Model model) {
        User user = (User) model.getAttribute("user");
        if (password.equals(passwordConfirmation)) {
            String cryptedPassword = Crypter.crypt(password, user.getRegisterDate().toString());
            if (cryptedPassword.equals(user.getPassword())) {
                model.addAttribute("error", "Новый пароль совпадает со старым.");
            } else {
                user.setPassword(cryptedPassword);
                userRepository.save(user);
                model.addAttribute("success", "Пароль был успешно изменён.");
            }
        } else {
            model.addAttribute("error", "Пароли не совпадают.");
        }
        return settings();
    }
}
