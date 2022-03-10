package com.example.reviewer.repository;

import com.example.reviewer.model.role.RoleDocument;
import org.springframework.data.repository.CrudRepository;

public interface RoleDocumentRepository extends CrudRepository<RoleDocument, Long> {

    Integer countAllByUserId(Long userId);
}
