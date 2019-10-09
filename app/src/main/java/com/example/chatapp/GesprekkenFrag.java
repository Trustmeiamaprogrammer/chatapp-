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
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class BerichtenFrag extends Fragment {

    private RecyclerView mGesLijst;

    private DatabaseReference mGesDatabase;
    private DatabaseReference mBerDatabase;
    private DatabaseReference mGebDatabase;

    private FirebaseAuth mAuth;

    private String mHuidigGebId;

    private View mMainView;


    public BerichtenFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_berichten, container, false);
        mGesLijst = (RecyclerView) mMainView.findViewById(R.ges_list);
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

    public void onStart()
    {
        super.onStart();
        Query gesprekkenQuery = mGesDatabase.orderByChild("timestamp");
        FirebaseRecyclerAdapter<>

    }



}
