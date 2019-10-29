package com.example.chatapp;
// Importeer
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
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class GesprekActivity extends AppCompatActivity {

    // Variabelen
    private String mGesGebruiker;
    private Toolbar mGesToolbar;
    private DatabaseReference mHoofdRef;
    private DatabaseReference notDatabase;
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
    private final List<Berichten> berichtenList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private BerichtenAdapter mAdapter;
    private static final int AANTAL_ITEMS_LADEN = 10;
    private int mHuidigePag = 1;
    private static final int GALLERIJ_FOTO = 1;
    private StorageReference mAfbeeldingOplag;
    private int itemPos = 0;
    private String mLaatstKey = "";
    private String mVorigKey = "";

    // Wanneer de activity wordt gemaakt...
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_gesprek);

        // Layout voor app bar
        mGesToolbar = findViewById(R.id.gesprek_app_bar);
        setSupportActionBar(mGesToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        // Referentie, Gebruiker met UID
        mHoofdRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebId = mAuth.getCurrentUser().getUid();
        // Haal extra gegevens op
        mGesGebruiker = getIntent().getStringExtra("gebId");
        String naamGeb = getIntent().getStringExtra("naamGeb");
        // Layout voor custom bar
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.geprek_cus_bar, null);
        actionBar.setCustomView(actionBarView);
        mTitelView = findViewById(R.id.cus_bar_titel);
        mLaatstGezienView =  findViewById(R.id.cus_bar_gezien);
        mProfielFoto = findViewById(R.id.cust_bar_afbeelding);

        // Knoppen toevoegen
        mGesVoegtoeKnop = findViewById(R.id.gesprekToevoegKnop);
        mGesZendKnop =  findViewById(R.id.berichtVerzendKnop);
        mGesBerView =  findViewById(R.id.gesprek_berichten_view);

        // Initialiseer variabelen
        mAdapter = new BerichtenAdapter(berichtenList);

        // Layout voor lijst
        mBerlijst =  findViewById(R.id.berichten_lijst);
        mVerversLayout = findViewById(R.id.bericht_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);
        mBerlijst.setHasFixedSize(true);
        mBerlijst.setLayoutManager(mLinearLayout);

        mBerlijst.setAdapter(mAdapter);
        // Referentie voor afbeelding opslag, gesprekken gezien en notificaties.
        mAfbeeldingOplag = FirebaseStorage.getInstance().getReference();
        mHoofdRef.child("gesprek").child(mHuidigeGebId).child(mGesGebruiker).child("gezien").setValue(true);
        notDatabase = FirebaseDatabase.getInstance().getReference().child("notificaties");

        // Roep methode aan
        laadBerichten();

        // Zet de titel in de naam van de gebruiker.
        mTitelView.setText(naamGeb);

        // Haal data op van de gebruiker
        mHoofdRef.child("gebruikers").child(mGesGebruiker).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String online = dataSnapshot.child("online").getValue().toString();
                final String image = dataSnapshot.child("afbeelding").getValue().toString();
                if(!image.equals("default")){
                    Picasso.with(GesprekActivity.this).load(image)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_launcher_foreground)
                     .into(mProfielFoto, new Callback() {
                         @Override
                         public void onSuccess() { }
                         @Override
                         public void onError() {
                             Picasso.with(GesprekActivity.this).load(image)
                                     .placeholder(R.drawable.ic_launcher_foreground)
                                     .into(mProfielFoto);}});
                }
                // Als gebruiker online is
                if (online.equals("true")) {
                    mLaatstGezienView.setText("online");

                } else {
                    // Bereken Tijdseenheden om in milliseconden en geef dit weer
                    GetTimeAgo getTimeago = new GetTimeAgo();
                    long laatstTijd = Long.parseLong(online);

                    String laatstGezienTijd = getTimeago.getTimeAgo(laatstTijd, getApplicationContext());
                    mLaatstGezienView.setText(laatstGezienTijd); }
            }

            @Override
            public void onCancelled( DatabaseError databaseError) { }});

        // Plaats data in gesprek
        mHoofdRef.child("gesprek").child(mHuidigeGebId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(mGesGebruiker)) {
                    // Als de gebruiker al in gesprek staat...
                    Map gesVoegtoeMap = new HashMap();
                    gesVoegtoeMap.put("gezien", false);
                    gesVoegtoeMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map gesGebMap = new HashMap();
                    gesGebMap.put("gesprek/" + mHuidigeGebId + "/" + mGesGebruiker, gesVoegtoeMap);
                    gesGebMap.put("gesprek/" + mGesGebruiker + "/" + mHuidigeGebId, gesVoegtoeMap);

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
            public void onCancelled(DatabaseError databaseError) { }});

        // Zend knop
        mGesZendKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zendBericht();
            }
        });

        // Knop voor afbeedlingen te verzenden
        mGesVoegtoeKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallerijIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallerijIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallerijIntent, "SELECTEER AFBEELDING"), GALLERIJ_FOTO);
            }
        });
        mVerversLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHuidigePag++;
                itemPos = 0;
                laadMeerBerichten();
            }
        });
    }


@Override
        protected void onActivityResult(int verzoekCode, int resultaatCode, Intent data) {
    super.onActivityResult(verzoekCode, resultaatCode, data);
// Haal de afbeelding op
    if (verzoekCode == GALLERIJ_FOTO && resultaatCode == RESULT_OK) {
        Uri imageUri = data.getData();
        CropImage.activity(imageUri).setAspectRatio(1, 1).start(this);
    }

        if (verzoekCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {


            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultaatCode == RESULT_OK) {
                // Voeg de afbeelding toe op de juiste plaats
                Uri resultUri = result.getUri();
                final String huidigGebRef = "berichten/" + mHuidigeGebId + "/" + mGesGebruiker;
                final String gesGebRef = "berichten/" + mGesGebruiker + "/" + mHuidigeGebId;
                final DatabaseReference gebGesPush = mHoofdRef.child("berichten")
                        .child(mHuidigeGebId).child(mGesGebruiker).push();
                final String pushId = gebGesPush.getKey();
                StorageReference bestandpad = mAfbeeldingOplag.child("berichtAfbeelding").child(pushId + ".jpg");

                // Upload de afbeelding
                final UploadTask uploadTask = bestandpad.putFile(resultUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String downloadUri = uri.toString();
                                DatabaseReference nNotRef = mHoofdRef.child("notificaties").child(mGesGebruiker).push();
                                String nNotId = nNotRef.getKey();
                                // Zorg ervoor dat de ontvanger de afbeelding ontvangt met notificatie
                                HashMap<String, String> notData = new HashMap<>();
                                notData.put("van", mHuidigeGebId);
                                notData.put("type", "afbeelding");

                                Map berichtMap = new HashMap();
                                berichtMap.put("bericht", downloadUri);
                                berichtMap.put("gezien", false);
                                berichtMap.put("type", "afbeelding");
                                berichtMap.put("tijd", ServerValue.TIMESTAMP);
                                berichtMap.put("van", mHuidigeGebId);

                                Map berichtGebMap = new HashMap();
                                berichtGebMap.put(huidigGebRef + "/" + pushId, berichtMap);
                                berichtGebMap.put(gesGebRef + "/" + pushId, berichtMap);
                                berichtGebMap.put("notificaties/" + "berichten/" + mGesGebruiker + "/" + nNotId, notData);

                                mGesBerView.setText("");

                                mHoofdRef.updateChildren(berichtGebMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            System.out.println("FOUT GESPREK ACTIVITY >>> " + databaseError.getMessage());
                                        } }}); }}); }}); } } }

private void laadMeerBerichten()
{
    DatabaseReference berRef = mHoofdRef.child("berichten").child(mHuidigeGebId).child(mGesGebruiker);
    Query berQuery = berRef.orderByKey().endAt(mLaatstKey).limitToLast(10);
    berQuery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded( DataSnapshot dataSnapshot, String s) {
            Berichten bericht = dataSnapshot.getValue(Berichten.class);
            String berichtKey = dataSnapshot.getKey();

            if(!mVorigKey.equals(berichtKey))
            {
                berichtenList.add(itemPos++, bericht);
            }
            else
            {
                mVorigKey = mLaatstKey;
            }

            if (itemPos == 1)
            {
                mLaatstKey = berichtKey;

            }

            mAdapter.notifyDataSetChanged();
            mVerversLayout.setRefreshing(false);
            mLinearLayout.scrollToPositionWithOffset(10, 0);
        }

        @Override
        public void onChildChanged( DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved( DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}

private void laadBerichten() {
    DatabaseReference berRef = mHoofdRef.child("berichten").child(mHuidigeGebId).child(mGesGebruiker);
    Query berQuery = berRef.limitToLast(mHuidigePag * AANTAL_ITEMS_LADEN);

    berQuery.addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Berichten bericht = dataSnapshot.getValue(Berichten.class);

            itemPos++;

            if (itemPos == 1) {
                String berKey = dataSnapshot.getKey();
                mLaatstKey = berKey;
                mVorigKey = berKey;
            }

            berichtenList.add(bericht);
            mAdapter.notifyDataSetChanged();
            mBerlijst.scrollToPosition(berichtenList.size() - 1);
            mVerversLayout.setRefreshing(false);
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }}); }

private void zendBericht()
{
    String bericht = mGesBerView.getText().toString();

    if(!TextUtils.isEmpty(bericht)) {
        String huidigGebRef = "berichten/" + mHuidigeGebId + "/" + mGesGebruiker;
        String gesGebRef = "berichten/" + mGesGebruiker + "/" + mHuidigeGebId;
        DatabaseReference huidigGesPush = mHoofdRef.child("berichten").child(mHuidigeGebId).child(mGesGebruiker).push();
        DatabaseReference nNotRef = mHoofdRef.child("notificaties").child(mGesGebruiker).push();
        String nNotId = nNotRef.getKey();

        String pushId = huidigGesPush.getKey();

        HashMap<String, String> notData = new HashMap<>();
        notData.put("van", mHuidigeGebId);
        notData.put("type", "tekst");

        Map berichtMap = new HashMap();
        berichtMap.put("bericht", bericht);
        berichtMap.put("gezien", false);
        berichtMap.put("type", "tekst");
        berichtMap.put("tijd", ServerValue.TIMESTAMP);
        berichtMap.put("van", mHuidigeGebId);

        Map berichtGebMap = new HashMap();
        berichtGebMap.put(huidigGebRef + "/" + pushId, berichtMap);
        berichtGebMap.put(gesGebRef + "/" + pushId, berichtMap);
        berichtGebMap.put("notificaties/" + "berichten/" + mGesGebruiker + "/" + nNotId, notData);

        mGesBerView.setText("");

        mHoofdRef.child("gesprek").child(mHuidigeGebId).child(mGesGebruiker).child("gezien").setValue(true);
        mHoofdRef.child("gesprek").child(mHuidigeGebId).child(mGesGebruiker).child("timestamp").setValue(ServerValue.TIMESTAMP);


        mHoofdRef.child("gesprek").child(mGesGebruiker).child(mHuidigeGebId).child("gezien").setValue(false);
        mHoofdRef.child("gesprek").child(mGesGebruiker).child(mHuidigeGebId).child("timestamp").setValue(ServerValue.TIMESTAMP);
        mHoofdRef.updateChildren(berichtGebMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("FOUT GESPREK ACTIVITY ZEND BERICHT>>>> " + databaseError.getMessage());
                }
            }
        });
        }
    }
}

