package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registreeractivity extends AppCompatActivity {

    private TextInputLayout mGebruikersnaam;
    private TextInputLayout mEmail;
    private TextInputLayout mWachtwoord;
    private Button mRegMaakKnop;


    private Toolbar mToolbar;

    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registreeractivity);

        mToolbar = (Toolbar) findViewById(R.id.regitreer_toolbar) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maak account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mRegProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

        mGebruikersnaam = (TextInputLayout) findViewById(R.id.regGebruikersnaam);
        mEmail = (TextInputLayout) findViewById(R.id.regEmail);
        mWachtwoord = (TextInputLayout) findViewById(R.id.regWachtwoord);
        mRegMaakKnop = (Button) findViewById(R.id.regMaakKnop);

        mRegMaakKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String gebruikersnaam = mGebruikersnaam.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mWachtwoord.getEditText().getText().toString();

                if(TextUtils.isEmpty(gebruikersnaam) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

                    Toast.makeText(Registreeractivity.this, "Voer de velden in.", Toast.LENGTH_LONG).show();


                }

                else
                {

                    mRegProgress.setTitle("Account aanmaken");
                    mRegProgress.setMessage("Even geduld...");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registreer_gebruiker(gebruikersnaam, email, password);
                }

            }
        });

    }

    private void registreer_gebruiker(String gebruikersnaam, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())

                {
                    mRegProgress.dismiss();
                    Intent hoofdIntent = new Intent(Registreeractivity.this, MainActivity.class);
                    finish();
                }

                else
                {
                    mRegProgress.hide();
                    Toast.makeText(Registreeractivity.this, "Fout", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
