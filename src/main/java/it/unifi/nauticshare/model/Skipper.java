package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Skipper extends Member{
    private String licenseNumber;
    private int experienceYears;
    private String bio;              // NUOVO: Biografia dello skipper
    private double avgRating;        // NUOVO: Valutazione media
    private int ratingsCount;        // NUOVO: Conteggio recensioni per il calcolo della media
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
        this.avgRating = rating;
        this.ratingsCount = 0;
        this.available = available;
    }

    public void updateRating(double newRating) {
        if (newRating < 1.0 || newRating > 5.0) {
            throw new IllegalArgumentException("La valutazione deve essere compresa tra 1 e 5.");
        }
        double totalScore = (this.avgRating * this.ratingsCount) + newRating;
        this.ratingsCount++;
        this.avgRating = totalScore / this.ratingsCount;
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

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }


}