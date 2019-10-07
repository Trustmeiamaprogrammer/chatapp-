package com.example.chatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Loginactivity extends AppCompatActivity {

    private TextInputLayout mEmail;
    private TextInputLayout mWachtwoord;
    private Button mLoginKnop;
    private Toolbar mToolbar;

    private ProgressDialog mLogProgress;
    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar) ;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Log in");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLogProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();

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
                    mLogProgress.setTitle("inloggen");
                    mLogProgress.setMessage("Even geduld...");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();

                    log_gebruiker(email, password);
                }

            }
        });

    }

    private void log_gebruiker (String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful())

                {
                    mLogProgress.dismiss();
                    Intent hoofdIntent = new Intent(Loginactivity.this, MainActivity.class);
                    hoofdIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(hoofdIntent);
                    finish();
                }

                else
                {
                    System.out.println( "DE FOUT IS: >>>>>>>>> " + task.getException());
                    mLogProgress.hide();
                    Toast.makeText(Loginactivity.this, "Fouuut", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
