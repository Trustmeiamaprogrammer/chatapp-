package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GebruikersActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mGebruikerslijst;
    private DatabaseReference gebruikersRef;
    private LinearLayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gebruikers);

        mToolbar = (Toolbar) findViewById(R.id.gebruikersAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Gebruikers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gebruikersRef = FirebaseDatabase.getInstance().getReference().child("Gebruikers");
        mLayoutManager = new LinearLayoutManager(this);
        mGebruikerslijst = (RecyclerView) findViewById(R.id.gebruikersLijst);
        mGebruikerslijst.setHasFixedSize(true);
        mGebruikerslijst.setLayoutManager(mLayoutManager);

    }

    protected void onStart()
    {
        super.onStart();

    }
}
