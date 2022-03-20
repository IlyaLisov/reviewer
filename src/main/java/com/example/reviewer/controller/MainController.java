package com.example.reviewer.controller;

import com.example.reviewer.model.feedback.Feedback;
import com.example.reviewer.model.feedback.FeedbackType;
import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import com.example.reviewer.model.user.UserRole;
import com.example.reviewer.repository.FeedbackRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;

@Controller
public class MainController extends com.example.reviewer.controller.Controller {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        if (model.getAttribute("user") != null) {
            return "redirect:account";
        }
        return "login";
    }

    @PostMapping("/login")
    public synchronized String doLogin(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession session, Model model) {
        Optional<User> userFromDataBase = userRepository.getByLogin(login);
        if (userFromDataBase.isPresent()) {
            String generatedPassword = Crypter.crypt(password, userFromDataBase.get().getRegisterDate().toString());
            if (generatedPassword.equals(userFromDataBase.get().getPassword())) {
                userFromDataBase.get().setLastSeenDate(LocalDate.now());
                userRepository.save(userFromDataBase.get());
                session.setAttribute("user", userFromDataBase.get());
            } else {
                model.addAttribute("error", "Проверьте правильность введенных данных.");
                model.addAttribute("login", login);
                return login(model);
            }
        } else {
            model.addAttribute("error", "Проверьте правильность введенных данных.");
            model.addAttribute("login", login);
            return login(model);
        }
        return "redirect:account";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (model.getAttribute("user") != null) {
            return "redirect:account";
        }
        return "register";
    }

    @PostMapping("/register")
    public synchronized String doRegister(@RequestParam("name") String name, @RequestParam("login") String login,
                                          @RequestParam("password") String password, @RequestParam("passwordConfirmation") String passwordConfirmation,
                                          HttpSession session, Model model) {
        if (password.equals(passwordConfirmation)) {
            Optional<User> userFromDatabase = userRepository.getByLogin(login);
            if (userFromDatabase.isPresent()) {
                model.addAttribute("error", "Пользователь с таким логином уже существует.");
                model.addAttribute("name", name);
                model.addAttribute("login", login);
                return register(model);
            } else {
                User user = new User();
                user.setName(name);
                user.setLogin(login);
                user.setUserRole(UserRole.USER);
                String generatedPassword = Crypter.crypt(password, user.getRegisterDate().toString());
                user.setPassword(generatedPassword);

                userRepository.save(user);
                session.setAttribute("user", user);
            }
        } else {
            model.addAttribute("error", "Введенные пароли не совпадают.");
            model.addAttribute("name", name);
            model.addAttribute("login", login);
            return register(model);
        }
        return "redirect:account";
    }

    @GetMapping("/feedback")
    public String feedback(Model model) {
        return "feedback";
    }

    @PostMapping("/feedback")
    public synchronized String doFeedback(@RequestParam("theme") String theme, @RequestParam("text") String text,
                                          Model model) {
        User user = (User) model.getAttribute("user");
        if (text.length() > MAX_FEEDBACK_LENGTH) {
            Feedback feedback = new Feedback();
            feedback.setFeedbackType(FeedbackType.valueOf(theme.toUpperCase()));
            feedback.setText(text);
            if (user != null) {
                feedback.setAuthor(user);
            }
            feedbackRepository.save(feedback);
            model.addAttribute("success", "Ваше сообщение успешно отправлено.");
        } else {
            model.addAttribute("error", "Сообщение не должно превышать 1024 символа.");
        }
        return feedback(model);
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        session.invalidate();
        model.addAttribute("success", "Вы вышли из аккаунта.");
        return "login";
    }
}
