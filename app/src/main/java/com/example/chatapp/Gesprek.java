package com.example.chatapp;

public class Gesprek {

    // Variabelen die overeenkomen in de database
    public String datum;
    public boolean gezien;

    // Constructor, getters en setters
    public Gesprek() {
    }
    public Gesprek(String datum, boolean gezien) {
        this.datum = datum;
        this.gezien = gezien;
    }
    public String getDatum() {
        return datum;
    }
    public void setDatum(String datum) {
        this.datum = datum;
    }
    public boolean isGezien() {
        return gezien;
    }
    public void setGezien(boolean gezien) {
        this.gezien = gezien;
    }
}
