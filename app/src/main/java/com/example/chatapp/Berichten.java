package com.example.chatapp;

public class Berichten {

    public final static String MSG_TYPE_SENT = "Verzonden";
    public final static String MSG_TYPE_RECEIVED = "Ontvangen";

    private String Bericht, Type;
    private long Tijd;
    private boolean Gezien;

    private String Van;

    public Berichten(String Van)
    {
        this.Van = Van;
    }

    public String getVan() {
        return Van;
    }

    public void setVan(String Van) {
        this.Van = Van;
    }

    public Berichten(){

    }



    public Berichten(String Bericht, String Type, long Tijd, boolean Gezien) {
        this.Bericht = Bericht;
        this.Type = Type;
        this.Tijd = Tijd;
        this.Gezien = Gezien;
    }




    public String getBericht() {
        return Bericht;
    }

    public void setBericht(String Bericht) {
        this.Bericht = Bericht;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public long getTijd() {
        return Tijd;
    }

    public void setTijd(long Tijd) {
        this.Tijd = Tijd;
    }

    public boolean isGezien() {
        return Gezien;
    }

    public void setGezien(boolean Gezien) {
        this.Gezien = Gezien;
    }



}
