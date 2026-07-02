package com.sparc.sparcscholarshipbackend.dto;

import com.sparc.sparcscholarshipbackend.enums.*;

import java.time.LocalDate;


public class ApplicationRequest {

    private String firstName;
    private String lastName;
    private Gender gender;
    private LocalDate dateOfBirth;
    private Sport sportName;
    private Scholarship scholarshipLevel;
    private Tournament tournamentLevel;
    private Achievement achievement;
    private String federationName;
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
}
