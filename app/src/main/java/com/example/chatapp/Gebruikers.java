package com.example.chatapp;

class Gebruikers {

    public String naam;
    public String afbeelding;
    public String status;
    public String thumbAfbeelding;

    public Gebruikers() {
    }

    public Gebruikers(String naam, String afbeelding, String status, String thumbAfbeelding) {
        this.naam = naam;
        this.afbeelding = afbeelding;
        this.status = status;
        this.thumbAfbeelding = thumbAfbeelding;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {

        this.naam = naam;
    }

    public String getAfbeelding() {
        return afbeelding;
    }

    public void setAfbeelding(String afbeelding) {
        this.afbeelding = afbeelding;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbAfbeelding() {
        return thumbAfbeelding;
    }

    public void setThumbAfbeelding(String thumbAfbeelding) {
        this.thumbAfbeelding = thumbAfbeelding;
    }
}
