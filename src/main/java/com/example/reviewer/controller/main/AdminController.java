package com.example.reviewer.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/account/admin")
public class AdminController {

    @GetMapping()
    public String index() {
        return "account/admin/index";
    }
}
