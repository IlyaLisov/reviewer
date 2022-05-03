package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Employee;
import com.example.reviewer.model.entity.EmployeeType;
import com.example.reviewer.model.entity.Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findAllByEntityId(Long entityId);

    Optional<Employee> findByNameAndEntity(String name, Entity entity);

    Optional<Employee> findByEntityAndType(Entity entity, EmployeeType employeeType);
}
