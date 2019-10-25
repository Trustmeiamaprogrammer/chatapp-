package com.example.chatapp;

public class Gesprek {

    public String datum;

    public boolean gezien;

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
