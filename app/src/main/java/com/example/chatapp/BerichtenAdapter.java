package com.example.chatapp;

// Importeer
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.util.Log;
import de.hdodenhof.circleimageview.CircleImageView;

public class BerichtenAdapter extends RecyclerView.Adapter<BerichtenAdapter.BerichtenViewHolder> {

    // Variabelen
    private List<Berichten> mBerLijst;
    private DatabaseReference gebDatabase;
    private DatabaseReference berDatabase;
    private FirebaseAuth mAuth;

    // Constructor
    public BerichtenAdapter( List<Berichten> mBerLijst)
    {
        this.mBerLijst = mBerLijst;
    }

// Wanneer de ViewHolder wordt gemaakt, geef deze terug.
    @Override
    public BerichtenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.berichten_layout, parent, false);

        return new BerichtenViewHolder(v); }



// Statische klasse, die uitgebreid wordt door Recycler.ViewHolder
    public static class BerichtenViewHolder extends RecyclerView.ViewHolder {

        // Variabelen
        public CircleImageView profielFoto;

       public LinearLayout linksBerichtLayout;
       public LinearLayout rechtsBerichtLayout;
       public TextView linksBerichtTextview;
       public TextView rechtsBerichtTextview;
       public TextView vanTijd;
       public TextView naarTijd;
       public ImageView afbeeldingLinks;
       public ImageView afbeeldingRechts;


       // Methode
       public BerichtenViewHolder(View view){
           super(view);
    // Voor XML
        linksBerichtLayout = itemView.findViewById(R.id.gesprek_links_bericht_layout);
        rechtsBerichtLayout = itemView.findViewById(R.id.gesprek_rechts_bericht_layout);
        linksBerichtTextview = itemView.findViewById(R.id.gesprek_links_bericht_tekst);
        rechtsBerichtTextview = itemView.findViewById(R.id.gesprek_rechts_bericht_text);
        vanTijd = itemView.findViewById(R.id.van_tijd);
        naarTijd = itemView.findViewById(R.id.naar_tijd);
        afbeeldingLinks = itemView.findViewById(R.id.afbeeldingLinks);
        afbeeldingRechts = itemView.findViewById(R.id.afbeeldingRechts); }
        }

        // Methode
        @Override
        public void onBindViewHolder (final BerichtenViewHolder viewHolder, int position) {

            mAuth = FirebaseAuth.getInstance();
            final String gebId = mAuth.getCurrentUser().getUid();
            final Berichten berDN = mBerLijst.get(position);
            String vanGebruiker = berDN.getVan();
            final String berType = berDN.getType();
            System.out.println(vanGebruiker);
            final SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
            // Referentie naar de node gebruikers, met daar de ID van de gebruiker
            gebDatabase = FirebaseDatabase.getInstance().getReference();
            gebDatabase.child("gebruikers").child(vanGebruiker).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Haal in de referentie Berichten de waarden van de naam en van de afbeelding op.
                    berDatabase = FirebaseDatabase.getInstance().getReference().child("berichten");
                    String naam = dataSnapshot.child("naam").getValue().toString();
                    String afbeelding = dataSnapshot.child("thumbAfb").getValue().toString();

                    // Als de uid overeenkomt met de gebruikers ID, plaats de berichten en afbeeldingen
                    // aan de rechter kant
                   if (dataSnapshot.getKey().equals(gebId)) {
                        viewHolder.rechtsBerichtLayout.setVisibility(LinearLayout.VISIBLE);
                        if (berType.equals("tekst")) {
                            viewHolder.afbeeldingRechts.setVisibility(View.INVISIBLE);
                            viewHolder.rechtsBerichtTextview.setText(berDN.getBericht());
                            viewHolder.naarTijd.setText(sfd.format(new Date(berDN.getTijd())));
                        }
                        if(berType.equals("afbeelding")){
                            viewHolder.rechtsBerichtTextview.setVisibility(View.INVISIBLE);
                        Picasso.with(viewHolder.afbeeldingRechts.getContext())
                                .load(berDN.getBericht())
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(viewHolder.afbeeldingRechts);

                            viewHolder.naarTijd.setText(sfd.format(new Date(berDN.getTijd())));
                        }
                        viewHolder.linksBerichtLayout.setVisibility(LinearLayout.GONE);
                    }
                   // Zo niet, plaats de tekst en afbeelding aan de linkerkant
                    else{
                        viewHolder.linksBerichtLayout.setVisibility(LinearLayout.VISIBLE);
                        if(berType.equals("tekst")){
                            viewHolder.afbeeldingLinks.setVisibility(View.INVISIBLE);
                            viewHolder.linksBerichtTextview.setText(berDN.getBericht());
                            viewHolder.vanTijd.setText(sfd.format(new Date(berDN.getTijd())));
                        }
                        if(berType.equals("afbeelding")){
                            viewHolder.linksBerichtTextview.setVisibility(View.INVISIBLE);
                            Picasso.with(viewHolder.afbeeldingLinks.getContext()).load(berDN.getBericht())
                                    .placeholder(R.drawable.ic_launcher_foreground)
                                    .into(viewHolder.afbeeldingLinks);
                            viewHolder.vanTijd.setText(sfd.format(new Date(berDN.getTijd())));
                        }
                        viewHolder.rechtsBerichtLayout.setVisibility(LinearLayout.GONE);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }});
       }
// Bepaal de positie van het item, zodat het duidelijk is welke item er aangeroepen moet worden
    @Override
    public int getItemCount() {
        return mBerLijst.size();
    }
}


