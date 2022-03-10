package com.example.reviewer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reviews")
public class ReviewsController extends com.example.reviewer.controller.Controller {

    @GetMapping()
    public String index() {
        return "reviews/index";
    }

}