package com.example.chatapp;

public class Verzoeken {

    public String datum;

    public Verzoeken(){}

    public Verzoeken(String datum){
        this.datum = datum;
    }

    public String getDatum(){
        return datum;
    }

    public void setDatum(String datum){
        this.datum = datum;
    }
}
