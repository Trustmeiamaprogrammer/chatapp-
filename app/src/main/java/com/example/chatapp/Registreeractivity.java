package com.example.chatapp;

// Importeer
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.HashMap;


public class Registreeractivity extends AppCompatActivity {
    // Variabelen
    private TextInputLayout mGebruikersnaam;
    private TextInputLayout mEmail;
    private TextInputLayout mWachtwoord;
    private Button mRegMaakKnop;
    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();
    private DatabaseReference gebDatabase;
    private FirebaseUser huigGeb;
    private Object NullPointerException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Layout
        setContentView(R.layout.activity_registreeractivity);
        mToolbar = findViewById(R.id.registreer_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Maak account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegProgress = new ProgressDialog(this);
        // Database gegevens
        mAuth = FirebaseAuth.getInstance();
        // Layout voor tekstinput en knop
        mGebruikersnaam = (TextInputLayout) findViewById(R.id.regGebruikersnaam);
        mEmail = (TextInputLayout) findViewById(R.id.regEmail);
        mWachtwoord = (TextInputLayout) findViewById(R.id.regWachtwoord);
        mRegMaakKnop = (Button) findViewById(R.id.regMaakKnop);

        gebDatabase = FirebaseDatabase.getInstance().getReference().child("gebruikers");

        mRegMaakKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String gebruikersnaam = mGebruikersnaam.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mWachtwoord.getEditText().getText().toString();
                // Als de velden leeg zijn
                if (TextUtils.isEmpty(gebruikersnaam) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(Registreeractivity.this, "Voer de velden in.", Toast.LENGTH_LONG).show();
                } else {
                    mRegProgress.setTitle("Account aanmaken");
                    mRegProgress.setMessage("Even geduld...");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                try {
                                    huigGeb = mAuth.getCurrentUser();
                                    String gebId = huigGeb.getUid();
                                    String appTkn = FirebaseInstanceId.getInstance().getToken();
                                    HashMap<String, String> gebMap = new HashMap<>();
                                    gebMap.put("naam", gebruikersnaam);
                                    gebMap.put("status", "Hallo! Ik gebruik ChatApp");
                                    gebMap.put("afbeelding", "default");
                                    gebMap.put("thumbAfb", "default");
                                    gebMap.put("appTkn", appTkn);

                                    gebDatabase.child(gebId).setValue(gebMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            mRegProgress.dismiss();
                                            String huidigGebId = mAuth.getCurrentUser().getUid();
                                            String appTkn = FirebaseInstanceId.getInstance().getToken();

                                            gebDatabase.child(huidigGebId).child("appTkn").setValue(appTkn).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Intent hoofdIntent = new Intent(Registreeractivity.this, MainActivity.class);
                                                    hoofdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(hoofdIntent);
                                                    finish();
                                                }
                                            });
                                        }
                                    });
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mRegProgress.hide();
                                Toast.makeText(Registreeractivity.this, "Niet gelukt registreren", Toast.LENGTH_LONG).show();
                            }
                        }

                    });
                }
            }
        });
    }
}

