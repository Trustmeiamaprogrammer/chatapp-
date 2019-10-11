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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import id.zelory.compressor.Compressor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
                final String afbeelding = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                // thumb_afbeelding moet nog
                String thumb_afbeelding = dataSnapshot.child("thumb_image").getValue().toString();

                mNaam.setText(naam);
                mStatus.setText(status);
                //klopt "default?"
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
            gallerijIntet.setType("afbeelding/*");
            gallerijIntet.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(gallerijIntet, "Selecteer afbeelding"), GALLERY_PICK);
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

                //Nederlands?
                StorageReference bestandpad = mAfbeeldingOpslag.child("profile_images").child(huidigeGebId + ".jpg");
                final StorageReference afbeelding_path = mAfbeeldingOpslag.child("profile_images").child("thumbs").child(huidigeGebId + ".jpg");

                bestandpad.putFile(resultaatUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String download_url = task.getResult().getMetadata().getReference().getDownloadUrl().toString();
                            UploadTask uploadTaak = afbeelding_pad.putBytes(thumb_byte);

                            uploadTaak.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("afbeelding", download_url);
                                        update_hashMap.put("thumb_afbeelding", thumb_downloadUrl);

                                        mGebDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if(task.isSuccessful()){
                                                    mProcessDialog.dismiss();
                                                    Toast.makeText(InstellingenActivity.this, "Succesvol upload", Toast.LENGTH_LONG).show();



                                                }

                                            }
                                        });

                                    } else {
                                        Toast.makeText(InstellingenActivity.this, "Error in uploading thumnail", Toast.LENGTH_LONG).show();
                                        mProcessDialog.dismiss();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(InstellingenActivity.this, "Error in uploading", Toast.LENGTH_LONG).show();
                            mProcessDialog.dismiss();
                        }
                    }
                });
            } else if (resultaatCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = resultaat.getError();

            }
        }
    }
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
