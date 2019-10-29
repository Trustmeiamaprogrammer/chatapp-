package com.example.chatapp;

// Importeer
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

// Gebruikers activity
public class GebruikersActivity extends AppCompatActivity {

    // Variabelen
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private RecyclerView mGebruikerslijst;
    private FirebaseUser mHuidigeGebruiker;
    private DatabaseReference gebruikersRef;
    private DatabaseReference mGebruikerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_gebruikers);
        // Vraag huidige gebruiker op, met UID
        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebruiker = mAuth.getCurrentUser();
        String huidigeId = mHuidigeGebruiker.getUid();
        // Referentie naar de huidige gebruiker
        gebruikersRef = FirebaseDatabase.getInstance().getReference().child("gebruikers").child(huidigeId);
        // Layout appbar
        mToolbar = findViewById(R.id.gebruikersAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Gebruikers");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Referentie naar gebruikers
        mGebruikerData= FirebaseDatabase.getInstance().getReference().child("gebruikers");
        // Layout voor gebruikerslijst
        mGebruikerslijst = findViewById(R.id.gebruikersLijst);
        mGebruikerslijst.setHasFixedSize(true);
        mGebruikerslijst.setLayoutManager(new LinearLayoutManager(this));
    }

    // Als de activity start...
    @Override
    protected void onStart() {
        super.onStart();
        // Als er nog geen huidige gebruiker is...
        if (mHuidigeGebruiker == null){
            sendToStart();
        } else{
            // Zo ja, zet status op online (true)
            gebruikersRef.child("online").setValue(true);
        }

        // Bouw een recycler adapter aan met opties, gebruik een query op de gebruikersdata ref.
        FirebaseRecyclerOptions<Gebruikers> options=
                new FirebaseRecyclerOptions.Builder<Gebruikers>()
                .setQuery(mGebruikerData, Gebruikers.class)
                .setLifecycleOwner(this)
                .build();
        // Maak de adapter aan met de volgende specificaties:
        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Gebruikers, GebruikersViewHolder>(options) {
            @NonNull
            @Override
            public GebruikersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
                return new GebruikersViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gebruikers_layout, parent, false));
            }
            @Override
            protected void onBindViewHolder(@NonNull final GebruikersViewHolder gebruikersViewHolder, int position, @NonNull Gebruikers gebruikers) {
                // Vraag de gebruikers ID op
                final String gebId = getRef(position).getKey();
                // Haal de op van de huidige gebruiker op
                mGebruikerData.child(gebId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String naamGeb = dataSnapshot.child("naam").getValue().toString();
                        String gebAfbeelding = dataSnapshot.child("thumbAfb").getValue().toString();
                        String gebStatus = dataSnapshot.child("status").getValue().toString();
                        // Zet deze variabelen in de ViewHolder
                        gebruikersViewHolder.setNaam(naamGeb);
                        gebruikersViewHolder.setAfbeelding(gebAfbeelding, getApplicationContext());
                        gebruikersViewHolder.setStatus((gebStatus));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}});

                // Ga naar de profiel activity en geef de gebruikers ID mee.
                gebruikersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profielIntent = new Intent(GebruikersActivity.this, ProfielActivity.class);
                        profielIntent.putExtra("gebId", gebId);
                        startActivity(profielIntent);
                    }
                }); }};
mGebruikerslijst.setAdapter(firebaseRecyclerAdapter);
    }

    // Wanneer de app wordt gestopt
    @Override
    protected void onStop(){
        super.onStop();
        // Zet de online status op de tijd dat de gebruiker nog online was
        if(mHuidigeGebruiker != null){
            gebruikersRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }
    // Ga naar de start activity
    private void sendToStart(){
        Intent startIntent = new Intent(GebruikersActivity.this, Startactivity.class);
        startActivity(startIntent);
        finish();
    }


    // Statische klasse voor ViewHolder
    public static class GebruikersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public GebruikersViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setNaam(String naam){
            TextView gebruikersnaamView = mView.findViewById(R.id.naamGebruiker);
            gebruikersnaamView.setText(naam);
        }
        public void setStatus(String status){
            TextView gebStatusView =  mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(status);
        }

        public void setAfbeelding (String thumbAfbeelding, Context ctx) {
            CircleImageView gebImageView =  mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbAfbeelding).placeholder(R.drawable.ic_launcher_foreground).into(gebImageView);
        }


    }
}