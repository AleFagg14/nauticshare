package it.unifi.nauticshare.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Rental {
    private int id;
    private int boatId;
    private int memberId;
    private Integer skipperId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int participants;        // NUOVO: Numero di partecipanti
    private double totalCost;

    public Rental() {
    }

    public Rental(int boatId, int memberId, Integer skipperId, LocalDate startDate, LocalDate endDate, int participants, double totalCost) {
        this.boatId = boatId;
        this.memberId = memberId;
        this.skipperId = skipperId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.totalCost = totalCost;
    }

    public Rental(int id, int boatId, int memberId, Integer skipperId, LocalDate startDate, LocalDate endDate, int participants, double totalCost) {
        this.id = id;
        this.boatId = boatId;
        this.memberId = memberId;
        this.skipperId = skipperId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.participants = participants;
        this.totalCost = totalCost;
    }

    /**
     * NUOVO METODO: Calcola il prezzo totale in base ai giorni di noleggio e al costo giornaliero
     */
    public double calculatePrice(double dailyRate) {
        if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
            throw new IllegalStateException("Date di noleggio non valide.");
        }
        // Calcola la differenza in giorni (incluso il giorno stesso, minimo 1 giorno)
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        if (days == 0) {
            days = 1;
        }
        this.totalCost = days * dailyRate;
        return this.totalCost;
    }

    /**
     * NUOVO METODO: Verifica se il noleggio è cancellabile (deve iniziare in una data futura)
     */
    public boolean isCancellable() {
        return startDate != null && startDate.isAfter(LocalDate.now());
    }

    // --- GETTERS E SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBoatId() {
        return boatId;
    }

    public void setBoatId(int boatId) {
        this.boatId = boatId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Integer getSkipperId() {
        return skipperId;
    }

    public void setSkipperId(Integer skipperId) {
        this.skipperId = skipperId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}