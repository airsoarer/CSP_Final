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

        // Get some values
        auth = FirebaseAuth.getInstance();

        email = (Button)findViewById(R.id.email);
        noAccount = (Button)findViewById(R.id.noAccount);

        // Set a timer to show login and sign up buttons after 2 and a half seconds
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

        // go to the main activity if email is clicked
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpeningActivity.this, LoginActivity.class));
            }
        });

        // Go to sign up if new create an account is clicked.
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OpeningActivity.this, SignUpActivity.class));
            }
        });

    }
}
