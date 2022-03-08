package com.example.reviewer.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/rating")
public class RatingController extends com.example.reviewer.controller.main.Controller {

    @GetMapping
    public String index() {
        return "rating/index";
    }
 }
