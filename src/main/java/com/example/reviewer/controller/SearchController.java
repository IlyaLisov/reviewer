package com.example.reviewer.controller;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.Entity;
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

    @GetMapping()
    public String index(@RequestParam("query") String query, Model model) {
        model.addAttribute("entities", education(query));
        model.addAttribute("employees", employee(query));
        model.addAttribute("page", 1);
        return "search/index";
    }

    public List<Entity> education(@RequestParam("query") String query) {
        Comparator<Entity> comparator = (entity1, entity2) -> {
            if (!query.isEmpty()) {
                String[] wordsInQuery = query.split(" ");
                return (int) (Arrays.stream(wordsInQuery).filter(word -> entity2.getName().toLowerCase().contains(word.toLowerCase())).count()
                        - (Arrays.stream(wordsInQuery).filter(word -> entity1.getName().toLowerCase().contains(word.toLowerCase())).count()));
            } else {
                return entity1.getRating().compareTo(entity2.getRating());
            }
        };

        String[] wordsInQuery = query.split(" ");
        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        return entities.stream()
                .filter(Entity::getVisible)
                .filter(entity -> Arrays.stream(wordsInQuery)
                        .anyMatch(word -> entity.getName().toLowerCase().contains(word.toLowerCase())
                                || firstLettersFromName(entity.getName().toLowerCase()).contains(word.toLowerCase()))
                        || (entity.getAbbreviation() != null && query.toLowerCase().contains(entity.getAbbreviation().toLowerCase())))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private String firstLettersFromName(String name) {
        String[] words = name.split(" ");
        StringBuilder result = new StringBuilder();
        for (String s : words) {
            result.append(s.charAt(0));
        }
        return result.toString();
    }

    public List<Employee> employee(@RequestParam("query") String query) {
        Comparator<Employee> comparator = (employee1, employee2) -> {
            if (!query.isEmpty()) {
                String[] wordsInQuery = query.split(" ");
                return (int) (Arrays.stream(wordsInQuery).filter(word -> employee2.getName().toLowerCase().contains(word.toLowerCase())).count()
                        - Arrays.stream(wordsInQuery).filter(word -> employee1.getName().toLowerCase().contains(word.toLowerCase())).count());
            } else {
                return employee1.getRating().compareTo(employee2.getRating());
            }
        };

        String[] wordsInQuery = query.split(" ");
        List<Employee> employees = (List<Employee>) employeeRepository.findAll();
        return employees.stream()
                .filter(Employee::getVisible)
                .filter(employee -> Arrays.stream(wordsInQuery)
                        .anyMatch(word -> employee.getName().toLowerCase().contains(word.toLowerCase())))
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
