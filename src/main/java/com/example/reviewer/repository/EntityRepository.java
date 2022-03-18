package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EntityRepository extends CrudRepository<Entity, Long> {

    public Optional<Entity> findByName(String name);
}
