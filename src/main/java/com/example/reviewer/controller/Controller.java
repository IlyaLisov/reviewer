package com.example.reviewer.controller;

import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EmployeeReviewRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import com.example.reviewer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

public class Controller {
    @Autowired
    private EntityReviewRepository entityReviewRepository;

    @Autowired
    private EmployeeReviewRepository employeeReviewRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityRepository entityRepository;

    @ModelAttribute("user")
    public User getUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @ModelAttribute("entityAmount")
    public long getEntityAmount() {
        return entityRepository.count();
    }

    @ModelAttribute("employeeAmount")
    public long getEmployeeAmount() {
        return employeeRepository.count();
    }

    @ModelAttribute("entityReviewAmount")
    public long getEntityReviewAmount() {
        return entityReviewRepository.count();
    }

    @ModelAttribute("employeeReviewAmount")
    public long getEmployeeReviewAmount() {
        return entityReviewRepository.count();
    }
}
