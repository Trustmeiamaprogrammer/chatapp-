package com.example.chatapp;

public class Verzoeken {
    // Klasse voor database

    public String datum;

    public Verzoeken(){}

    public Verzoeken(String Datum){
        this.datum = datum;
    }

    public String getDatum(){
        return datum;
    }

    public void setDatum(String datum){
        this.datum = datum;
    }
}
