package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Booking{
    private int id;
    private int memberId;
    private int boatId;
    private Integer skipperId; // Per permettere null nel caso Member ha una patente.
    private LocalDate bookingDate;
    private int participants;
    private RegistrationType registrationType;

    public Booking(){}

    public Booking(int memberId, int boatId, Integer skipperId, LocalDate bookingDate, int participants, RegistrationType registrationType) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.skipperId = skipperId;
        this.bookingDate = bookingDate;
        this.participants = participants;
        this.registrationType = registrationType;
    }

    public Booking(int id, int memberId, int boatId, Integer skipperId, LocalDate bookingDate, int participants, RegistrationType registrationType) {
        this.id = id;
        this.memberId = memberId;
        this.boatId = boatId;
        this.skipperId = skipperId;
        this.bookingDate = bookingDate;
        this.participants = participants;
        this.registrationType = registrationType;
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

    public int getBoatId() {
        return boatId;
    }

    public void setBoatId(int boatId) {
        this.boatId = boatId;
    }

    public Integer getSkipperId() {
        return skipperId;
    }

    public void setSkipperId(Integer skipperId) {
        this.skipperId = skipperId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public RegistrationType getRegistrationType() {
        return registrationType;
    }

    public void setRegistrationType(RegistrationType registrationType) {
        this.registrationType = registrationType;
    }

}