package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.repository.EmployeeRepository;
import com.example.reviewer.repository.EntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rating")
public class RatingController extends com.example.reviewer.controller.Controller {
    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public String index(Model model) {
        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entities", entities.stream()
                .sorted((e1, e2) -> e2.getRating() - e1.getRating())
                .collect(Collectors.toList()));

        List<Employee> employees = (List<Employee>) employeeRepository.findAll();
        model.addAttribute("employees", employees.stream()
                .sorted((e1, e2) -> e2.getRating() - e1.getRating())
                .collect(Collectors.toList()));

        return "rating/index";
    }
}
