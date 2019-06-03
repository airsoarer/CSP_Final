package com.example.matthewwilliamsii.toodo;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    // Set most needed values for script to work
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference ref;

    EditText fname;
    String fnameTxt;

    EditText lname;
    String lnameTxt;

    EditText email;
    String emailTxt;

    EditText pass;
    String passTxt;

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize values
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        fname = (EditText)findViewById(R.id.fname);
        lname = (EditText)findViewById(R.id.lname);
        email = (EditText)findViewById(R.id.email);
        pass = (EditText)findViewById(R.id.pass);
        login = (Button)findViewById(R.id.login);


        // Get the values of the inputed text fiuelds
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnameTxt = fname.getText().toString();
                lnameTxt = lname.getText().toString();
                emailTxt = email.getText().toString();
                passTxt = pass.getText().toString();
                signUp();
            }
        });

    }

    public void signUp(){
        // Sign up method and send user data to their own uid in the databasee
        auth.createUserWithEmailAndPassword(emailTxt, passTxt).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    String uid = user.getUid();

                    ref = database.getReference("Users/" + uid + "/Info");
                    ref.child("FirstName").setValue(fnameTxt);
                    ref.child("LastName ").setValue(lnameTxt);
                    ref.child("Email").setValue(emailTxt);
                    ref.child("Password").setValue(passTxt);

                    startActivity(new Intent(SignUpActivity.this, MainActivity.class).putExtra("UID", uid));
                }else{
                    Toast.makeText(SignUpActivity.this, "Unexpected Sign Up Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
