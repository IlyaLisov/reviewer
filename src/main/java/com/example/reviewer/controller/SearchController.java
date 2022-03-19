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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
public class SearchController extends com.example.reviewer.controller.MainController {
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EntityRepository entityRepository;

    @GetMapping("/entities")
    public String education(@RequestParam("query") String query, Model model) {
        Comparator<Entity> comparator = (entity1, entity2) -> {
            if (!query.isEmpty()) {
                String[] wordsInQuery = query.split(" ");
                return (int) (Arrays.stream(wordsInQuery).filter(word -> entity2.getName().toLowerCase().contains(word.toLowerCase())).count()
                        - Arrays.stream(wordsInQuery).filter(word -> entity1.getName().toLowerCase().contains(word.toLowerCase())).count());
            } else {
                return entity1.getId().compareTo(entity2.getId());
            }
        };

        String[] wordsInQuery = query.split(" ");
        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entities", entities.stream()
                .filter(entity -> Arrays.stream(wordsInQuery)
                        .anyMatch(word -> entity.getName().toLowerCase().contains(word.toLowerCase())))
                .sorted(comparator)
                .collect(Collectors.toList()));

        return "search/entities";
    }

    @GetMapping("/employees")
    public String employees(@RequestParam("query") String query, Model model) {
        Comparator<Employee> comparator = (employee1, employee2) -> {
            if (!query.isEmpty()) {
                String[] wordsInQuery = query.split(" ");
                return (int) (Arrays.stream(wordsInQuery).filter(word -> employee2.getName().toLowerCase().contains(word.toLowerCase())).count()
                        - Arrays.stream(wordsInQuery).filter(word -> employee1.getName().toLowerCase().contains(word.toLowerCase())).count());
            } else {
                return employee1.getId().compareTo(employee2.getId());
            }
        };

        String[] wordsInQuery = query.split(" ");
        List<Employee> employees = (List<Employee>) employeeRepository.findAll();
        model.addAttribute("employees", employees.stream()
                .filter(employee -> Arrays.stream(wordsInQuery)
                        .anyMatch(word -> employee.getName().toLowerCase().contains(word.toLowerCase())))
                .sorted(comparator)
                .collect(Collectors.toList()));
        return "search/employees";
    }
}
