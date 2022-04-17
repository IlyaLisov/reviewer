package com.example.reviewer.repository;

import com.example.reviewer.model.report.EntityReport;
import org.springframework.data.repository.CrudRepository;

public interface EntityReportRepository extends CrudRepository<EntityReport, Long> {
}
