package com.example.chatapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GesprekActivity extends AppCompatActivity {

    private String mGesGebruiker;
    private Toolbar mGesToolbar;
    private DatabaseReference mHoofdRef;
    private TextView mTitelView;
    private TextView mLaatstGezien;
    private CircleImageView mProfielFoto;
    private FirebaseAuth mAuth;
    private String mHuidigeGebId;
    private ImageButton mGesVoegtoeKnop;
    private ImageButton mGesZendKnop;
    private EditText mGesBerView;
    private RecyclerView mBerlijst;
    private SwipeRefreshLayout mVerversLayout;

    // klasse berichten aanmaken
    private final List <Berichten> berichtenlist = new ArrayList<>();
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

        mGesToolbar = (Toolbar) findViewById(R.id.ges_app_bar);

        setSupportActionBar(mGesToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mHoofdRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mHuidigeGebId = mAuth.getCurrentUser().getUid();
        // Staat goed in DB?
        mGesGebruiker = getIntent().getStringExtra("Gebruikers_id");
        String naamGeb = getIntent().getStringExtra("Gebruiker_naam");
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.ges_cus_bar, null);

        actionBar.setCustomView(actionBarView);

        mTitelView = (TextView) findViewById(R.id.ges_cus_bar_titel);
        mLaatstGezien = (TextView) findViewById(R.id.ges_cus_bar_gezien);
        mProfielFoto = (CircleImageView)  findViewById(R.id.ges_cus_bar_foto);

        mGesVoegtoeKnop = (ImageButton) findViewById(R.id.ges_voegtoe_knop);
        mGesZendKnop = (ImageButton) findViewById(R.id.ges_zend_knop);
        mGesBerView = (EditText) findViewById(R.id.ges_ber_view);
        // Onsecuur hier!
        mAdapter = new BerichtAdapter (berichtenlist);

        mBerlijst = (RecyclerView) findViewById(R.id.ges_ber_lijst);
        mVerversLayout = (SwipeRefreshLayout) findViewById(R.id.ges_ber_swipelayout);
        mLinearLayout = new LinearLayoutManager(this);
        mBerlijst.setHasFixedSize(true);
        mBerlijst.setLayoutManager(mLinearLayout);

        mBerlijst.setAdapter(mAdapter);

        mAfbeeldingOplag = FirebaseStorage.getInstance().getReference();
        mHoofdRef.child("gesprek").child(mHuidigeGebId).child(mGesGebruiker).child("Gezien").setValue(true);

        laadBerichten();






    }
}
