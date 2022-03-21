package com.example.reviewer.model.review;

import com.example.reviewer.model.role.Role;
import com.example.reviewer.model.user.User;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 1024)
    private String text;

    @NotNull
    @ManyToOne
    private User author;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private Role authorRole;

    @NotNull
    private Integer mark;

    @NotNull
    private LocalDate reviewDate;

    public Review() {
        this.reviewDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Role getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(Role authorRole) {
        this.authorRole = authorRole;
    }

    public Integer getMark() {
        return mark;
    }

    public void setMark(Integer mark) {
        this.mark = mark;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return id.equals(review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
