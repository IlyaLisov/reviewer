package com.example.reviewer.repository;

import com.example.reviewer.model.entity.Entity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EntityRepository extends CrudRepository<Entity, Long> {

    Optional<Entity> findByName(String name);

    List<Entity> findByParentEntity(Entity entity);
}
