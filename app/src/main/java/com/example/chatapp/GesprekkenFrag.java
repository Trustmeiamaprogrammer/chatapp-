package com.example.chatapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class GesprekkenFrag extends Fragment {

    private RecyclerView mGesLijst;

    private DatabaseReference mGesDatabase;
    private DatabaseReference mBerDatabase;
    private DatabaseReference mGebDatabase;

    private FirebaseAuth mAuth;

    private String mHuidigGebId;

    private View mMainView;


    public GesprekkenFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_berichten, container, false);
        mGesLijst = (RecyclerView) mMainView.findViewById(R.id.ges_list);
        mAuth = FirebaseAuth.getInstance();
        mHuidigGebId = mAuth.getCurrentUser().getUid();
        mGesDatabase = FirebaseDatabase.getInstance().getReference().child("Gesprekken").child(mHuidigGebId);
        mGesDatabase.keepSynced(true);
        mGebDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers");

        mBerDatabase = FirebaseDatabase.getInstance().getReference().child("Berichten").child(mHuidigGebId);
        mGebDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mGesLijst.setHasFixedSize(true);
        mGesLijst.setLayoutManager(linearLayoutManager);
        return mMainView;
    }

    public void onStart() {
        super.onStart();
        Query gesprekkenQuery = mGesDatabase.orderByChild("timestamp");
        FirebaseRecyclerAdapter<Gesprek, GesprekViewHolder> gespAdapter = new FirebaseRecyclerAdapter<Gesprek, GesprekViewHolder>(
                Gesprek.class,
                R.layout.gebruikers_layout,
                GesprekViewHolder.class,
                gesprekkenQuery
        ) {
            @Override
            protected void populateViewHolder(final GesprekViewHolder gesprekViewHolder, final Gesprek gesprek, int i) {
                final String lijstGebId = getRef(i).getKey();
                Query laatstBerichtQuery = mBerDatabase.child(lijstGebId).limitToLast(1);

                laatstBerichtQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String data = dataSnapshot.child("Berichten").getValue().toString();
                        GesprekViewHolder.setBericht(data, gesprek.isGezien());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                mGebDatabase.child(lijstGebId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String naamGeb = dataSnapshot.child("naam").getValue().toString();
                        // THUMB IMAGE REF NIET ZEKER
                        String gebThub = dataSnapshot.child("thumb_image").toString();

                        if (dataSnapshot.hasChild("online")) {
                            String gebOnline = dataSnapshot.child("online").getValue().toString();
                            GesprekViewHolder.setGebOnline(gebOnline);
                        }
                        GesprekViewHolder.setNaam(naamGeb);
                        GesprekViewHolder.setGebAfbeelding(gebThub, getContext());

                        GesprekViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent gesprekIntent  = new Intent(getContext(), GesprekActivity.class);
                                // geb_id juist???
                                gesprekIntent.putExtra("geb_id", lijstGebId);
                                gesprekIntent.putExtra("geb_naam", naamGeb);
                                startActivity(gesprekIntent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }


        };

        mGesLijst.setAdapter(gespAdapter);
    }

    public static class GesprekViewHolder extends RecyclerView.ViewHolder {
        static View mView;

        public GesprekViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setBericht(String bericht, boolean isGeien) {
            TextView gebStatusView = (TextView) mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(bericht);
            if (!isGeien) {
                gebStatusView.setTypeface(gebStatusView.getTypeface(), Typeface.BOLD);
            } else {
                gebStatusView.setTypeface(gebStatusView.getTypeface(), Typeface.NORMAL);
            }
        }

        public void setNaam(String naam) {
            TextView gebNaamView = (TextView) mView.findViewById(R.id.naamGebruiker);
            gebNaamView.setText(naam);
        }

        public void setGebAfbeelding(String thumbImage, Context ctx) {
            CircleImageView gebAfbeeldingView = (CircleImageView) mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbImage).placeholder(R.mipmap.ic_launcher_round).into(gebAfbeeldingView);
        }

        public void setGebOnline(String onlineStatus) {
            ImageView gebOnlineView = (ImageView) mView.findViewById(R.id.online_icon);

            if (onlineStatus.equals("true")) {
                gebOnlineView.setVisibility(View.VISIBLE);
            } else {
                gebOnlineView.setVisibility(View.INVISIBLE);


            }
        }

    }
}