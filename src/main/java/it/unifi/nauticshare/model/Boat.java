package it.unifi.nauticshare.model;

public class Boat{
    private int id;
    private String regNum;
    private String name;
    private BoatType type;
    private int seats;
    private String photoUrl;
    private String description;

    public Boat(){}

    //Senza id, per quando si crea una nuova barca
    public Boat(String regNum, String name, BoatType type, int seats, String photoUrl, String description) {
        this.regNum = regNum;
        this.name = name;
        this.type = type;
        this.seats = seats;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    //Con id, per quando si recupera una barca dal database
    public Boat(int id, String regNum, String name, BoatType type, int seats, String photoUrl, String description) {
        this.id = id;
        this.regNum = regNum;
        this.name = name;
        this.type = type;
        this.seats = seats;
        this.photoUrl = photoUrl;
        this.description = description;
    }

    // Getters and setters
    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getRegNum(){
        return regNum;
    }

    public void setRegNum(String regNum){
        this.regNum = regNum;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public BoatType getType(){
        return type;
    }

    public void setType(BoatType type){
        this.type = type;
    }

    public int getSeats(){
        return seats;
    }

    public void setSeats(int seats){
        this.seats = seats;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl = photoUrl;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return "Boat{" +
                "id=" + id +
                ", regNum='" + regNum + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", seats=" + seats +
                ", photoUrl='" + photoUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }


}