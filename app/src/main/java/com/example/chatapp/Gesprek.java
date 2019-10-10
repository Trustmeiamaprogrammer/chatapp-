package com.example.chatapp;

public class Gesprek {

    public boolean gezien;
    public long timestamp;

    public Gesprek()
    {

    }

    public boolean isGezien()
    {
        return gezien;
    }

    public void setGezien(boolean gezien) {
        this.gezien = gezien;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Gesprek(boolean gezien, long timestamp)
    {
        this.gezien = gezien;
        this.timestamp = timestamp;
    }
}
