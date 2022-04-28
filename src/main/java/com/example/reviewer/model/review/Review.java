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
    protected Long id;

    @Column(length = 1024)
    protected String text;

    @NotNull
    @ManyToOne
    protected User author;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    protected Role authorRole;

    @NotNull
    protected Integer mark;

    @NotNull
    protected LocalDate reviewDate;

    @NotNull
    protected Boolean isVisible;

    @NotNull
    protected Boolean isDeleted;

    @NotNull
    protected Boolean isEdited;

    @NotNull
    protected Boolean isConfirmed;

    @NotNull
    private Integer reportCounter;

    public Review() {
        this.reviewDate = LocalDate.now();
        this.isVisible = true;
        this.isEdited = false;
        this.isDeleted = false;
        this.isConfirmed = false;
        this.reportCounter = 0;
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

    public Boolean getVisible() {
        return isVisible != null ? isVisible : true;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Boolean getDeleted() {
        return isDeleted != null ? isDeleted : false;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean getEdited() {
        return isEdited != null ? isEdited : false;
    }

    public void setEdited(Boolean edited) {
        isEdited = edited;
    }

    public Boolean getConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        isConfirmed = confirmed;
    }

    public Integer getReportCounter() {
        return reportCounter != null ? reportCounter : 0;
    }

    public void setReportCounter(Integer reportCounter) {
        this.reportCounter = reportCounter;
    }

    public void increaseReportCounter() {
        if (reportCounter != null) {
            reportCounter++;
        } else {
            reportCounter = 1;
        }
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
