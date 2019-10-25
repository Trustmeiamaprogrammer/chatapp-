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
import com.firebase.ui.database.FirebaseRecyclerOptions;
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


        mMainView = inflater.inflate(R.layout.fragment_berichten, container, false);
        mGesLijst = (RecyclerView) mMainView.findViewById(R.id.ges_list);
        mAuth = FirebaseAuth.getInstance();
        mHuidigGebId = mAuth.getCurrentUser().getUid();
        mGesDatabase = FirebaseDatabase.getInstance().getReference().child("gesprek").child(mHuidigGebId);
        mGesDatabase.keepSynced(true);
        mGebDatabase = FirebaseDatabase.getInstance().getReference().child("gebruikers");
        mGebDatabase.keepSynced(true);

        mGesLijst.setHasFixedSize(true);
        mGesLijst.setLayoutManager(new LinearLayoutManager(getContext()));
        return mMainView;
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Gesprek> options =
                new FirebaseRecyclerOptions.Builder<Gesprek>()
                        .setQuery(mGesDatabase, Gesprek.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter gespAdapter = new FirebaseRecyclerAdapter<Gesprek, GesprekViewHolder>(options) {
            @NonNull
            @Override
            public GesprekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new GesprekViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.gebruikers_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final GesprekViewHolder gesprekViewHolder, int position, @NonNull final Gesprek gesprek) {


                final String lijstGebId = getRef(position).getKey();

                mGebDatabase.child(lijstGebId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String naamGeb = dataSnapshot.child("naam").getValue().toString();
                        String gebThumb = dataSnapshot.child("thumbAfb").getValue().toString();
                        String gebStatus = dataSnapshot.child("status").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {
                            String gebOnline = dataSnapshot.child("online").getValue().toString();

                            gesprekViewHolder.setGebOnline(gebOnline);
                        }

                        gesprekViewHolder.setNaam(naamGeb);
                        gesprekViewHolder.setGebAfbeelding(gebThumb, getContext());
                        gesprekViewHolder.setStatus(gebStatus);


                        gesprekViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent gesIntent = new Intent(getContext(), GesprekActivity.class);

                                gesIntent.putExtra("GebId", lijstGebId);
                                gesIntent.putExtra("NaamGeb", naamGeb);
                                startActivity(gesIntent);
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

    public interface OnFragmentInteractionListener {

    }

    public static class GesprekViewHolder extends RecyclerView.ViewHolder {
        private static View mView;
        public GesprekViewHolder(@NonNull View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setStatus(String status) {
            TextView gebStatusView = mView.findViewById(R.id.gebruikerStatus);
            gebStatusView.setText(status);
        }


        public static void setNaam(String naam) {
            TextView gebNaamView = (TextView) mView.findViewById(R.id.naamGebruiker);
            gebNaamView.setText(naam);
        }

        public static void setGebAfbeelding(String thumbImage, Context ctx) {
            CircleImageView gebAfbeeldingView = mView.findViewById(R.id.GebruikerAfbeelding);
            Picasso.with(ctx).load(thumbImage).placeholder(R.mipmap.ic_launcher_round).into(gebAfbeeldingView);
        }

        public static void setGebOnline(String onlineStatus) {
            ImageView gebOnlineView = mView.findViewById(R.id.online_icon);

            if (onlineStatus.equals("true")) {
                gebOnlineView.setVisibility(View.VISIBLE);
            } else {
                gebOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}