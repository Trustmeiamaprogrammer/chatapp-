package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class InstellingenActivity extends AppCompatActivity {
    private DatabaseReference mGebDatabase;
    private FirebaseUser mHuidigeGebruiker;

    // CircleImageView
    private CircleImageView mToonAfbeelding;

    // 2 TextView maken
    private TextView mNaam;
    private TextView mStatus;

    // 2 Knoppen maken
    private Button mStatusKnop;
    private Button mAfbeeldingKnop;

    private static final int GALLERY_PICK =1;
    private StorageReference mAfbeeldingOpslag;

    private ProgressDialog mProcessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instellingen);
        mToonAfbeelding = (CircleImageView) findViewById(R.id.instelling_afbeelding);
        // ID aanmaken
        mNaam = (TextView) findViewById(R.id.instellingenNaam);
        mStatus = (TextView) findViewById(R.id.intellingenStatus);

        // ID aanmaken
        mStatusKnop = (Button) findViewById(R.id.instellingenStatusKnop);
        mAfbeeldingKnop = (Button) findViewById(R.id.instellingenAfbeeldingKnop);

        mAfbeeldingOpslag = FirebaseStorage.getInstance().getReference();
        mHuidigeGebruiker = FirebaseAuth.getInstance().getCurrentUser();

        String huidigeGebUid = mHuidigeGebruiker.getUid();

        mGebDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(huidigeGebUid);
        mGebDatabase.keepSynced(true);

        mGebDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String naam = dataSnapshot.child("name").getValue().toString();
                // Afbeelding moet nog
                String status = dataSnapshot.child("status").getValue().toString();
                // thumb_afbeelding moet nog

                mNaam.setText(naam);
                mStatus.setText(status);

                // Code voor afbeelding
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    mStatusKnop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String statusInhoud = mStatus.getText().toString();
            // Statusklasse aanmaken
            Intent statusIntent = new Intent(InstellingenActivity.this, StatusActivity.class);
            statusIntent.putExtra("statusInhoud", statusInhoud);
            startActivity(statusIntent);
        }
    });

    mAfbeeldingKnop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent gallerijIntet = new Intent();
            gallerijIntet.setType("afbeelding/");
            gallerijIntet.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(gallerijIntet, "Selecteer afbeelding", GALLERY_PICK));
        }
    });
    }
    @Override
    protected void onActivityResult(int verzoekCode, int resultaatCode, Intent data)
    {
        super.onActivityResult(verzoekCode, resultaatCode, data);
        if (verzoekCode == GALLERY_PICK && resultaatCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            // CODE OVER AFBEELDING
        }
    }
}
