package com.example.chatapp;

import android.content.Context;

class GetTimeAgo {

    private static final int milliSeconden = 1000;
    private static final int milliMinuut = 60 * milliSeconden;
    private static final int milliUur = 60 * milliMinuut;
    private static final int milliDag = 24 * milliUur;

    public static String getTimeAgo(long tijd, Context ctx){
        if (tijd < 1000000000000L){
            tijd *= 1000;

        }

        long nu = System.currentTimeMillis();

        if (tijd > nu || tijd <= 0){
            return null;
        }
        final long vers = nu - tijd;
        if (vers < milliMinuut){
            return "Online";
        }
        else if (vers < 2 * milliMinuut){
            return "1 minuut geleden";
        }

        else if (vers < 50 * milliMinuut){
            return vers / milliMinuut + "minuten geleden";
        }
        else if (vers < 90 * milliMinuut){
            return "1 uur geleden";
        }
        else if (vers < 24 * milliUur){
            return vers /milliUur + "uur geleden";
        }
        else if (vers < 48 * milliUur){
            return "Gisteren";
        }
        else {
            return vers / milliDag + "dagen geleden";
        }
    }
}
