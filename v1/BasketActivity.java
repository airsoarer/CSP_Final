package com.example.matthewwilliamsii.toodo;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BasketActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference refOne;
    DatabaseReference refTwo;

    TextView titleView;
    Button newTodo;

    Bundle extras;

    String uid;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        extras = getIntent().getExtras();
        uid = extras.getString("UID");
        key = extras.getString("KEY");

        database = FirebaseDatabase.getInstance();
        refOne = database.getReference("Users/" + uid + "/Baskets/" + key);
        refTwo = database.getReference("Users/" + uid + "/Baskets/" + key + "/Items");

        titleView = (TextView)findViewById(R.id.basketName);
        newTodo = (Button)findViewById(R.id.newTodo);

        newTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog  = new Dialog(BasketActivity.this);
                dialog.setContentView(R.layout.new_todo_modal);
                dialog.setTitle("Create a Todo");

                dialog.show();
            }
        });

    }
}
