package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Registration {
    private int id;
    private int memberId;
    private RegistrationType type;
    private int year;
    private LocalDate registrationDate;

    public Registration(){}

    public Registration(int memberId, RegistrationType type, int year, LocalDate registrationDate) {
        this.memberId = memberId;
        this.type = type;
        this.year = year;
        this.registrationDate = registrationDate;
    }

    public Registration(int id, int memberId, RegistrationType type, int year, LocalDate registrationDate) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.year = year;
        this.registrationDate = registrationDate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public RegistrationType getType() {
        return type;
    }

    public void setType(RegistrationType type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

}