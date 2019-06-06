package com.example.matthewwilliamsii.toodo;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BasketActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference refOne;
    DatabaseReference refTwo;

    TextView titleView;
    Button newTodo;

    Bundle extras;

    String uid;
    String key;

    int counter = 0;
    boolean notify;
    String dateString = "";

    String items;

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

        refOne.child("Title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        newTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog  = new Dialog(BasketActivity.this);
                dialog.setContentView(R.layout.new_todo_modal);
                dialog.setTitle("Create a Todo");

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                dialog.getWindow().setLayout((7 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);

                final DatePicker date = (DatePicker) dialog.findViewById(R.id.date);
                final CheckBox notification = (CheckBox) dialog.findViewById(R.id.notificationBox);
                final ScrollView dateScroll = (ScrollView) dialog.findViewById(R.id.dateScroll);
                notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(counter % 2 == 0){
                            dateScroll.setVisibility(View.VISIBLE);
                            notify = true;
                        }else {
                            dateScroll.setVisibility(View.GONE);
                            notify = false;
                        }
                        counter += 1;
                    }
                });

                Button create = (Button) dialog.findViewById(R.id.createTask);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText title = (EditText) dialog.findViewById(R.id.toDoTitle);
                        String titleTxt = title.getText().toString();
                        EditText description = (EditText) dialog.findViewById(R.id.description);
                        String descriptionTxt = description.getText().toString();
                        if(notify){
                            dateString = date.getMonth() + "/" + date.getDayOfMonth() + "/" + date.getYear();
                        }

                        String key = refTwo.push().getKey();
                        refTwo.child(key).child("Title").setValue(titleTxt);
                        refTwo.child(key).child("Description").setValue(descriptionTxt);
                        refTwo.child(key).child("Date").setValue(dateString);

                        refOne.child("NumberOfItems").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                items = dataSnapshot.getValue(String.class);
                                int itemsNumber = Integer.parseInt(items);
                                itemsNumber += 1;
                                items = Integer.toString(itemsNumber);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        refOne.child("NumberOfItems").setValue(items);
                    }
                });

                dialog.show();
            }
        });

    }

    public void getItems(){

    }
}
