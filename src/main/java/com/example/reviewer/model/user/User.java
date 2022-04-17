package com.example.reviewer.model.user;

import com.example.reviewer.model.entity.Entity;
import com.example.reviewer.model.review.EmployeeReview;
import com.example.reviewer.model.review.EntityReview;
import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.role.RoleEntity;
import com.sun.istack.NotNull;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@javax.persistence.Entity
public class User implements Comparable<User> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String login;

    @NotNull
    private String password;

    @NotNull
    private Integer rating;

    @NotNull
    private LocalDate registerDate;

    @NotNull
    private LocalDate lastSeenDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<RoleEntity> roles;

    private Boolean isBlocked;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<EntityReview> likedEntityReviews;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<EmployeeReview> likedEmployeeReviews;

    public User() {
        this.rating = 0;
        this.registerDate = LocalDate.now();
        this.lastSeenDate = LocalDate.now();
        this.likedEntityReviews = new ArrayList<>();
        this.likedEmployeeReviews = new ArrayList<>();
        this.isBlocked = false;
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public void upRating(Integer rating) {
        this.rating += rating;
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

    public void setRoles(List<RoleEntity> roles) {
        this.roles = roles;
    }

    public List<Role> getRolesInEntity(Long entityId) {
        return roles.stream()
                .filter(roleEntity -> roleEntity.getEntity().getId().equals(entityId))
                .map(RoleEntity::getRole)
                .collect(Collectors.toList());
    }

    public void addRole(Role role, Entity entity) {
        roles.add(new RoleEntity(this, entity, role));
    }

    public boolean hasRole(Long entityId) {
        return roles.stream()
                .anyMatch(roleEntity -> Objects.equals(roleEntity.getEntity().getId(), entityId));
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

    public List<EntityReview> getLikedEntityReviews() {
        return likedEntityReviews;
    }

    public void setLikedEntityReviews(List<EntityReview> likedEntityReviews) {
        this.likedEntityReviews = likedEntityReviews;
    }

    public void addLikedEntityReview(EntityReview review) {
        likedEntityReviews.add(review);
    }

    public void removeLikedEntityReview(EntityReview review) {
        likedEntityReviews.remove(review);
    }

    public List<EmployeeReview> getLikedEmployeeReviews() {
        return likedEmployeeReviews;
    }

    public void setLikedEmployeeReviews(List<EmployeeReview> likedEmployeeReviews) {
        this.likedEmployeeReviews = likedEmployeeReviews;
    }

    public void addLikedEmployeeReview(EmployeeReview review) {
        likedEmployeeReviews.add(review);
    }

    public void removeLikedEmployeeReview(EmployeeReview review) {
        likedEmployeeReviews.remove(review);
    }

    public Boolean getBlocked() {
        return isBlocked;
    }

    public void setBlocked(Boolean blocked) {
        isBlocked = blocked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(User user) {
        return rating - user.rating;
    }
}
