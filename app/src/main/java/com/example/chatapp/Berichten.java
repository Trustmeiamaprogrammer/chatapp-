package com.example.chatapp;

public class Berichten {

    // Status van bericht wordt gedeclareerd in strings
    public final static String MSG_TYPE_SENT = "Verzonden";
    public final static String MSG_TYPE_RECEIVED = "Ontvangen";

    // Variabelen die overeenkomen met de database
    private String bericht, type;
    private long tijd;
    private boolean gezien;
    private String van;

    // Constructor, Getters en Setters
    public Berichten(String van)
    {
        this.van = van;
    }

    public String getVan() {
        return van;
    }

    public void setVan(String van) {
        this.van = van;
    }

    public Berichten(){

    }



    public Berichten(String bericht, String type, Long tijd, boolean gezien) {
        this.bericht = bericht;
        this.type = type;
        this.tijd = tijd;
        this.gezien = gezien;
    }




    public String getBericht() {
        return bericht;
    }

    public void setBericht(String bericht) {
        this.bericht = bericht;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTijd() {
        return tijd;
    }

    public void setTijd(long tijd) {
        this.tijd = tijd;
    }

    public boolean isGezien() {
        return gezien;
    }

    public void setGezien(boolean gezien) {
        this.gezien = gezien;
    }



}
