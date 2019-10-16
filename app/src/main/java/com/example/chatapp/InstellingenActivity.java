package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import id.zelory.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class InstellingenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
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
        mNaam = (TextView) findViewById(R.id.instellingenNaam);
        mStatus = (TextView) findViewById(R.id.intellingenStatus);

        mStatusKnop = (Button) findViewById(R.id.instellingenStatusKnop);
        mAfbeeldingKnop = (Button) findViewById(R.id.instellingenAfbeeldingKnop);

        mAfbeeldingOpslag = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebruiker = FirebaseAuth.getInstance().getCurrentUser();

        String huidigeGebUid = mHuidigeGebruiker.getUid();

        mGebDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(huidigeGebUid);
        mGebDatabase.keepSynced(true);

        mGebDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String naam = dataSnapshot.child("Naam").getValue().toString();
                final String afbeelding = dataSnapshot.child("Afbeelding").getValue().toString();
                String status = dataSnapshot.child("Status").getValue().toString();
                String thumbAfbeelding = dataSnapshot.child("ThumbAfb").getValue().toString();

               mNaam.setText(naam);
               mStatus.setText(status);
                if(!afbeelding.equals("default")) {

                    //Picasso.with(InstellingenActivity.this).load(afbeelding).placeholder(R.mipmap.ic_launcher_round).into(mToonAfbeelding);

                    Picasso.with(InstellingenActivity.this).load(afbeelding).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.mipmap.ic_launcher_round).into(mToonAfbeelding, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                            Picasso.with(InstellingenActivity.this).load(afbeelding).placeholder(R.mipmap.ic_launcher_round).into(mToonAfbeelding);

                        }
                    });

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    mStatusKnop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String statusInhoud = mStatus.getText().toString();
            Intent statusIntent = new Intent(InstellingenActivity.this, StatusActivity.class);
            statusIntent.putExtra("StatusInhoud", statusInhoud);
            startActivity(statusIntent);
        }
    });

    mAfbeeldingKnop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent gallerijIntet = new Intent();
            gallerijIntet.setType("Afbeelding/*");
            gallerijIntet.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(gallerijIntet, "Selecteer afbeelding"), GALLERY_PICK);
        }
    });
    }

    @Override
    protected void onStart(){
        super.onStart();
        if (mHuidigeGebruiker == null){
            sendToStart();
        }

        else{
            mGebDatabase.child("Online").setValue("true");
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(mHuidigeGebruiker != null){
            mGebDatabase.child("Online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void sendToStart(){
        Intent startIntent = new Intent(InstellingenActivity.this, Startactivity.class);
        startActivity(startIntent);
        finish();
    }
    @Override
    protected void onActivityResult(int verzoekCode, int resultaatCode, Intent data)
    {
        super.onActivityResult(verzoekCode, resultaatCode, data);
        if (verzoekCode == GALLERY_PICK && resultaatCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (verzoekCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult resultaat = CropImage.getActivityResult(data);
            if (resultaatCode == RESULT_OK) {
                mProcessDialog = new ProgressDialog(InstellingenActivity.this);
                mProcessDialog.setTitle("Afbeelding uploaden..");
                mProcessDialog.setMessage("Wacht terwijl uw afbeelding geupload wordt.");
                mProcessDialog.setCanceledOnTouchOutside(false);
                mProcessDialog.show();

                try {

                    Uri resultaatUri = resultaat.getUri();
                    final File afbeelding_pad = new File(resultaatUri.getPath());
                    String huidigeGebId = mHuidigeGebruiker.getUid();

                    Bitmap thumb_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(afbeelding_pad);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    final byte[] thumb_byte = baos.toByteArray();


                    final StorageReference bestandpad = mAfbeeldingOpslag.child("ProfielFotos").child(huidigeGebId + ".jpg");
                    final StorageReference afbeelding_path = mAfbeeldingOpslag.child("ProfielFotos").child("Thumbs").child(huidigeGebId + ".jpg");

                    final UploadTask uploadTask = bestandpad.putFile(resultaatUri);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadURL = uri.toString();
                                    final UploadTask uploadTask1 = afbeelding_path.putBytes(thumb_byte);

                                    uploadTask1.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot thumbTask) {

                                            thumbTask.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri thumbUri) {

                                                    String thumbDownloadUrl = thumbUri.toString();

                                                    Map updateHashMap = new HashMap();
                                                    updateHashMap.put("Afbeelding", downloadURL);
                                                    updateHashMap.put("Thumb", thumbDownloadUrl);

                                                    mGebDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                mProcessDialog.dismiss();

                                                                Toast.makeText(InstellingenActivity.this, "Succesvolle upload", Toast.LENGTH_LONG).show();
                                                            } else {
                                                                mProcessDialog.dismiss();
                                                                Toast.makeText(InstellingenActivity.this, "Fout bij uploaden", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (resultaatCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception fout = resultaat.getError();
            }

        }}



    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLengte = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLengte; i++ ){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);

        }
        return randomStringBuilder.toString();
    }

}
