package com.example.chatapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private TextInputLayout mStatus;
    private Button mOpslaanKnop;

    private DatabaseReference mStatusDatabase;
    private FirebaseUser mHuidigeGebruiker;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        mHuidigeGebruiker = FirebaseAuth.getInstance().getCurrentUser();
        String huidige_uid = mHuidigeGebruiker.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Gebruikers").child(huidige_uid);
        mToolbar = (Toolbar) findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("StatusWaarde");
        mStatus = (TextInputLayout) findViewById(R.id.status_invoer);
        mOpslaanKnop = (Button) findViewById(R.id.status_opslaan_knop);

        mStatus.getEditText().setText(status_value);

        mOpslaanKnop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Opslaan verandering");
                mProgress.setMessage("Wacht terwijl wordt opgeslagen");
                mProgress.show();

                String status = mStatus.getEditText().getText().toString();

                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        } else{
                            Toast.makeText(getApplicationContext(), "Er was een error tijdens het opslaan", Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }
        });


    }
}
