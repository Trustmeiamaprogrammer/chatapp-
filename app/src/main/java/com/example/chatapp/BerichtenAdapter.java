package com.example.chatapp;

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

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import de.hdodenhof.circleimageview.CircleImageView;

public class BerichtenAdapter extends RecyclerView.Adapter<BerichtenAdapter.BerichtenViewHolder> {

    private int selectItem = 0;
    private Context c;

    private ArrayList<Berichten> mBerLijst;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void OnItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mListener = listener;
    }
    private DatabaseReference gebDatabase;
    private DatabaseReference berDatabase;
    private FirebaseAuth mAuth;

private LinearLayout layout;
public TextView berText;
public CircleImageView profielFoto;
public TextView gebruikersnaam;
public ImageView berImage;


private static final String TAG = "LIJST";



    public BerichtenAdapter( ArrayList<Berichten> mBerLijst)
    {

    }


    @Override
    public BerichtenViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.berichten_layout, parent, false);

        BerichtenViewHolder bvh = new BerichtenViewHolder(v, mListener);
        return bvh;

    }

    public static class BerichtenViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profielFoto;

       public LinearLayout linksBerichtLayout;
       public LinearLayout rechtsBerichtLayout;
       public TextView linksBerichtTextview;
       public TextView rechtsBerichtTextview;
       public TextView vanTijd;
       public TextView naarTijd;

    public BerichtenViewHolder(View view, final OnItemClickListener listener) {
        super(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                {
                    int position = getAdapterPosition();
                    if (position  != RecyclerView.NO_POSITION){
                        listener.OnItemClick(position);
                    }
                }
            }
        });

        linksBerichtLayout = itemView.findViewById(R.id.gesprek_links_bericht_layout);
        rechtsBerichtLayout = itemView.findViewById(R.id.gesprek_rechts_bericht_layout);
        linksBerichtTextview = itemView.findViewById(R.id.gesprek_links_bericht_tekst);
        rechtsBerichtTextview = itemView.findViewById(R.id.gesprek_rechts_bericht_text);
        vanTijd = itemView.findViewById(R.id.van_tijd);
        naarTijd = itemView.findViewById(R.id.naar_tijd);

//        berText =  view.findViewById(R.id.berichttekst);
//        profielFoto = view.findViewById(R.id.bericht_profiel);
//        gebruikersnaam =  view.findViewById(R.id.naamtekst);
//        berImage =  view.findViewById(R.id.bericht_afbeelding);


    }


        }

        @Override
        public void onBindViewHolder (final BerichtenViewHolder viewHolder, int position) {

            mAuth = FirebaseAuth.getInstance();
            final String gebId = mAuth.getCurrentUser().getUid();
            final Berichten berDN = mBerLijst.get(position);
            Log.d(TAG, " "+ gebId );
            String vanGebruiker = berDN.getVan();
            final String berType = berDN.getType();
            System.out.println(vanGebruiker);


            gebDatabase = FirebaseDatabase.getInstance().getReference();

            gebDatabase.child("gebruikers").child(vanGebruiker).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    berDatabase = FirebaseDatabase.getInstance().getReference().child("berichten");
                    String naam = dataSnapshot.child("naam").getValue().toString();
                    String afbeelding = dataSnapshot.child("thumbAfb").getValue().toString();

                    //viewHolder.gebruikersnaam.setText(naam);

                    //Picasso.with(viewHolder.profielFoto.getContext()).load(afbeelding).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.profielFoto);
                    if (dataSnapshot.getKey().equals(gebId)) {
                        viewHolder.rechtsBerichtLayout.setVisibility(LinearLayout.VISIBLE);

                        if (berType.equals("Tekst")) {
                            viewHolder.rechtsBerichtTextview.setText(berDN.getBericht());

                        }
                        viewHolder.rechtsBerichtLayout.setVisibility(LinearLayout.GONE);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

//            if (berType.equals("Tekst")){
//                viewHolder.berText.setText(b.getBericht());
//                viewHolder.berImage.setVisibility(View.INVISIBLE);
//            } else {
//                viewHolder.berText.setVisibility(View.INVISIBLE);
//                Picasso.with(viewHolder.profielFoto.getContext()).load(b.getBericht()).placeholder(R.drawable.ic_launcher_foreground).into(viewHolder.berImage);
//            }
       }

    @Override
    public int getItemCount() {
        return 0;
    }


}


