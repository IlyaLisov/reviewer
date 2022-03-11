package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Employee;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    List<Employee> findAllByEntityId(Long entityId);
}
