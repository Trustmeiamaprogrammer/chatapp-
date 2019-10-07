package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;


public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewpager;
    private SectiePagerAdapter mSectiePagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mGebruikerRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.mainAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Chat app");

        if (mAuth.getCurrentUser() != null)
        {
            mGebruikerRef = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(mAuth.getCurrentUser().getUid());
        }

        // Tabbladen
        mViewpager = (ViewPager) findViewById(R.id.main_view);
        mSectiePagerAdapter = new SectiePagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mSectiePagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser huidigeGebruiker = mAuth.getCurrentUser();

        if (huidigeGebruiker == null)
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
            mGebruikerRef.child("online").setValue(ServerValue.TIMESTAMP);
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
