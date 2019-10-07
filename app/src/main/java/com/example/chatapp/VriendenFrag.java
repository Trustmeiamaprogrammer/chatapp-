package com.example.chatapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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
        FirebaseRecyclerAdapter<Vrienden, VriendenViewHolder> vriendenRecyclerViewAdapter = new FirebaseRecyclerAdapter<Vrienden, vriendenViewHolder>
        {
            Vrienden.class,
            R.layout.gebruikers_layout,
            VriendenViewHolder.class,
            vriendenDatabase
        } {
        @Override
       protected void toonViewHolder (final VriendenViewHolder vriendenViewHolder, Vrienden vrienden, int i)
        {
            vriendenViewHolder.setDate(vrienden.getDate());
            final String
        }
    }

}
