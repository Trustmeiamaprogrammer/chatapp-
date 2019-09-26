package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.time.Instant;

public class Startactivity extends AppCompatActivity {


    private Button regKnop;
    private Button loginKnop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startactivity);

        regKnop = (Button) findViewById(R.id.start_regKnop);
        loginKnop = (Button) findViewById(R.id.start_loginKnop);

        regKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent regIntent = new Intent(Startactivity.this, Registreeractivity.class);
                startActivity(regIntent);


            }
        });

        loginKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent logIntent = new Intent(Startactivity.this, Loginactivity.class);
                startActivity(logIntent);


            }
        });
    }
}
