package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.berichten_layout, parent, false);

        return new BerichtenViewHolder(v);
    }

    public class BerichtenViewHolder extends RecyclerView.ViewHolder {

    public TextView berText;
    public CircleImageView profielFoto;
    public TextView gebruikersnaam;
    public ImageView berImage;

    public BerichtenViewHolder(View view) {
        super(view);


        berText = (TextView) view.findViewById(R.id.berichttekst);
        profielFoto = (CircleImageView) view.findViewById(R.id.bericht_profiel);
        gebruikersnaam = (TextView) view.findViewById(R.id.naamtekst);
        berImage = (ImageView) view.findViewById(R.id.bericht_afbeelding);




    }


        }

        @Override
public void onBindViewHolder (final BerichtenViewHolder viewHolder, int i)
        {
            Berichten b = berLijst.get(i);
            String vanGebruiker = b.getVan();
            String berType = b.getType();

            gebDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(vanGebruiker);
            gebDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String naam = dataSnapshot.child("Naam").getValue().toString();
                    String afbeelding = dataSnapshot.child("ThumbAfbeelding").getValue().toString();

                    viewHolder.gebruikersnaam.setText(naam);

                    Picasso.with(viewHolder.profielFoto.getContext()).load(afbeelding).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.profielFoto);
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (berType.equals("Tekst")){
                viewHolder.berText.setText(b.getBericht());
                viewHolder.berImage.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.berText.setVisibility(View.INVISIBLE);
                Picasso.with(viewHolder.profielFoto.getContext()).load(b.getBericht()).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.berImage);
            }
        }

    @Override
    public int getItemCount() {
        return berLijst.size();
    }
}


