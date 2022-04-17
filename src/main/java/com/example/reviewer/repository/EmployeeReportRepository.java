package com.example.reviewer.repository;

import com.example.reviewer.model.report.EmployeeReport;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeReportRepository extends CrudRepository<EmployeeReport, Long> {
}
