package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Member {
    private int id;
    private String name;
    private String surname;
    private String email;
    private String passwordHash;
    private String city;
    private LocalDate birthday;
    private boolean hasLicense;

    //Senza id, per quando si crea un nuovo membro
    public Member(String name, String surname, String email, String passwordHash, String city,
                  LocalDate birthday, boolean hasLicense) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.city = city;
        this.birthday = birthday;
        this.hasLicense = hasLicense;
    }

    public Member () {}

    //Con id, per quando si recupera un membro dal database
    public Member (int id, String name, String surname, String email, String passwordHash, String city,
                   LocalDate birthday, boolean hasLicense) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.passwordHash = passwordHash;
        this.city = city;
        this.birthday = birthday;
        this.hasLicense = hasLicense;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public boolean isHasLicense() {
        return hasLicense;
    }

    public void setHasLicense(boolean hasLicense) {
        this.hasLicense = hasLicense;
    }

    @Override
    public String toString(){
        return "Member{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", birthday=" + birthday +
                ", hasLicense=" + hasLicense +
                '}';
    }
}