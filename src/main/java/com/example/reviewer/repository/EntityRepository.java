package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Entity;
import org.springframework.data.repository.CrudRepository;

public interface EntityRepository extends CrudRepository<Entity, Long> {
}
