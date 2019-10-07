package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;


public class Registreeractivity extends AppCompatActivity {

    private TextInputLayout mGebruikersnaam;
    private TextInputLayout mEmail;
    private TextInputLayout mWachtwoord;
    private Button mRegMaakKnop;


    private Toolbar mToolbar;

    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registreeractivity);

        mToolbar = (Toolbar) findViewById(R.id.registreer_toolbar) ;
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

    private void registreer_gebruiker(final String gebruikersnaam, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful())

                {
                    FirebaseUser huidigeGebruiker = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = huidigeGebruiker.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(uid);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful())
                            {
                                System.out.println(task.getException());
                                return;
                            }

                            String apparaatToken = task.getResult().getToken();

                            HashMap<String, String> gebruikersMap = new HashMap<>();
                            gebruikersMap.put("Naam", gebruikersnaam);
                            gebruikersMap.put("Status", "Hallo, ik gebruik Chatapp.");
                            gebruikersMap.put("Afbeelding", "default");
                            gebruikersMap.put("Afbeelding_thumb", "default");
                            gebruikersMap.put("Apparaat_token", apparaatToken);
                            mDatabase.setValue(gebruikersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        mRegProgress.dismiss();
                                        Intent hoofdIntent = new Intent(Registreeractivity.this, MainActivity.class);
                                        hoofdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(hoofdIntent);
                                        finish();
                                    }
                                }
                            });
                        }
                    });

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
