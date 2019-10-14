package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class Loginactivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputLayout mWachtwoord;
    private Button mLoginKnop;
    private Toolbar mToolbar;

    private ProgressDialog mLogProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mGebruikersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLogProgress = new ProgressDialog(this);

        mGebruikersDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers");
        mEmail = (TextInputLayout) findViewById(R.id.logEmail);
        mWachtwoord = (TextInputLayout) findViewById(R.id.logWachtwoord);
        mLoginKnop = (Button) findViewById(R.id.loginKnop);

        mLoginKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getEditText().getText().toString();
                String password = mWachtwoord.getEditText().getText().toString();

                if(TextUtils.isEmpty(email) ||  TextUtils.isEmpty(password)) {
                    Toast.makeText(Loginactivity.this, "Voer de velden in.", Toast.LENGTH_LONG).show();
                }

                else
                {
                    mLogProgress.setTitle("Inloggen");
                    mLogProgress.setMessage("Even geduld...");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();

                    loginGebruiker(email, password);
                }

            }
        });

    }

    // Gebruik E-mail en password als variabelen om daarmee in te loggen.
    private void loginGebruiker (String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful())

                {
                    mLogProgress.dismiss();
                    String huidigeGebruikersId = mAuth.getCurrentUser().getUid();
                    String apparaatToken = FirebaseInstanceId.getInstance().getToken();

                    mGebruikersDatabase.child(huidigeGebruikersId).child("AppTkn").setValue(apparaatToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent hoofdIntent = new Intent(Loginactivity.this, MainActivity.class);
                            hoofdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(hoofdIntent);
                            finish();

                        }
                    });

                }

                else
                {
                    System.out.println( "DE FOUT IS: >>>>>>>>> " + task.getException());
                    mLogProgress.hide();
                    Toast.makeText(Loginactivity.this, "Het inloggen is mislukt", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
