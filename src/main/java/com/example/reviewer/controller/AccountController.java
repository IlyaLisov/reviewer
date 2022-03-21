package com.example.reviewer.controller;

import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.role.RoleDocument;
import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeReviewRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import com.example.reviewer.repository.RoleDocumentRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/account")
public class AccountController extends com.example.reviewer.controller.Controller {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleDocumentRepository roleDocumentRepository;

    @Autowired
    private EntityReviewRepository entityReviewRepository;

    @Autowired
    private EmployeeReviewRepository employeeReviewRepository;

    @GetMapping()
    public String index(HttpSession session, Model model) {
        User user = (User) model.getAttribute("user");
        List<User> users = (List<User>) userRepository.findAll();
        users.sort((u1, u2) -> u2.getRating() - u1.getRating());
        int positionInRating = users.indexOf(user);
        model.addAttribute("index", positionInRating + 1);
        return "account/index";
    }

    @GetMapping("/roles")
    public String roles(HttpSession session, Model model) {
        User user = (User) model.getAttribute("user");
        model.addAttribute("roles", user.getRoles());
        return "account/roles/index";
    }

    @GetMapping("/roles/add")
    public String rolesAdd(HttpSession session, Model model) {
        User user = (User) model.getAttribute("user");
        int requestAmount = roleDocumentRepository.countAllByUserId(user.getId());
        model.addAttribute("requestAmount", requestAmount);
        return "account/roles/add";
    }

    @PostMapping("/roles/add")
    public String doRolesAdd(@RequestParam("name") String name, @RequestParam("role") String role, @RequestParam("file") MultipartFile file,
                             HttpSession session, Model model) {
        User user = (User) model.getAttribute("user");
        if (!file.isEmpty()) {
            if (Arrays.asList(contentTypes).contains(file.getContentType())) {
                try {
                    if (file.getSize() > MAX_UPLOAD_SIZE) {
                        model.addAttribute("error", "Превышен допустимый размер файла.");
                    } else {
                        String uuid = String.valueOf(UUID.randomUUID());
                        File convertFile = new File(uploadPath + "/" + uuid + "." + file.getContentType().replace("image/", ""));
                        convertFile.createNewFile();
                        FileOutputStream fout = new FileOutputStream(convertFile);
                        fout.write(file.getBytes());
                        fout.close();
                        RoleDocument document = new RoleDocument(name, Role.valueOf(role.toUpperCase()), user, uuid + "." + file.getContentType().replace("image/", ""));
                        roleDocumentRepository.save(document);
                        model.addAttribute("success", "Документ загружен успешно.");
                        model.addAttribute("name", name);
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
        return rolesAdd(session, model);
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        User user = (User) model.getAttribute("user");
        List<EntityReview> entityReviews = entityReviewRepository.findAllByAuthorId(user.getId());
        model.addAttribute("entityReviews", entityReviews.stream()
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        List<EmployeeReview> employeeReviews = employeeReviewRepository.findAllByAuthorId(user.getId());
        model.addAttribute("employeeReviews", employeeReviews.stream()
                .sorted((e1, e2) -> e2.getReviewDate().compareTo(e1.getReviewDate()))
                .collect(Collectors.toList()));

        return "account/reviews";
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Model model) {
        User user = (User) model.getAttribute("user");
        if (user != null) {
            model.addAttribute("likedEntityReviews", user.getLikedEntityReviews());
            model.addAttribute("likedEmployeeReviews", user.getLikedEmployeeReviews());
        }
        return "account/bookmarks";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "account/settings";
    }

    @PostMapping("/settings")
    public String doSettings(@RequestParam(name = "password") String password, @RequestParam("passwordConfirmation") String passwordConfirmation,
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
        return settings(model);
    }
}
