package it.unifi.nauticshare.model;

public class Registration {
    private int id;
    private int memberId;
    private RegistrationType type;
    private int year;

    public Registration() {}

    // Senza id
    public Registration(int memberId, RegistrationType type, int year) {
        this.memberId = memberId;
        this.type = type;
        this.year = year;
    }

    // Con id
    public Registration(int id, int memberId, RegistrationType type, int year) {
        this.id = id;
        this.memberId = memberId;
        this.type = type;
        this.year = year;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public RegistrationType getType() { return type; }
    public void setType(RegistrationType type) { this.type = type; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", memberId=" + memberId +
                ", type=" + type +
                ", year=" + year +
                '}';
    }
}