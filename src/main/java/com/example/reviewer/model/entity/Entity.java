package com.example.reviewer.model.entity;

import com.example.reviewer.model.user.User;
import com.sun.istack.NotNull;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@javax.persistence.Entity
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private EntityType type;

    @NotNull
    @Column(length = 1024)
    private String name;

    @NotNull
    private String abbreviation;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Region region;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private District district;

    private String address;

    private String siteURL;

    @NotNull
    @ManyToOne
    private User author;

    private String imageURL;

    @ManyToOne
    private Entity parentEntity;

    @Formula("(SELECT COUNT(*) FROM rdb.review r WHERE r.entity_id = id)")
    private Integer reviewsAmount;

    @Formula("(SELECT COUNT(DISTINCT r.author_id) FROM rdb.review r WHERE r.entity_id = id)")
    private Integer peopleInvolved;

    @Formula("(SELECT SUM(r.mark) FROM rdb.review r WHERE r.entity_id = id)")
    private Integer rating;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSiteURL() {
        return siteURL;
    }

    public void setSiteURL(String siteURL) {
        this.siteURL = siteURL;
    }

    public Integer getRating() {
        return rating != null ? rating : 0;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Entity getParentEntity() {
        return parentEntity;
    }

    public void setParentEntity(Entity parentEntity) {
        this.parentEntity = parentEntity;
    }

    public Integer getReviewsAmount() {
        return reviewsAmount;
    }

    public void setReviewsAmount(Integer reviewsAmount) {
        this.reviewsAmount = reviewsAmount;
    }

    public Integer getPeopleInvolved() {
        return peopleInvolved;
    }

    public void setPeopleInvolved(Integer peopleInvolved) {
        this.peopleInvolved = peopleInvolved;
    }

    public String getAverageRating() {
        return (reviewsAmount != null && rating != null && reviewsAmount != 0) ? String.format("%.1f", 1.0f * rating / reviewsAmount) : "Нет оценок";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, name, region, district);
    }
}
