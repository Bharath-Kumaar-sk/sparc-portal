package com.sparc.sparcscholarshipbackend.entity;

import com.sparc.sparcscholarshipbackend.enums.*;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sport sportName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Scholarship scholarshipLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tournament tournamentLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Achievement achievement;

    @Column(nullable = false)
    private String federationName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = true)
    private String adminRemarks;

    @Column
    private LocalDateTime submittedAt;

    public Application() {
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Sport getSportName() {
        return sportName;
    }

    public void setSportName(Sport sportName) {
        this.sportName = sportName;
    }

    public Scholarship getScholarshipLevel() {
        return scholarshipLevel;
    }

    public void setScholarshipLevel(Scholarship scholarshipLevel) {
        this.scholarshipLevel = scholarshipLevel;
    }

    public Tournament getTournamentLevel() {
        return tournamentLevel;
    }

    public void setTournamentLevel(Tournament tournamentLevel) {
        this.tournamentLevel = tournamentLevel;
    }

    public Achievement getAchievement() {
        return achievement;
    }

    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }

    public String getFederationName() {
        return federationName;
    }

    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}