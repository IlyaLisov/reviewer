package com.example.reviewer.model.role;

import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.user.User;
import com.sun.istack.NotNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@javax.persistence.Entity
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    private User user;

    @NotNull
    @ManyToOne
    private Entity entity;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public RoleEntity() {

    }

    public RoleEntity(User user, Entity entity, Role role) {
        this.user = user;
//        this.entity = entity;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
//
//    public Entity getEntity() {
//        return entity;
//    }
//
//    public void setEntity(Entity entity) {
//        this.entity = entity;
//    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
