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
        mVriendenLijst =  mMainView.findViewById(R.id.vriendenLijst);
        mAuth = FirebaseAuth.getInstance();
        huidigeGebruikerId = mAuth.getCurrentUser().getUid();
        vriendenDatabase = FirebaseDatabase.getInstance().getReference()
                .child("vrienden").child(huidigeGebruikerId);
        vriendenDatabase.keepSynced(true);
        gebruikersRef = FirebaseDatabase.getInstance().getReference().child("gebruikers");
        gebruikersRef.keepSynced(true);

        mVriendenLijst.setHasFixedSize(true);
        mVriendenLijst.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Vrienden> options =
                new FirebaseRecyclerOptions.Builder<Vrienden>()
                        .setQuery(vriendenDatabase, Vrienden.class)
                        .setLifecycleOwner(this)
                        .build();

        FirebaseRecyclerAdapter vriendenRecyclerViewAdapter = new FirebaseRecyclerAdapter<Vrienden, VriendenViewHolder>(options) {

            @NonNull
            @Override
            public VriendenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new VriendenViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gebruikers_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final VriendenViewHolder vriendenViewHolder, int position, @NonNull final Vrienden vrienden) {
                final String lijstGebId = getRef(position).getKey();
                gebruikersRef.child(lijstGebId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String naamGeb = dataSnapshot.child("naam").getValue().toString();
                        String gebThumb = dataSnapshot.child("thumbAfb").getValue().toString();
                        String gebStatus = dataSnapshot.child("status").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String gebOnline = dataSnapshot.child("online").getValue().toString();
                            vriendenViewHolder.setGebOnline(gebOnline);
                        }

                        vriendenViewHolder.setNaam(naamGeb);
                        vriendenViewHolder.setGebAfbeelding(gebThumb, getContext());
                        vriendenViewHolder.setStatus(gebStatus);

                        vriendenViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence opties[] = new CharSequence[]{"Open profiel", "Zend bericht"};
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                                builder.setTitle("Selecteer opties");
                                builder.setItems(opties, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if (i == 0) {
                                            Intent profielIntent = new Intent(getContext(), ProfielActivity.class);
                                            profielIntent.putExtra("gebId", lijstGebId);
                                            startActivity(profielIntent);
                                        }

                                        if (i == 1) {
                                            Intent gesIntent = new Intent(getContext(), GesprekActivity.class);
                                            gesIntent.putExtra("gebId", lijstGebId);
                                            gesIntent.putExtra("naamGeb", naamGeb);
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

    public interface OnFragmentInteractionListener {

    }



    public static class VriendenViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public VriendenViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }
        public void setStatus(String status) {
            TextView gebStatusView =  mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(status);
        }

        public void setNaam(String naam){
            TextView gebNaamView =  mView.findViewById(R.id.naamGebruiker);
            gebNaamView.setText(naam);
        }

        public void setGebAfbeelding (String thumbAfb, Context ctx) {
            CircleImageView gebImageView =  mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbAfb).placeholder(R.drawable.ic_launcher_foreground).into(gebImageView);
        }

        public void setGebOnline(String onlineStatus){
            ImageView gebOnlineView =  mView.findViewById(R.id.online_icon);

            if(onlineStatus.equals("true")){
                gebOnlineView.setVisibility(View.VISIBLE);
            }

            else {
                gebOnlineView.setVisibility(View.INVISIBLE);

            }
        }



    }
}
