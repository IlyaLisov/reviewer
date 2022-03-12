package com.example.reviewer.repository;

import com.example.reviewer.model.user.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> getByLogin(String login);

    boolean existsByLogin(String login);
}
