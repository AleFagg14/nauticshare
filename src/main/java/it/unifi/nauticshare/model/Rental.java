package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Rental {
    private int id;
    private int boatId;
    private int memberId;
    private Integer skipperId; // Per permettere null nel caso Member ha una patente.
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;

    public Rental() {
    }

    public Rental(int boatId, int memberId, Integer skipperId, LocalDate startDate, LocalDate endDate, double totalCost) {
        this.boatId = boatId;
        this.memberId = memberId;
        this.skipperId = skipperId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    public Rental(int id, int boatId, int memberId, Integer skipperId, LocalDate startDate, LocalDate endDate, double totalCost) {
        this.id = id;
        this.boatId = boatId;
        this.memberId = memberId;
        this.skipperId = skipperId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
    }

    // Getters and setters
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

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}