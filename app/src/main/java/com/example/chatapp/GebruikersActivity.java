package com.example.chatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class GebruikersActivity extends AppCompatActivity {

    // ID van XML nog

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

    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Gebruikers, GebruikersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gebruikers, GebruikersViewHolder>(
                Gebruikers.class,
                R.layout.gebruikers_layout,
                GebruikersViewHolder.class,
                gebruikersRef
        ) {
            @Override
            protected void populateViewHolder(GebruikersViewHolder gebruikersViewHolder, Gebruikers gebruikers, int i) {
                gebruikersViewHolder.setGebruikersnaam(gebruikers.getNaam());
                gebruikersViewHolder.setGebStatus(gebruikers.getStatus());
                gebruikersViewHolder.setGebAfbeelding(gebruikers.getThumbAfbeelding(), getApplicationContext());

                final String gebId = getRef(i).getKey();

                gebruikersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profielIntent = new Intent(GebruikersActivity.this, ProfielAcvtivity.class);
                        profielIntent.putExtra("GebId", gebId);
                        startActivity(profielIntent);
                    }
                });

            }
        };
mGebruikerslijst.setAdapter(firebaseRecyclerAdapter);

    }

    public static class GebruikersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GebruikersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setGebruikersnaam(String naam) {
            TextView gebruikersnaamView = (TextView) mView.findViewById(R.id.naamGebruiker);

        }
        public void setGebStatus(String status){
            TextView gebStatusView = (TextView) mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(status);


        }

        public void setGebAfbeelding (String thumbAfbeelding, Context ctx) {
            CircleImageView gebImageView = (CircleImageView) mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbAfbeelding).placeholder(R.drawable.StandFoto).into(gebImageView);

        }


    }
}