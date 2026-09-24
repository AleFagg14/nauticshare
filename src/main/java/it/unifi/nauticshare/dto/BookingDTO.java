package it.unifi.nauticshare.dto;

import it.unifi.nauticshare.model.RegistrationType;

import java.time.LocalDate;

public class BookingDTO {
    private int memberId;
    private int boatId;
    private Integer skipperId; //Può essere null se il membro non richiede uno skipper
    private LocalDate date;
    private int seatsBooked;
    private RegistrationType regType;

    public BookingDTO(int memberId, int boatId, Integer skipperId, LocalDate date, int seatsBooked, RegistrationType regType) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.skipperId = skipperId;
        this.date = date;
        this.seatsBooked = seatsBooked;
        this.regType = regType;
    }

    // Getters and setters
    public int getMemberId() {return memberId;}
    public void setMemberId(int memberId) {this.memberId = memberId;}

    public int getBoatId() {return boatId;}
    public void setBoatId(int boatId) {this.boatId = boatId;}

    public Integer getSkipperId() {return skipperId;}
    public void setSkipperId(Integer skipperId) {this.skipperId = skipperId;}

    public LocalDate getDate() {return date;}
    public void setDate(LocalDate date) {this.date = date;}

    public int getSeatsBooked() {return seatsBooked;}
    public void setSeatsBooked(int seatsBooked) {this.seatsBooked = seatsBooked;}

    public RegistrationType getRegType() {return regType;}
    public void setRegType(RegistrationType regType) {this.regType = regType;}

}