package it.unifi.nauticshare.model;

import java.time.LocalDate;

public class Booking {
    private int id;
    private int memberId;
    private int boatId;
    private Integer skipperId; // nullable — può essere null
    private LocalDate date;
    private int seatsBooked;
    private double totalPrice;
    private RegistrationType regType;

    public Booking() {}

    // Senza id — per inserimento nuova prenotazione
    public Booking(int memberId, int boatId, Integer skipperId,
                   LocalDate date, int seatsBooked,
                   double totalPrice, RegistrationType regType) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.skipperId = skipperId;
        this.date = date;
        this.seatsBooked = seatsBooked;
        this.totalPrice = totalPrice;
        this.regType = regType;
    }

    // Con id — per lettura da DB
    public Booking(int id, int memberId, int boatId, Integer skipperId,
                   LocalDate date, int seatsBooked,
                   double totalPrice, RegistrationType regType) {
        this.id = id;
        this.memberId = memberId;
        this.boatId = boatId;
        this.skipperId = skipperId;
        this.date = date;
        this.seatsBooked = seatsBooked;
        this.totalPrice = totalPrice;
        this.regType = regType;
    }

    // Cancellabile solo se la data dell'uscita è futura
    public boolean isCancellable() {
        return date != null && date.isAfter(LocalDate.now());
    }

    // Getters e setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public int getBoatId() { return boatId; }
    public void setBoatId(int boatId) { this.boatId = boatId; }

    public Integer getSkipperId() { return skipperId; }
    public void setSkipperId(Integer skipperId) { this.skipperId = skipperId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public RegistrationType getRegType() { return regType; }
    public void setRegType(RegistrationType regType) { this.regType = regType; }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", boatId=" + boatId +
                ", skipperId=" + skipperId +
                ", date=" + date +
                ", seatsBooked=" + seatsBooked +
                ", totalPrice=" + totalPrice +
                ", regType=" + regType +
                '}';
    }
}