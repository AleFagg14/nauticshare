package it.unifi.nauticshare.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {
    private static final double DAILY_RATE = 100.0;
    private static final double PARTICIPANT_FEE = 10.0;

    private int id;
    private int memberId;
    private int boatId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int numParticipants;
    private double totalPrice;

    public Rental() {}

    // Senza id — per inserimento nuovo noleggio
    public Rental(int memberId, int boatId, LocalDate startDate,
                  LocalDate endDate, int numParticipants, double totalPrice) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numParticipants = numParticipants;
        this.totalPrice = totalPrice;
    }

    // Con id — per lettura da DB
    public Rental(int id, int memberId, int boatId, LocalDate startDate,
                  LocalDate endDate, int numParticipants, double totalPrice) {
        this.id = id;
        this.memberId = memberId;
        this.boatId = boatId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numParticipants = numParticipants;
        this.totalPrice = totalPrice;
    }

    // Calcola prezzo: giorni × DAILY_RATE + partecipanti × PARTICIPANT_FEE
    public double calculatePrice() {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalStateException("Date di noleggio non valide.");
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) days = 1;
        this.totalPrice = (days * DAILY_RATE) + (numParticipants * PARTICIPANT_FEE);
        return this.totalPrice;
    }

    // Cancellabile solo se la data di inizio è futura
    public boolean isCancellable() {
        return startDate != null && startDate.isAfter(LocalDate.now());
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getBoatId() { return boatId; }
    public void setBoatId(int boatId) { this.boatId = boatId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getNumParticipants() { return numParticipants; }
    public void setNumParticipants(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", boatId=" + boatId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", numParticipants=" + numParticipants +
                ", totalPrice=" + totalPrice +
                '}';
    }
}