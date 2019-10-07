package com.example.chatapp;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;


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
        return mMainView;
    }

}
