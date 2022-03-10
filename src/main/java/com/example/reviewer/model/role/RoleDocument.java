package com.example.reviewer.model.role;

import com.example.reviewer.model.user.User;
import com.sun.istack.NotNull;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RoleDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String entityName;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    private User user;

    @NotNull
    private String photoId;

    public RoleDocument() {
    }

    public RoleDocument(String entityName, Role role, User user, String photoId) {
        this.entityName = entityName;
        this.role = role;
        this.user = user;
        this.photoId = photoId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role roleType) {
        this.role = roleType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
