package com.example.chatapp;

public class Gebruikers {

    public String naam;
    public String afbeelding;
    public String status;
    public String thumbAfb;

    public Gebruikers() {
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        naam = naam;
    }

    public String getAfbeelding() {
        return afbeelding;
    }

    public void setAfbeelding(String afbeelding) {
        afbeelding = afbeelding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        status = status;
    }

    public String getThumbAfb() {
        return thumbAfb;
    }

    public void setThumbAfb(String thumbAfb) {
        thumbAfb = thumbAfb;
    }

    public Gebruikers(String naam, String afbeelding, String status, String thumbAfb) {
        naam = naam;
        afbeelding = afbeelding;
        status = status;
        thumbAfb = thumbAfb;
    }
}
