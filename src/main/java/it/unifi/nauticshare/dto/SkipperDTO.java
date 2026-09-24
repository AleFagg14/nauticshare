package it.unifi.nauticshare.dto;

public class SkipperDTO{
    private int memberId;
    private int boatId;
    private String certificate;
    private String bio;

    public SkipperDTO(int memberId, int boatId, String certificate, double avgRating, String bio) {
        this.memberId = memberId;
        this.boatId = boatId;
        this.certificate = certificate;
        this.bio = bio;
    }

    // Getters and setters
    public int getMemberId() {return memberId;}
    public void setMemberId(int memberId) {this.memberId = memberId;}

    public int getBoatId() {return boatId;}
    public void setBoatId(int boatId) {this.boatId = boatId;}

    public String getCertificate() {return certificate;}
    public void setCertificate(String certificate) {this.certificate = certificate;}

    public String getBio() {return bio;}
    public void setBio(String bio) {this.bio = bio;}

}