package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class GesprekActivity extends AppCompatActivity {

    private String mGesGebruiker;
    private Toolbar mGesToolbar;
    private DatabaseReference mHoofdRef;
    private TextView mTitelView;
    private TextView mLaatstGezienView;
    private CircleImageView mProfielFoto;
    private FirebaseAuth mAuth;
    private String mHuidigeGebId;
    private ImageButton mGesVoegtoeKnop;
    private ImageButton mGesZendKnop;
    private EditText mGesBerView;
    private RecyclerView mBerlijst;
    private SwipeRefreshLayout mVerversLayout;

    // klasse berichten aanmaken
    private final List<Berichten> berichtenlist = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    // Klasse BerichtenAdapter maken
    private BerichtenAdapter mAdapter;
    private static final int AANTAL_ITEMS_LADEN = 10;
    private int mHuidigePag = 1;
    private static final int GALLERIJ_FOTO = 1;
    private StorageReference mAfbeeldingOplag;

    private int itemPos = 0;
    private String mLaatstKey = "";
    private String mVorigKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesprek);

        mGesToolbar = (Toolbar) findViewById(R.id.gesprek_app_bar);

        setSupportActionBar(mGesToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        mHoofdRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebId = mAuth.getCurrentUser().getUid();
        // Staat goed in DB?
        mGesGebruiker = getIntent().getStringExtra("GebId");
        String naamGeb = getIntent().getStringExtra("GebNaam");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.geprek_cus_bar, null);

        actionBar.setCustomView(actionBarView);

        mTitelView = (TextView) findViewById(R.id.cus_bar_titel);
        mLaatstGezienView = (TextView) findViewById(R.id.cus_bar_gezien);
        mProfielFoto = (CircleImageView) findViewById(R.id.cust_bar_afbeelding);

        mGesVoegtoeKnop = (ImageButton) findViewById(R.id.gesprekToevoegKnop);
        mGesZendKnop = (ImageButton) findViewById(R.id.berichtVerzendKnop);
        mGesBerView = (EditText) findViewById(R.id.gesprek_berichten_view);

        mAdapter = new BerichtenAdapter(berichtenlist);

        mBerlijst = (RecyclerView) findViewById(R.id.berichten_lijst);
        mVerversLayout = (SwipeRefreshLayout) findViewById(R.id.bericht_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mBerlijst.setHasFixedSize(true);
        mBerlijst.setLayoutManager(mLinearLayout);

        mBerlijst.setAdapter(mAdapter);

        mAfbeeldingOplag = FirebaseStorage.getInstance().getReference();
        mHoofdRef.child("Gesprek").child(mHuidigeGebId).child(mGesGebruiker).child("Gezien").setValue(true);

        laadBerichten();

        mTitelView.setText(naamGeb);

        mHoofdRef.child("Gebruikers").child(mGesGebruiker).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("Online").getValue().toString();
                String image = dataSnapshot.child("Afbeelding").getValue().toString();

                if (online.equals("true")) {
                    mLaatstGezienView.setText("Online");

                } else {
                    GetTimeAgo getTimeago = new GetTimeAgo();
                    long laatstTijd = Long.parseLong(online);

                    String laatstGezienTijd = getTimeago.getTimeAgo(laatstTijd, getApplicationContext());

                    mLaatstGezienView.setText(laatstGezienTijd);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mHoofdRef.child("Gesprek").child(mHuidigeGebId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mGesGebruiker)) {
                    Map gesVoegtoeMap = new HashMap();
                    gesVoegtoeMap.put("Gezien", false);
                    gesVoegtoeMap.put("Timestamp", ServerValue.TIMESTAMP);

                    Map gesGebMap = new HashMap();
                    gesGebMap.put("Gesprek/" + mHuidigeGebId + "/" + mGesGebruiker, gesVoegtoeMap);
                    gesGebMap.put("Gesprek/" + mGesGebruiker + "/" + mHuidigeGebId, gesVoegtoeMap);

                    mHoofdRef.updateChildren(gesGebMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                System.out.println("GESPREKLOG GESPREK ACTIVITTY >>>>>>" + databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mGesZendKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Misschien toch de Engelse sendMessage()?
                zendBericht();
            }
        });

        mGesVoegtoeKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallerijIntent = new Intent();

                // KLOPT DIT OOK?
                gallerijIntent.setType("Afbeelding/*");
                gallerijIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallerijIntent, "SELECTEER AFBEELDING"), GALLERIJ_FOTO);
            }
        });

        mVerversLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHuidigePag++;
                itemPos = 0;
                // NIET IN ENGELS?
                laadMeerBerichten();
            }
        });
    }
@Override
        protected void onActivityResult(int verzoekCode, int resultaatCode, Intent data)
{
    super.onActivityResult(verzoekCode, resultaatCode, data);
    if (verzoekCode == GALLERIJ_FOTO && resultaatCode == RESULT_OK)
    {
        Uri imageUri = data.getData();

        final String huidigGebRef = "Berichten/" + mHuidigeGebId + "/" + mGesGebruiker;
        final String gesGebRef = "Berichten/" + mGesGebruiker + "/" + mHuidigeGebId;
        final DatabaseReference gebGesPush = mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).push();
        final String pushId = gebGesPush.getKey();
        StorageReference bestandpad = mAfbeeldingOplag.child("BerichtAfbeelding").child(pushId + ".jpg");

        bestandpad.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                {
                    String downloadUri = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    Map berichtMap = new HashMap();
                    berichtMap.put("Bericht", downloadUri);
                    berichtMap.put("Gezien", false);
                    berichtMap.put("Type", "Afbeelding");
                    berichtMap.put("Tijd", ServerValue.TIMESTAMP);
                    berichtMap.put("Van", mHuidigeGebId);

                    Map berichtGebMap = new HashMap();
                    berichtGebMap.put(huidigGebRef + "/" + pushId, berichtMap);
                    berichtGebMap.put(gebGesPush + "/" + pushId, berichtMap);

                    mGesBerView.setText("");

                    mHoofdRef.updateChildren(berichtGebMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError != null)
                            {
                                System.out.println("FOUT GESPREK ACTIVITY >>> " + databaseError.getMessage());
                            }
                        }
                    });



                }
            }
        });

    }


}

private void laadMeerBerichten()
{
    DatabaseReference berRef = mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker);
    Query berQuery = berRef.orderByKey().endAt(mLaatstKey).limitToLast(10);
    berQuery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Berichten bericht = dataSnapshot.getValue(Berichten.class);
            String berichtKey = dataSnapshot.getKey();

            if(!mVorigKey.equals(berichtKey))
            {
                berichtenlist.add(itemPos++, bericht);
            }
            else
            {
                mVorigKey = mLaatstKey;

            }

            if (itemPos ==1)
            {
                mLaatstKey = berichtKey;

            }

            System.out.println( mLaatstKey + " | " + mVorigKey + " | " + berichtKey);

            mAdapter.notifyDataSetChanged();

            mVerversLayout.setRefreshing(false);
            mLinearLayout.scrollToPositionWithOffset(10, 0);

        }



        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}

private void laadBerichten()
{
    DatabaseReference berRef = mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker);

    Query berQuery = berRef.limitToLast(mHuidigePag * AANTAL_ITEMS_LADEN);
    berQuery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Berichten bericht = dataSnapshot.getValue(Berichten.class);
            itemPos ++;
            if (itemPos == 1)
            {
                String berichtKey = dataSnapshot.getKey();
                mLaatstKey = berichtKey;
                mVorigKey = berichtKey;
            }

            berichtenlist.add(bericht);
            mAdapter.notifyDataSetChanged();
            mBerlijst.scrollToPosition(berichtenlist.size() - 1);

            mVerversLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}

private void zendBericht()
{
    String bericht = mGesBerView.getText().toString();

    if(!TextUtils.isEmpty(bericht))
    {
        String huidigGebRef = "Berichten/" + mHuidigeGebId + "/" + mGesGebruiker;
        String gesGebRef = "Berichten/" + mGesGebruiker + "/" + mHuidigeGebId;
        DatabaseReference huidigGesPush = mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).push();

        String pushId = huidigGesPush.getKey();

        Map berichtMap = new HashMap();
        berichtMap.put("Bericht", bericht);
        berichtMap.put("Gezien", false);
        berichtMap.put("Type", "Tekst");
        berichtMap.put("Tijd", ServerValue.TIMESTAMP);
        berichtMap.put("Van", mHuidigeGebId);

        Map berichtGebMap = new HashMap();
        berichtGebMap.put(huidigGebRef + "/" + pushId, berichtMap);
        berichtGebMap.put(gesGebRef + "/" + pushId, berichtMap);

        mGesBerView.setText("");

        mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).child("Gezien").setValue(true);
        mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).child("Timestamp").setValue(ServerValue.TIMESTAMP);

        mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).child("Gezien").setValue(false);
        mHoofdRef.child("Berichten").child(mHuidigeGebId).child(mGesGebruiker).child("Timestamp").setValue(ServerValue.TIMESTAMP);
        mHoofdRef.updateChildren(berichtGebMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError != null)
                {
                    System.out.println("FOUT GESPREK ACTIVITY ZEND BERICHT>>>> " + databaseError.getMessage());
                }
            }
        });

    }
}
    }

