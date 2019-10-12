package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.core.view.View;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

class BerichtenAdapter extends RecyclerView.Adapter<BerichtenAdapter.BerichtenViewHolder> {

    private List<Berichten> berLijst;
    private DatabaseReference gebDatabase;

    public BerichtenAdapter(List<Berichten> berLijst)
    {
        this.berLijst = berLijst;
    }


    @NonNull
    @Override
    public BerichtenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ber_layout, parent, false);

        return new BerichtenViewHolder(v);
    }

    public class BerichtenViewHolder extends RecyclerView.ViewHolder {

    public TextView berText;
    public CircleImageView profielFoto;
    public TextView gebruikersnaam;
    public ImageView berImage;

    public BerichtenViewHolder(View view)
    {
        super();


        berText = (TextView) view.findViewById(R.id.berTekstLayout);
        profielFoto = (CircleImageView) view.



    }


        }
