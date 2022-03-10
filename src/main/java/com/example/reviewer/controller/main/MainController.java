package com.example.reviewer.controller.main;

import com.example.reviewer.model.user.Crypter;
import com.example.reviewer.model.user.User;
import com.example.reviewer.model.user.UserRole;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

@Controller
public class MainController extends com.example.reviewer.controller.main.Controller {
    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        if(session.getAttribute("user") != null) {
            return "redirect:account";
        }
        return "login";
    }

    @PostMapping("/login")
    public synchronized String doLogin(@RequestParam("login") String login, @RequestParam("password") String password, HttpSession session, Model model) {
        Optional<User> userFromDataBase = userRepository.getByLogin(login);
        if(userFromDataBase.isPresent()) {
            String generatedPassword = Crypter.crypt(password, userFromDataBase.get().getRegisterDate().toString());
            if(generatedPassword.equals(userFromDataBase.get().getPassword())) {
                userFromDataBase.get().setLastSeenDate(LocalDate.now());
                userRepository.save(userFromDataBase.get());
                session.setAttribute("user", userFromDataBase.get());
            } else {
                model.addAttribute("error","Проверьте правильность введенных данных.");
                model.addAttribute("login", login);
                return "login";
            }
        } else {
            model.addAttribute("error", "Проверьте правильность введенных данных.");
            model.addAttribute("login", login);
            return "login";
        }
        return "redirect:account";
    }

    @GetMapping("/register")
    public String register(HttpSession session) {
        if(session.getAttribute("user") != null) {
            return "redirect:account";
        }
        return "register";
    }

    @PostMapping("/register")
    public synchronized String doRegister(@RequestParam("name") String name, @RequestParam("login") String login,
                             @RequestParam("password") String password, @RequestParam("passwordConfirmation") String passwordConfirmation,
                             HttpSession session, Model model) {
        if(password.equals(passwordConfirmation)) {
            Optional<User> userFromDatabase = userRepository.getByLogin(login);
            if(userFromDatabase.isPresent()) {
                model.addAttribute("error", "Пользователь с таким логином уже существует.");
                model.addAttribute("name", name);
                model.addAttribute("login", login);
                return "register";
            } else {
                User user = new User();
                user.setName(name);
                user.setLogin(login);
                user.setUserRole(UserRole.USER);
                ZonedDateTime zdtNow = ZonedDateTime.now(ZoneOffset.UTC);
                String generatedPassword = Crypter.crypt(password, user.getRegisterDate().toString());
                user.setPassword(generatedPassword);

                userRepository.save(user);
                session.setAttribute("user", user);
            }
        } else {
            model.addAttribute("error", "Введенные пароли не совпадают.");
            model.addAttribute("name", name);
            model.addAttribute("login", login);
            return "register";
        }
        return "redirect:account";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "feedback";
    }

    @PostMapping("/feedback")
    public synchronized String doFeedback(HttpSession session, Model model) {
        //doFeedback
        return "feedback";
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
