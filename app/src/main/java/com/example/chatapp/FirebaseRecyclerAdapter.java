// Verkregen van: https://www.youtube.com/watch?v=vpObpZ5MYSE

package com.example.chatapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class FirebaseRecyclerAdapter extends RecyclerView.Adapter<FirebaseRecyclerAdapter.MyViewHolder> {
Context context;

//Klasse aanmaken Gesprek
ArrayList<Gesprek> gesprekken;

public FirebaseRecyclerAdapter (Context c, ArrayList<Gesprek> g)
{
    context = c;
    gesprekken = g;
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //Layout maken voor gesprekken
        return new MyViewHolder(layoutInflater.from(context).inflate(R.layout.layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    // Klasse gesprek moet aangemaakt worden met getNaam() en getGesprek()
        holder.naam.setText(gesprekken.get(position).getNaam());
        holder.gesprek.setText(gesprekken.get(position).getGesprek());
        // Ook voor profilepic?
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        // Mogelijk 2 variabelen voor naam en gesprek
        TextView naam, gesprek;
        // Ook variabel voor foto?
        public MyViewHolder(View itemView)
        {
            super(itemView);
            // In View een vak aanmaken met naam en gesprek.
            naam = (TextView) itemView.findViewById(R.id.naam);
            gesprek = (TextView) itemView.findViewById(R.id.gesprek);
        }
    }
}
