package com.example.chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class ProfielActivity extends AppCompatActivity {

    /// {} of () staan niet overal goed. ID's voor xml moeten aangemaakt worden.

    private ImageView profielFoto;
    private TextView profielNaam, profielStatus, profielVriendenAantal;
    private Button profielZendKnop, profielWeigerKnop;

    private DatabaseReference gebDatabase;
    private DatabaseReference verzoekDatabase;
    private DatabaseReference vriendDatabase;
    private DatabaseReference notDatabase;

    private DatabaseReference mHuidigRef;
    private FirebaseUser mHuidigGeb;

    private String huidigState;

    private ProgressDialog mProcessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiel);

        final String gebId = getIntent().getStringExtra("gebruikersId");
        mHuidigRef = FirebaseDatabase.getInstance().getReference();
        gebDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(gebId);
        verzoekDatabase = FirebaseDatabase.getInstance().getReference().child("VriendVer");
        vriendDatabase = FirebaseDatabase.getInstance().getReference().child("Vrienden");
        notDatabase = FirebaseDatabase.getInstance().getReference().child("Notificaties");
        mHuidigGeb = FirebaseAuth.getInstance().getCurrentUser();

        profielFoto = (ImageView) findViewById(R.id.profielAfbeelding);
        profielNaam = (TextView) findViewById(R.id.profielnaam);
        profielStatus = (TextView) findViewById(R.id.profielStatus);
        profielVriendenAantal = (TextView) findViewById(R.id.profielAantal);
        profielZendKnop = (Button) findViewById(R.id.ProfielZendKnop);
        profielWeigerKnop = (Button) findViewById(R.id.ProfielWeigerKnop);

        huidigState = "Geenvrienden";
        profielWeigerKnop.setVisibility(View.INVISIBLE);
        profielWeigerKnop.setEnabled(false);


        mProcessDialog = new ProgressDialog(this);
        mProcessDialog.setTitle("Laden... geb_data");
        mProcessDialog.setMessage("Even geduld...");
        mProcessDialog.setCanceledOnTouchOutside(false);
        mProcessDialog.show();


        gebDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gebruikersnaam = dataSnapshot.child("Naam").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String afbeelding = dataSnapshot.child("Afbeelding").getValue().toString();

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
                            String ver_type = dataSnapshot.child(gebId).child("VerType").getValue().toString();

                            if (ver_type.equals("Ontvangen")) {
                                huidigState = "VerOntvang";
                                profielZendKnop.setText("Verzoek accepteren");

                                profielWeigerKnop.setVisibility(View.VISIBLE);
                                profielWeigerKnop.setEnabled(true);

                            } else if (ver_type.equals("Verzonden")) {
                                huidigState = "VerStuurd";

                                profielZendKnop.setText("Annuleren");
                                profielWeigerKnop.setVisibility(View.INVISIBLE);
                                profielWeigerKnop.setEnabled(false);

                            }
                            mProcessDialog.dismiss();


                        } else {
                            vriendDatabase.child(mHuidigGeb.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(gebId)) {

                                        huidigState = "Vrienden";
                                        profielZendKnop.setText("Ontvrienden");
                                        profielWeigerKnop.setVisibility(View.INVISIBLE);
                                        profielWeigerKnop.setEnabled(false);

                                    }
                                    mProcessDialog.dismiss();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProcessDialog.dismiss();

                                }
                            });
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

        profielZendKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profielZendKnop.setEnabled(false);

                DatabaseReference nNotRef = mHuidigRef.child("Notificaties").child(gebId).push();
                String nNotId = nNotRef.getKey();

                HashMap<String, String> notData = new HashMap<>();
                notData.put("Van", mHuidigGeb.getUid());
                notData.put("Type", "Verzoek");
                Map verMap = new HashMap<>();
                verMap.put("VriendVer/" + mHuidigGeb.getUid() + "/" + gebId + "/VerType", "Verzonden");
                verMap.put("VriendVer/" + gebId + "/" + mHuidigGeb.getUid() + "/VerType", "Verzonden");
                verMap.put("Notificaties/" + gebId + "/" + nNotId, notData);

                mHuidigRef.updateChildren(verMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Toast.makeText(ProfielActivity.this, "Verzoek versturen niet geukt.", Toast.LENGTH_SHORT).show();
                        } else {

                            huidigState = "VerStuurd";
                            profielZendKnop.setText("Annuleer");
                        }
                        profielZendKnop.setEnabled(true);
                    }
                });

                if(huidigState.equals("VerStuurd")){
                    verzoekDatabase.child(mHuidigGeb.getUid()).child(gebId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            verzoekDatabase.child(gebId).child(mHuidigGeb.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    profielZendKnop.setEnabled(true);
                                    huidigState = "Geenvrienden";
                                    profielZendKnop.setText("Verzoek verstuurd");

                                    profielWeigerKnop.setVisibility(View.INVISIBLE);
                                    profielWeigerKnop.setEnabled(false);

                                }
                            });

                        }
                    });
                }

                if(huidigState.equals("VerOntvang")){
                    final String huidigDatum = DateFormat.getDateTimeInstance().format(new Date());

                    Map vriendMap = new HashMap();
                    vriendMap.put("Vrienden/" + mHuidigGeb.getUid() + "/" + gebId + "/Datum", huidigDatum);
                    vriendMap.put("Vrienden/" + gebId + "/" + mHuidigGeb.getUid() + "/Datum", huidigDatum);
                    vriendMap.put("VriendVer/" + mHuidigGeb.getUid() + "/" + gebId, null);
                    vriendMap.put("VriendVer/" + gebId + "/" + mHuidigGeb.getUid(), null);

                    mHuidigRef.updateChildren(vriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                profielZendKnop.setEnabled(true);
                                huidigState ="Vrienden";
                                profielZendKnop.setText("Ontvrient");

                                profielWeigerKnop.setVisibility(View.INVISIBLE);
                                profielWeigerKnop.setEnabled(false);


                            } else {
                                String fout = databaseError.getMessage();
                                Toast.makeText(ProfielActivity.this, fout, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                if(huidigState.equals("Vrienden")){
                    Map ontvriendMap = new HashMap();
                    ontvriendMap.put("Vrienden/"+ mHuidigGeb.getUid() + "/" + gebId, null);
                    ontvriendMap.put("Vrienden/"+ gebId + "/" + mHuidigGeb.getUid(), null);

                    mHuidigRef.updateChildren(ontvriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError == null){
                                huidigState = "Geenvrienden";
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


}
