package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

public class GebruikersActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private RecyclerView mGebruikerslijst;
    private FirebaseUser mHuidigeGebruiker;
    private DatabaseReference gebruikersRef;
    private DatabaseReference mGebruikerData;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gebruikers);

        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebruiker = mAuth.getCurrentUser();
        String huidigeId = mHuidigeGebruiker.getUid();
        mGebruikerData = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(huidigeId);

        mToolbar = (Toolbar) findViewById(R.id.gebruikersAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Gebruikers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gebruikersRef = FirebaseDatabase.getInstance().getReference().child("Gebruikers");

        mGebruikerslijst = findViewById(R.id.gebruikersLijst);
        mGebruikerslijst.setHasFixedSize(true);
        mGebruikerslijst.setLayoutManager(new LinearLayoutManager(this));

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (mHuidigeGebruiker == null){
            sendToStart();
        } else{
            mGebruikerData.child("Online").setValue(true);
        }

        FirebaseRecyclerOptions<Gebruikers> options=
                new FirebaseRecyclerOptions.Builder<Gebruikers>()
                .setQuery(gebruikersRef, Gebruikers.class)
                .setLifecycleOwner(this)
                .build();


        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gebruikers, GebruikersViewHolder>(options) {
            @NonNull
            @Override
            public GebruikersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                return new GebruikersViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gebruikers_layout, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull GebruikersViewHolder gebruikersViewHolder,int position, @NonNull Gebruikers gebruikers) {
                gebruikersViewHolder.setGebruikersnaam(gebruikers.getNaam());
                gebruikersViewHolder.setGebStatus(gebruikers.getStatus());
                gebruikersViewHolder.setGebAfbeelding(gebruikers.getThumbAfbeelding(), getApplicationContext());

                final String gebId = getRef(position).getKey();

                gebruikersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profielIntent = new Intent(GebruikersActivity.this, ProfielActivity.class);
                        profielIntent.putExtra("GebId", gebId);
                        startActivity(profielIntent);
                    }
                });

            }
        };
mGebruikerslijst.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    protected void onStop(){
        super.onStop();

        if(mHuidigeGebruiker != null){
            gebruikersRef.child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart(){
        Intent startIntent = new Intent(GebruikersActivity.this, Startactivity.class);
        startActivity(startIntent);
        finish();
    }

    public static class GebruikersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GebruikersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGebruikersnaam(String naam){
            TextView gebruikersnaamView = mView.findViewById(R.id.naamGebruiker);
            gebruikersnaamView.setText(naam);

        }
        public void setGebStatus(String status){
            TextView gebStatusView =  mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(status);


        }

        public void setGebAfbeelding (String thumbAfbeelding, Context ctx) {
            CircleImageView gebImageView = (CircleImageView) mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbAfbeelding).placeholder(R.drawable.ic_launcher_foreground).into(gebImageView);

        }


    }
}