package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity
    implements VerzoekFrag.OnFragmentInteractionListener,GesprekkenFrag.OnFragmentInteractionListener,VriendenFrag.OnFragmentInteractionListener{

    @Override
    public void onFragmentInteraction() {


    }

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private FirebaseUser huidigGeb;
    private ViewPager mViewpager;
    private SectiePagerAdapter mSectiePagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mGebruikerRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        mToolbar =  findViewById(R.id.mainAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat app");

        huidigGeb = mAuth.getCurrentUser();



        if (mAuth.getCurrentUser() != null)
        {
            String huidigGebId = huidigGeb.getUid();
            mGebruikerRef = FirebaseDatabase.getInstance().getReference().child("gebruikers").child(huidigGebId);
        }



        // Tabbladen
        mViewpager =  findViewById(R.id.main_view);
        mSectiePagerAdapter = new SectiePagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mSectiePagerAdapter);

        mTabLayout =  findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (huidigGeb == null)
        {
           sendToStart();
        }

        else
        {
            mGebruikerRef.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FirebaseUser huidigeGebruiker = mAuth.getCurrentUser();

        if(huidigeGebruiker != null)
        {
            mGebruikerRef.child("online").setValue(ServerValue.TIMESTAMP);
        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, Startactivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);

         getMenuInflater().inflate(R.menu.main_menu, menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.mainLoguitKnop)
        {
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId() == R.id.mainAinstKnop)
        {
            Intent instellingenIntent = new Intent(MainActivity.this, InstellingenActivity.class);
            startActivity(instellingenIntent);
        }

        if(item.getItemId() == R.id.mainGebruikersKnop)
        {
            Intent gebruikersIntent = new Intent(MainActivity.this, GebruikersActivity.class);
            startActivity(gebruikersIntent);
        }

        return true;
    }


}
