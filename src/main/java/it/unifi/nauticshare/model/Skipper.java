package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Skipper extends Member{
    private String licenseNumber;
    private int experienceYears;
    private double rating;
    private boolean available;

    public Skipper() {
        super();
    }

    // Costruttore completo (richiama super() per inizializzare i campi di Member)
    public Skipper (int id, String name, String surname, String email, String passwordHash,
                    String city, LocalDate birthday, boolean hasLicense,
                    String licenceNumber, int experienceYears, double rating, boolean available)
    {
        super(id, name, surname, email, passwordHash, city, birthday, hasLicense);
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
        this.rating = rating;
        this.available = available;
    }

    //Getters and setters
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


}