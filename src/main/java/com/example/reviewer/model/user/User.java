package com.example.reviewer.model.user;

import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.role.RoleEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class User {
    private Long id;
    private String name;
    private String login;
    private String password;
    private int rating;
    private LocalDate registerDate;
    private LocalDate lastSeenDate;
    private UserRole userRole;

    private List<RoleEntity> roles;

    public User() {
        this.registerDate = LocalDate.now();
        this.lastSeenDate = LocalDate.now();
        this.roles = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDate getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate) {
        this.registerDate = registerDate;
    }

    public LocalDate getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(LocalDate lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public List<RoleEntity> getRoles() {
        return roles;
    }

    public void addRole(Role role, Entity entity) {
        roles.add(new RoleEntity(this, entity, role));
    }

    public boolean isAdmin() {
        return userRole.equals(UserRole.ADMIN);
    }

    public boolean isModerator() {
        return userRole.equals(UserRole.MODERATOR);
    }

    public boolean isUser() {
        return userRole.equals(UserRole.USER);
    }
}
