package com.example.chatapp;

class Berichten {

    private String bericht, type;
    private long tijd;
    private boolean gezien;

    private String van;

    public Berichten (String van)
    {
        this.van = van;
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

    public String getVan() {
        return van;
    }

    public void setVan(String van) {
        this.van = van;
    }

    public Berichten(String bericht, String type, long tijd, boolean gezien) {
        this.bericht = bericht;
        this.type = type;
        this.tijd = tijd;
        this.gezien = gezien;
        this.van = van;
    }

    public Berichten()
    {

    }
}
