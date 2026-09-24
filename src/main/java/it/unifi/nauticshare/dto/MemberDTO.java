package it.unifi.nauticshare.dto;

import java.time.LocalDate;

public class MemberDTO{
    private String name;
    private String surname;
    private String email;
    private String password;  //!! non in Hash, qui il membro si sta registrando.
    private String city;
    private LocalDate birthday;
    private boolean hasLicense;

    public MemberDTO(String name, String surname, String email, String password, String city, LocalDate birthday, boolean hasLicense) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.city = city;
        this.birthday = birthday;
        this.hasLicense = hasLicense;
    }

    // Getters and setters
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getSurname() {return surname;}
    public void setSurname(String surname) {this.surname = surname;}

    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getCity() {return city;}
    public void setCity(String city) {this.city = city;}

    public LocalDate getBirthday() {return birthday;}
    public void setBirthday(LocalDate birthday) {this.birthday = birthday;}

    public boolean isHasLicense() {return hasLicense;}
    public void setHasLicense(boolean hasLicense) {this.hasLicense = hasLicense;}

}