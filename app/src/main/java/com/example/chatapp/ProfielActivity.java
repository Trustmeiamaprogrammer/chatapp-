package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfielActivity extends AppCompatActivity {


    private ImageView profielFoto;
    private TextView profielNaam, profielStatus, profielVriendenCount;
    private Button profielZendKnop, profielWeigerKnop;

    private FirebaseAuth mAuth;

    private DatabaseReference gebDatabase;
    private DatabaseReference verzoekDatabase;
    private DatabaseReference vriendDatabase;
    private DatabaseReference notDatabase;

    private DatabaseReference mHuidigRef;
    private FirebaseUser mHuidigGeb;
    private FirebaseUser mHuidigeGebruiker;

    private String huidigState;

    private ProgressDialog mProcessDialog;
    private DatabaseReference vriendenRef;
    private DatabaseReference mGebDatabase;

    private int aantalVrienden = 0;

    private int aantalGebruikers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiel);

        final String gebId = getIntent().getStringExtra("gebId");

        mHuidigRef = FirebaseDatabase.getInstance().getReference();
        gebDatabase = FirebaseDatabase.getInstance().getReference().child("gebruikers").child(gebId);
        verzoekDatabase = FirebaseDatabase.getInstance().getReference().child("vriendVer");
        vriendDatabase = FirebaseDatabase.getInstance().getReference().child("vrienden");
        notDatabase = FirebaseDatabase.getInstance().getReference().child("notificaties");
        mHuidigGeb = FirebaseAuth.getInstance().getCurrentUser();

        mAuth = FirebaseAuth.getInstance();

        profielFoto = findViewById(R.id.profielAfbeelding);
        profielNaam = findViewById(R.id.profielnaam);
        profielStatus =  findViewById(R.id.profielStatus);
        //profielVriendenCount = findViewById(R.id.profielAantal);
        profielZendKnop = findViewById(R.id.ProfielZendKnop);
        profielWeigerKnop =  findViewById(R.id.ProfielWeigerKnop);

        huidigState = "geenVrienden";
        profielWeigerKnop.setVisibility(View.INVISIBLE);
        profielWeigerKnop.setEnabled(false);


        mProcessDialog = new ProgressDialog(this);
        mProcessDialog.setTitle("Laden van gebruikers data");
        mProcessDialog.setMessage("Even geduld...");
        mProcessDialog.setCanceledOnTouchOutside(false);
        mProcessDialog.show();

        vriendenRef = FirebaseDatabase.getInstance().getReference().child("vrienden");
        mHuidigeGebruiker = FirebaseAuth.getInstance().getCurrentUser();

        String huidigeGebUid = mHuidigeGebruiker.getUid();

        mGebDatabase = FirebaseDatabase.getInstance().getReference().child("gebruikers").child(huidigeGebUid);
        mGebDatabase.keepSynced(true);

        mGebDatabase = FirebaseDatabase.getInstance().getReference();
        vriendenRef.child(huidigeGebUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    aantalVrienden = (int) dataSnapshot.getChildrenCount();
                    System.out.println(aantalVrienden);
                } }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { } });

        mGebDatabase.child("gebruikers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    aantalGebruikers = (int) dataSnapshot.getChildrenCount();
                    System.out.println(aantalGebruikers);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        PieChart pieChart = findViewById(R.id.taartGrafiek);
        pieChart.setUsePercentValues(false);
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry( aantalVrienden, "Vrienden"));
        pieEntries.add(new PieEntry( aantalGebruikers, "Gebruikers"));
        PieDataSet set = new PieDataSet(pieEntries, "Verhouding");
        PieData data = new PieData(set);
        set.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDescription(null);
        data.setValueTextSize(12f);
        pieChart.setHoleRadius(00);
        pieChart.setData(data);
        pieChart.invalidate();



        gebDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {
                String gebruikersnaam = dataSnapshot.child("naam").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String afbeelding = dataSnapshot.child("afbeelding").getValue().toString();

                profielNaam.setText(gebruikersnaam);
                profielStatus.setText(status);

                Picasso.with(ProfielActivity.this).load(afbeelding).placeholder(R.drawable.ic_launcher_foreground).into(profielFoto);

                if(mHuidigGeb.getUid().equals(gebId)){
                    profielWeigerKnop.setEnabled(false);
                    profielWeigerKnop.setVisibility(View.INVISIBLE);

                    profielZendKnop.setEnabled(false);
                    profielZendKnop.setVisibility(View.INVISIBLE);

                }


                verzoekDatabase.child(mHuidigGeb.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(gebId)) {
                                String ver_type = dataSnapshot.child(gebId).child("verType").getValue().toString();

                                if (ver_type.equals("ontvangen")) {

                                    huidigState = "verOntvang";
                                    profielZendKnop.setText("Accepteer verzoek");
                                    profielWeigerKnop.setVisibility(View.VISIBLE);
                                    profielWeigerKnop.setEnabled(true);


                                } else if (ver_type.equals("verzonden")) {
                                    huidigState = "verStuurd";
                                    profielZendKnop.setText("Annuleer verzoek");
                                    profielWeigerKnop.setVisibility(View.INVISIBLE);
                                    profielWeigerKnop.setEnabled(false);
                                }

                                mProcessDialog.dismiss();
                            } else {
                                vriendDatabase.child(mHuidigGeb.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(gebId)) {
                                            huidigState = "vrienden";
                                            profielZendKnop.setText("Verwijder Vriend");
                                            profielWeigerKnop.setVisibility(View.INVISIBLE);
                                            profielWeigerKnop.setEnabled(false);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                mProcessDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profielWeigerKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Map weigerVriendMap = new HashMap();
                        weigerVriendMap.put("vriendVer/"+ mHuidigGeb.getUid() + "/" + gebId, null);
                        weigerVriendMap.put("vriendVer/"+ gebId + "/" + mHuidigGeb.getUid(), null);


                        mHuidigRef.updateChildren(weigerVriendMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                if(databaseError == null){
                                    huidigState = "geenVrienden";
                                    profielZendKnop.setText("Vriendschap verzoek");

                                    profielWeigerKnop.setVisibility(View.INVISIBLE);
                                    profielWeigerKnop.setEnabled(false);

                                } else {
                                    String fout = databaseError.getMessage();
                                    Toast.makeText(ProfielActivity.this, fout, Toast.LENGTH_SHORT).show();

                                }
                                profielZendKnop.setEnabled(true);
                            }
                        });



//                if (huidigState.equals("verOntvang")) {
//                    profielZendKnop.setText("Vriendschap verzoek");
//
//                    profielWeigerKnop.setVisibility(View.INVISIBLE);
//                    profielWeigerKnop.setEnabled(false);
//
//                    if (huidigState.equals("verStuurd")) {
//                        profielZendKnop.setText("Vriendschap verzoek");
//
//                        profielWeigerKnop.setVisibility(View.INVISIBLE);
//                        profielWeigerKnop.setEnabled(false);
//                    }


          //      }

            }
        });

        profielZendKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profielZendKnop.setEnabled(false);
                if(huidigState.equals("geenVrienden")) {

                    DatabaseReference nNotRef = mHuidigRef.child("notificaties").child(gebId).push();
                    String nNotId = nNotRef.getKey();

                    HashMap<String, String> notData = new HashMap<>();
                    notData.put("van", mHuidigGeb.getUid());
                    notData.put("type", "verzoek");
                    Map verMap = new HashMap<>();
                    verMap.put("vriendVer/" + mHuidigGeb.getUid() + "/" + gebId + "/verType", "verzonden");
                    verMap.put("vriendVer/" + gebId + "/" + mHuidigGeb.getUid() + "/verType", "ontvangen");
                    verMap.put("notificaties/" + gebId + "/" + nNotId, notData);

                    mHuidigRef.updateChildren(verMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ProfielActivity.this, "Verzoek versturen niet geukt.", Toast.LENGTH_SHORT).show();
                            }

                                huidigState = "verStuurd";
                                profielZendKnop.setText("Annuleer");

                                profielZendKnop.setEnabled(true);

                        }
                    });
                }

                if(huidigState.equals("verStuurd")){
                    verzoekDatabase.child(mHuidigGeb.getUid()).child(gebId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            verzoekDatabase.child(gebId).child(mHuidigGeb.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    profielZendKnop.setEnabled(true);
                                    huidigState = "geenVrienden";
                                    profielZendKnop.setText("Verzoek verstuurd");

                                    profielWeigerKnop.setVisibility(View.INVISIBLE);
                                    profielWeigerKnop.setEnabled(false);



                                }
                            });

                        }
                    });
                }

                if(huidigState.equals("verOntvang")){
                    final String huidigDatum = DateFormat.getDateTimeInstance().format(new Date());

                    Map vriendMap = new HashMap();
                    vriendMap.put("vrienden/" + mHuidigGeb.getUid() + "/" + gebId + "/datum", huidigDatum);
                    vriendMap.put("vrienden/" + gebId + "/" + mHuidigGeb.getUid() + "/datum", huidigDatum);
                    vriendMap.put("vriendVer/" + mHuidigGeb.getUid() + "/" + gebId, null);
                    vriendMap.put("vriendVer/" + gebId + "/" + mHuidigGeb.getUid(), null);

                    mHuidigRef.updateChildren(vriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                profielZendKnop.setEnabled(true);
                                huidigState ="vrienden";
                                profielZendKnop.setText("verwijder vriend");

                                profielWeigerKnop.setVisibility(View.INVISIBLE);
                                profielWeigerKnop.setEnabled(false);


                            } else {
                                String fout = databaseError.getMessage();
                                Toast.makeText(ProfielActivity.this, fout, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(huidigState.equals("vrienden")){
                    Map ontvriendMap = new HashMap();
                    ontvriendMap.put("vrienden/"+ mHuidigGeb.getUid() + "/" + gebId, null);
                    ontvriendMap.put("vrienden/"+ gebId + "/" + mHuidigGeb.getUid(), null);

                    mHuidigRef.updateChildren(ontvriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                huidigState = "geenVrienden";
                                profielZendKnop.setText("Vriendschap verzoek");

                                profielWeigerKnop.setVisibility(View.INVISIBLE);
                                profielWeigerKnop.setEnabled(false);

                            } else {
                                String fout = databaseError.getMessage();
                                Toast.makeText(ProfielActivity.this, fout, Toast.LENGTH_SHORT).show();

                            }
                            profielZendKnop.setEnabled(true);
                        }
                    });

                }
            }


        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        if ( mHuidigGeb == null){
            sendToStart();
        } else {
            gebDatabase.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mHuidigGeb != null){
            gebDatabase.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart(){
        Intent startIntent = new Intent(ProfielActivity.this, Startactivity.class);
        startActivity(startIntent);
        finish();
    }

}
