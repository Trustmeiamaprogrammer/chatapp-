package com.example.chatapp;

public class Gebruikers {

    public String naam;
    public String afbeelding;
    public String status;
    public String thumbAfb;

    public Gebruikers() {
    }

    public Gebruikers(String naam, String afbeelding, String status, String thumbAfb) {
        this.naam = naam;
        this.afbeelding = afbeelding;
        this.status = status;
        this.thumbAfb = thumbAfb;
    }

    public void setNaam(String naam) {

        this.naam = naam;
    }

    public void setAfbeelding(String afbeelding) {
        this.afbeelding = afbeelding;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNaam() {
        return naam;
    }



    public String getAfbeelding() {
        return afbeelding;
    }



    public String getStatus() {
        return status;
    }




    public void setThumbAfb(String thumbAfb) {
        this.thumbAfb = thumbAfb;
    }

    public String getThumbAfb() {
        return thumbAfb;
    }


}
