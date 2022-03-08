package com.example.reviewer.controller.main;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/account")
public class AccountController {

    @GetMapping()
    public String index() {
        return "account/index";
    }

    @GetMapping("/roles")
    public String roles() {
        return "account/roles/index";
    }

    @GetMapping("/roles/add")
    public String rolesAdd() {
        return "account/roles/add";
    }

    @PostMapping("/roles/add")
    public String doRolesAdd() {
        //do roles add
        return "account/roles/add";
    }

    @GetMapping("/reviews")
    public String reviews() {
        return "account/reviews";
    }

    @GetMapping("/bookmarks")
    public String bookmarks() {
        return "account/bookmarks";
    }

    @GetMapping("/settings")
    public String settings() {
        return "account/settings";
    }

    @PostMapping("/settings")
    public String doSettings() {
        //do settings
        return "account/settings";
    }
}
