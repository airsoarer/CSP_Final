package com.example.matthewwilliamsii.toodo;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class BasketActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference refOne;
    DatabaseReference refTwo;

    LinearLayout itemView;
    TextView titleView;
    Button newTodo;

    Bundle extras;

    String uid;
    String key;

    int counter = 0;
    boolean notify = false;

    String items;

    int month;
    int day;
    int year;

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
                notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(counter % 2 == 0){
                            date.setVisibility(View.VISIBLE);
                            notify = true;
                        }else {
                            date.setVisibility(View.GONE);
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
                        String key = refTwo.push().getKey();
                        if(notify){
                            month = date.getMonth();
                            day = date.getDayOfMonth();
                            year = date.getYear();

                            refTwo.child(key).child("Month").setValue(month);
                            refTwo.child(key).child("Day").setValue(day);
                            refTwo.child(key).child("Year").setValue(year);
                        }

                        refTwo.child(key).child("Title").setValue(titleTxt);
                        refTwo.child(key).child("Description").setValue(descriptionTxt);
                        refTwo.child(key).child("Notify").setValue(notify);
                        refTwo.child(key).child("Complete").setValue(false);

                        refOne.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                items = dataSnapshot.child("Amount").getValue(String.class);
                                int itemsNumber = Integer.parseInt(items);
                                itemsNumber += 1;
                                items = Integer.toString(itemsNumber);
                                Log.d("======TAG======", items);
                                refOne.child("Amount").setValue(items);
                                dialog.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                dialog.show();
            }
        });

        getItems();
    }

    public void getItems(){
        itemView = (LinearLayout)findViewById(R.id.todoView);
        refTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemView.removeAllViews();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String title = (String)snapshot.child("Title").getValue();
                    String description = (String)snapshot.child("Description").getValue();
                    Boolean notify = (Boolean)snapshot.child("Notify").getValue();

//                    if(notify){
//                        int month = (int)dataSnapshot.child("Month").getValue();
//                        int day = (int)dataSnapshot.child("Day").getValue();
//                        int year = (int)dataSnapshot.child("Year").getValue();
//                    }

                    // Basket Layout
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(8, 0, 8, 50);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setBackgroundResource(R.drawable.white_rounded);
                    layout.setLayoutParams(params);

                    // CheckBox
                    CheckBox check = new CheckBox(getApplicationContext());
                    LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    checkParams.setMargins(20, 10,0, 10);
                    check.setPadding(10, 0,0,0);
                    check.setLayoutParams(checkParams);
                    check.setText(title);
                    check.setTextSize(18);
                    check.setButtonTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                    check.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    layout.addView(check);

                    // Description
                    TextView descriptionView = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    descriptionParams.setMargins(20, -10,0, 20);
                    descriptionView.setLayoutParams(descriptionParams);
                    descriptionView.setText(description);
                    descriptionView.setTextSize(12);
                    descriptionView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    layout.addView(descriptionView);

                    itemView.addView(layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
