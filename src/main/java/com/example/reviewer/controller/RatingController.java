package com.example.reviewer.controller;

import com.example.reviewer.model.entity.District;
import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.EmployeeType;
import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.entity.EntityType;
import com.example.reviewer.model.entity.Region;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rating")
public class RatingController extends com.example.reviewer.controller.Controller {
    @GetMapping
    public String index(@RequestParam(value = "entityType", required = false) String entityType,
                        @RequestParam(value = "region", required = false) String region,
                        @RequestParam(value = "district", required = false) String district,
                        @RequestParam(value = "employeeType", required = false) String employeeType,
                        @RequestParam(value = "entity", required = false) Long entityId,
                        @RequestParam(value = "entityPage", required = false) Integer entityPage,
                        @RequestParam(value = "employeePage", required = false) Integer employeePage,
                        Model model) {
        Predicate<Entity> entityTypeFilter = entity -> {
            if (entityType != null && !entityType.isEmpty() && !entityType.equals("ANY")) {
                try {
                    return entity.getType().equals(EntityType.valueOf(entityType));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return true;
        };

        Predicate<Entity> regionFilter = entity -> {
            if (region != null && !region.isEmpty() && !region.equals("ANY")) {
                try {
                    return entity.getRegion().equals(Region.valueOf(region));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return true;
        };

        Predicate<Entity> districtFilter = entity -> {
            if (district != null && !district.isEmpty() && !district.equals("ANY")) {
                try {
                    return entity.getDistrict().equals(District.valueOf(district));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return true;
        };

        Predicate<Employee> employeeTypeFilter = employee -> {
            if (employeeType != null && !employeeType.isEmpty() && !employeeType.equals("ANY")) {
                try {
                    return employee.getType().equals(EmployeeType.valueOf(employeeType));
                } catch (IllegalArgumentException ignored) {
                }
            }
            return true;
        };

        Predicate<Employee> entityFilter = employee -> {
            if (entityId != null && !entityId.equals(0L)) {
                try {
                    return employee.getEntity().getId().equals(entityId);
                } catch (IllegalArgumentException ignored) {
                }
            }
            return true;
        };

        if (entityPage == null) {
            entityPage = 1;
        }

        if (employeePage == null) {
            employeePage = 1;
        }

        List<Entity> entities = (List<Entity>) entityRepository.findAll();
        model.addAttribute("entities", entities.stream()
                .filter(Entity::getVisible)
                .filter(entityTypeFilter)
                .filter(regionFilter)
                .filter(districtFilter)
                .sorted((e1, e2) -> e2.getRating() - e1.getRating())
                .skip((entityPage - 1) * AMOUNT_OF_ENTITIES_PER_PAGE)
                .limit(AMOUNT_OF_ENTITIES_PER_PAGE)
                .collect(Collectors.toList()));

        List<Employee> employees = (List<Employee>) employeeRepository.findAll();
        model.addAttribute("employees", employees.stream()
                .filter(Employee::getVisible)
                .filter(employeeTypeFilter)
                .filter(entityFilter)
                .sorted((e1, e2) -> e2.getRating() - e1.getRating())
                .skip((employeePage - 1) * AMOUNT_OF_ENTITIES_PER_PAGE)
                .limit(AMOUNT_OF_ENTITIES_PER_PAGE)
                .collect(Collectors.toList()));

        model.addAttribute("employeeEntities", entityRepository.findAll());
        model.addAttribute("entityTypes", EntityType.values());
        model.addAttribute("employeeTypes", EmployeeType.values());
        model.addAttribute("regions", Region.values());
        model.addAttribute("districts", District.values());
        model.addAttribute("entityType", entityType);
        model.addAttribute("region", region);
        model.addAttribute("district", district);
        model.addAttribute("employeeType", employeeType);
        model.addAttribute("entity", entityId);
        model.addAttribute("entityPageAmount", entities.size() / AMOUNT_OF_ENTITIES_PER_PAGE + 1);
        model.addAttribute("employeePageAmount", employees.size() / AMOUNT_OF_ENTITIES_PER_PAGE + 1);
        model.addAttribute("entityPage", entityPage);
        model.addAttribute("employeePage", employeePage);
        return "rating/index";
    }
}
