package com.example.reviewer.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class MainController extends com.example.reviewer.controller.main.Controller {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(HttpSession session, Model model) {
        //doLogin
        return "account";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(HttpSession session, Model model) {
        //doregister
        return "account";
    }

    @GetMapping("/feedback")
    public String feedback() {
        return "feedback";
    }

    @PostMapping("/feedback")
    public String doFeedback(HttpSession session, Model model) {
        //doFeedback
        return "feedback";
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }
}
