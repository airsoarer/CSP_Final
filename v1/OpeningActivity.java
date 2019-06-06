package com.example.matthewwilliamsii.toodo;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class OpeningActivity extends AppCompatActivity {

    FirebaseAuth auth;

    Button email;
    Button noAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        auth = FirebaseAuth.getInstance();

        email = (Button)findViewById(R.id.email);
        noAccount = (Button)findViewById(R.id.noAccount);

        CountDownTimer timer = new CountDownTimer(2500, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if(auth.getCurrentUser() != null){
                    String uid = auth.getCurrentUser().getUid();
                    startActivity(new Intent(OpeningActivity.this, MainActivity.class).putExtra("UID", uid));
                }else{
                    email.setVisibility(View.VISIBLE);
                    noAccount.setVisibility(View.VISIBLE);
                }
            }
        }.start();

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpeningActivity.this, LoginActivity.class));
            }
        });

        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpeningActivity.this, SignUpActivity.class));
            }
        });

    }
}
