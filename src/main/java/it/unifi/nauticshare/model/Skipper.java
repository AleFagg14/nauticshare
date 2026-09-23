package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Skipper extends Member {
    private int boatId;
    private String certificate;
    private double avgRating;
    private String bio;

    public Skipper() {
        super();
    }

    // Senza id
    public Skipper(String name, String surname, String email,
                   String passwordHash, String city, LocalDate birthday,
                   boolean hasLicense, int boatId,
                   String certificate, double avgRating, String bio) {
        super(name, surname, email, passwordHash, city, birthday, hasLicense);
        this.boatId = boatId;
        this.certificate = certificate;
        this.avgRating = avgRating;
        this.bio = bio;
    }

    // Con id
    public Skipper(int id, String name, String surname, String email,
                   String passwordHash, String city, LocalDate birthday,
                   boolean hasLicense, int boatId,
                   String certificate, double avgRating, String bio) {
        super(id, name, surname, email, passwordHash, city, birthday, hasLicense);
        this.boatId = boatId;
        this.certificate = certificate;
        this.avgRating = avgRating;
        this.bio = bio;
    }

    // Aggiorna la media ponderata dopo una nuova valutazione
    public void updateRating(double newRating) {
        if (newRating < 1.0 || newRating > 5.0) {
            throw new IllegalArgumentException(
                    "La valutazione deve essere compresa tra 1 e 5.");
        }
        // Media semplice — il DB mantiene già avg_rating aggiornato
        this.avgRating = (this.avgRating + newRating) / 2.0;
    }

    public int getBoatId() { return boatId; }
    public void setBoatId(int boatId) { this.boatId = boatId; }

    public String getCertificate() { return certificate; }
    public void setCertificate(String certificate) { this.certificate = certificate; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    @Override
    public String toString() {
        return "Skipper{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", boatId=" + boatId +
                ", certificate='" + certificate + '\'' +
                ", avgRating=" + avgRating +
                ", bio='" + bio + '\'' +
                '}';
    }
}