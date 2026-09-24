package it.unifi.nauticshare.dto;

import it.unifi.nauticshare.model.RegistrationType;

public class RegistrationDTO {
    private int memberId;
    private RegistrationType type;
    private int year;

    public RegistrationDTO(int memberId, RegistrationType type, int year) {
        this.memberId = memberId;
        this.type = type;
        this.year = year;
    }

    public int getMemberId() { return memberId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }

    public RegistrationType getType() { return type; }
    public void setType(RegistrationType type) { this.type = type; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}