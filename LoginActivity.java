package com.example.matthewwilliamsii.toodo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    // Set most values needed for the program to run
    FirebaseAuth auth;

    EditText email;
    String emailTxt;

    EditText pass;
    String passTxt;

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize values
        auth = FirebaseAuth.getInstance();

        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);

        login = (Button)findViewById(R.id.login);

//      Get email and pass values and go to login function
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailTxt = email.getText().toString();
                passTxt = pass.getText().toString();

                login();
            }
        });
    }

    public void login(){
        // Sign in method.
        auth.signInWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();

                    startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("UID", uid));
                }else{
                    String errorCode = ((FirebaseAuthException)task.getException()).getErrorCode();

                    // Check to see what type of login error there is if any
                    if(errorCode == "ERROR_INVALID_EMAIL"){
                        Toast.makeText(LoginActivity.this, "Incorrect Email", Toast.LENGTH_SHORT).show();
                        email.setBackgroundResource(R.color.pink);
                    }else if(errorCode == "ERROR_WRONG_PASSWORD") {
                        Toast.makeText(LoginActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                        pass.setBackgroundResource(R.color.pink);
                    }else if(errorCode == "ERROR_USER_NOT_FOUND"){
                        Toast.makeText(LoginActivity.this, "This user wasn't found in our databases", Toast.LENGTH_SHORT).show();
                    }else{

                    }
                }
            }
        });
    }
}
