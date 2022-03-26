package com.example.reviewer.controller;

import com.example.reviewer.model.user.User;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EmployeeReviewRepository;
import com.example.reviewer.repository.EntityRepository;
import com.example.reviewer.repository.EntityReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

public class Controller {
    protected static final int RATING_FOR_CREATION_ENTITY = 10;
    protected static final int RATING_FOR_CREATION_EMPLOYEE = 5;
    protected final static int MAX_REVIEW_TEXT_LENGTH = 1024;
    protected final static int RATING_FOR_LEFTING_REVIEW = 1;
    protected final static String uploadPath = "data/users/";
    protected final static String[] contentTypes = {"image/jpg", "image/png", "image/jpeg"};
    protected final static Long MAX_UPLOAD_SIZE = 8 * 1024 * 1024L; //8MB
    protected final static int MAX_FEEDBACK_LENGTH = 1024;
    protected final static int MAX_REVIEW_PER_ENTITY = 3;

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
        return employeeReviewRepository.count();
    }
}
