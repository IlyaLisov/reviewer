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
        entities.sort((e1, e2) -> (e2.getRating() != null && e1.getRating() != null) ? e2.getRating() - e1.getRating() : 0);
        model.addAttribute("entities", entities);

        List<Employee> employees = (List<Employee>) employeeRepository.findAll();
        employees.sort((e1, e2) -> (e2.getRating() != null && e1.getRating() != null) ? e2.getRating() - e1.getRating() : 0);
        model.addAttribute("employees", employees);

        return "rating/index";
    }
}
