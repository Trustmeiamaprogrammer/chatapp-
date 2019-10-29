package com.example.chatapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VerzoekFrag extends Fragment {

    private RecyclerView mVerzoeklijst;
    private DatabaseReference mVerzoekDatabase;
    private DatabaseReference mGebruikerDatabase;
    private FirebaseAuth mAuth;
    private String mHuidigeGebruikerID;
    private View mHoofdView;


    public VerzoekFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mHoofdView = inflater.inflate(R.layout.fragment_verzoek, container, false);
        mVerzoeklijst = mHoofdView.findViewById(R.id.verzoeklijst);
        mAuth = FirebaseAuth.getInstance();

        mHuidigeGebruikerID = mAuth.getCurrentUser().getUid();

        mVerzoekDatabase = FirebaseDatabase.getInstance().getReference().child("vriendVer").child(mHuidigeGebruikerID);
        mVerzoekDatabase.keepSynced(true);
        mGebruikerDatabase = FirebaseDatabase.getInstance().getReference().child("gebruikers");
        mGebruikerDatabase.keepSynced(true);

        mVerzoeklijst.setHasFixedSize(true);
        mVerzoeklijst.setLayoutManager(new LinearLayoutManager(getContext()));

        return mHoofdView;

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Verzoeken> options =
        new FirebaseRecyclerOptions.Builder<Verzoeken>()
        .setQuery(mVerzoekDatabase, Verzoeken.class)
        .setLifecycleOwner(this)
        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Verzoeken, VerzoekViewHolder>(options) {
            @NonNull
            @Override
            public VerzoekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VerzoekViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gebruikers_layout, parent, false));

        }
        @Override
        protected void onBindViewHolder ( @NonNull final VerzoekViewHolder verzoekViewHolder,
        int position, @NonNull final Verzoeken verzoeken){
            final String lijstGebId = getRef(position).getKey();
            mGebruikerDatabase.child(lijstGebId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String naamGeb = dataSnapshot.child("naam").getValue().toString();
                    String gebAfbeelding = dataSnapshot.child("thumbAfb").getValue().toString();
                    String gebStatus = dataSnapshot.child("status").getValue().toString();

                    if (dataSnapshot.hasChild("online")) {
                        String gebOnline = dataSnapshot.child("online").getValue().toString();
                        verzoekViewHolder.setGebruikerOnline(gebOnline);
                    }
                    verzoekViewHolder.setGebruikersnaam(naamGeb);
                    verzoekViewHolder.setAfbeelding(gebAfbeelding, getContext());
                    verzoekViewHolder.setStatus(gebStatus);


                    verzoekViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent profielIntent = new Intent(getContext(), ProfielActivity.class);
                            profielIntent.putExtra("gebId", lijstGebId);
                            startActivity(profielIntent);
                        }

                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    };
    mVerzoeklijst.setAdapter(firebaseRecyclerAdapter);
}
public interface OnFragmentInteractionListener {
    void onFragmentInteraction();
}

public static class VerzoekViewHolder extends RecyclerView.ViewHolder{
    View mView;

    public VerzoekViewHolder(View itemView){
        super(itemView);
        mView = itemView;
    }

    public void setStatus(String status){
        TextView gebStatusView = mView.findViewById(R.id.gebruikerStatus);
        gebStatusView.setText(status);
    }

    public void setGebruikersnaam(String gebruikersnaam){
    TextView gebruikersnaamView = mView.findViewById(R.id.naamGebruiker);
    gebruikersnaamView.setText(gebruikersnaam);
    }

    public void setAfbeelding(String afbeelding, Context ctx){
        CircleImageView gebAfbeeldingView = mView.findViewById(R.id.GebruikerAfbeelding);
        Picasso.with(ctx).load(afbeelding).placeholder(R.drawable.ic_launcher_background).into(gebAfbeeldingView);
    }

    public void setGebruikerOnline(String online){
        ImageView gebruikerOnlineView = mView.findViewById(R.id.online_icon);

        if (online.equals("true")){
            gebruikerOnlineView.setVisibility(View.VISIBLE);
        } else {
            gebruikerOnlineView.setVisibility(View.INVISIBLE);
        }
    }
}

}
