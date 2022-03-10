package com.example.reviewer.controller;

import com.example.reviewer.model.user.User;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

public class Controller {

    @ModelAttribute("user")
    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
}
