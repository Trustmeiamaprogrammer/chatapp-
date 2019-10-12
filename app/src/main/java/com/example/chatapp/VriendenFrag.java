package com.example.chatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
public class VriendenFrag extends Fragment {
    private RecyclerView mVriendenLijst;
    private DatabaseReference vriendenDatabase;
    private DatabaseReference gebruikersRef;
    private FirebaseAuth mAuth;
    private String huidigeGebruikerId;
    private View mMainView;




    public VriendenFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_vrienden, container, false);
        mVriendenLijst = (RecyclerView) mMainView.findViewById(R.id.vriendenLijst);
        mAuth = FirebaseAuth.getInstance();
        huidigeGebruikerId = mAuth.getCurrentUser().getUid();
        vriendenDatabase = FirebaseDatabase.getInstance().getReference().child("Vrienden").child(huidigeGebruikerId);
        vriendenDatabase.keepSynced(true);
        gebruikersRef = FirebaseDatabase.getInstance().getReference().child("Gebruikers");
        gebruikersRef.keepSynced(true);

        mVriendenLijst.setHasFixedSize(true);
        mVriendenLijst.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Vrienden, VriendenViewHolder> vriendenRecyclerViewAdapter = new FirebaseRecyclerAdapter<Vrienden, VriendenViewHolder>(
            Vrienden.class,
            R.layout.gebruikers_layout,
            VriendenViewHolder.class,
            vriendenDatabase
            ) {
            @Override
            protected void populateViewHolder(final VriendenViewHolder vriendenViewHolder, final Vrienden vrienden, int i) {
                vriendenViewHolder.setDatum(vrienden.getDatum());
                final String lijstGebId = getRef(i).getKey();
                gebruikersRef.child(lijstGebId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String naamGeb = dataSnapshot.child("Naam").getValue().toString();
                        String gebThumb = dataSnapshot.child("ThumbImage").getValue().toString();

                        if (dataSnapshot.hasChild("Online"))
                        {
                            String gebOnline = dataSnapshot.child("Online").getValue().toString();
                            vriendenViewHolder.setGebOnline(gebOnline);
                        }

                        vriendenViewHolder.setNaam(naamGeb);
                        vriendenViewHolder.setGebAfbeelding(gebThumb, getContext());

                        vriendenViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence opties[] = new CharSequence[]{"Open profiel", "Zend bericht"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                builder.setTitle("Selecteer opties");
                                builder.setItems(opties, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i == 0)
                                        {
                                            Intent profielIntent = new Intent(getContext(), ProfielAcvtivity.class);
                                            profielIntent.putExtra("gebruikersId", lijstGebId);
                                            startActivity(profielIntent);
                                        }

                                        if (i == 1){
                                            Intent gesIntent = new Intent(getContext(), GesprekActivity.class);
                                            gesIntent.putExtra("gebruikersId", lijstGebId);
                                            startActivity(gesIntent);
                                        }

                                    }
                                });

                                builder.show();
                            }
                        });


                    }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    };
mVriendenLijst.setAdapter(vriendenRecyclerViewAdapter);
}

    public static class VriendenViewHolder extends RecyclerView.ViewHolder {
        static View mView;

        public VriendenViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setDatum(String datum) {
            TextView gebStatusView = (TextView) mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(datum);
        }

        public void setNaam(String naam){
            TextView gebNaamView = (TextView) mView.findViewById(R.id.naamGebruiker);
            gebNaamView.setText(naam);
        }

        public void setGebAfbeelding (String thumbAfb, Context ctx) {
            CircleImageView gebImageView = (CircleImageView) mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbAfb).placeholder(R.drawable.ic_launcher_background).into(gebImageView);
        }

        public void setGebOnline(String onlineStatus){
            ImageView gebOnlineView = (ImageView) mView.findViewById(R.id.online_icon);

            if(onlineStatus.equals("true")){
                gebOnlineView.setVisibility(View.VISIBLE);
            }

            else {
                gebOnlineView.setVisibility(View.INVISIBLE);

            }
        }





    }
}
