package it.unifi.nauticshare.dto;

import java.time.LocalDate;

public class RentalDTO{
    private int memberId;
    private int boatId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numParticipants;

    public RentalDTO(int memberId, int boatId, LocalDate startDate, LocalDate endDate, int numParticipants) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numParticipants = numParticipants;
    }

    // Getters and setters
    public int getMemberId() {return memberId;}
    public void setMemberId(int memberId) {this.memberId = memberId;}

    public int getBoatId() {return boatId;}
    public void setBoatId(int boatId) {this.boatId = boatId;}

    public LocalDate getStartDate() {return startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate;}

    public LocalDate getEndDate() {return endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate;}

    public int getNumParticipants() {return numParticipants;}
    public void setNumParticipants(int numParticipants) {this.numParticipants = numParticipants;}
}