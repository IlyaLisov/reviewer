package com.example.reviewer.model.role;

import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.user.User;

public class RoleEntity {
    private User user;
    private Entity entity;
    private Role role;

    public RoleEntity(User user, Entity entity, Role role) {
        this.user = user;
        this.entity = entity;
        this.role = role;
    }
}
