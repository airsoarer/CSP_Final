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

    // Set most of the values needed for the program to run
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
    boolean checked;

    String items;

    int month;
    int day;
    int year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        // Initialize values
        checked = false;
        extras = getIntent().getExtras();
        uid = extras.getString("UID");
        key = extras.getString("KEY");

        database = FirebaseDatabase.getInstance();
        refOne = database.getReference("Users/" + uid + "/Baskets/" + key);
        refTwo = database.getReference("Users/" + uid + "/Baskets/" + key + "/Items");

        titleView = (TextView)findViewById(R.id.basketName);
        newTodo = (Button)findViewById(R.id.newTodo);

        // Get the title of the basket
        refOne.child("Title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleView.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Create a new to-do on click
        newTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the dialog box
                final Dialog dialog  = new Dialog(BasketActivity.this);
                dialog.setContentView(R.layout.new_todo_modal);
                dialog.setTitle("Create a Todo");

                // Make it so that the dialog box takes up about 90 to 95% of the screen
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                int height = metrics.heightPixels;

                dialog.getWindow().setLayout((7 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);

                // Get values from our date picker and checkbox and see if the user wants a notification
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

                // When create is pressed get the values and send them to Firebase.
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

                        refOne.child("Amount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                items = dataSnapshot.getValue(String.class);
                                int itemsNumber = Integer.parseInt(items);
                                itemsNumber += 1;
                                items = Integer.toString(itemsNumber);
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
        // Get the items from firebase and create elements for them and send them to a scrollview
        refTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemView.removeAllViews();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    final String key = snapshot.getKey();
                    String title = (String)snapshot.child("Title").getValue();
                    String description = (String)snapshot.child("Description").getValue();
                    Boolean notify = snapshot.child("Notify").getValue(Boolean.class);
//                    Log.i("Notify snapshot", notify.toString());
                    Boolean complete = snapshot.child("Complete").getValue(Boolean.class);
                    long month = 0;
                    long day = 0;
                    long year = 0;

                    if(notify == null){
                        notify = false;
                    }

                    if(notify){
                        month = (long)snapshot.child("Month").getValue();
                        day = (long)snapshot.child("Day").getValue();
                        year = (long)snapshot.child("Year").getValue();
                    }

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
                    final CheckBox check = new CheckBox(getApplicationContext());
                    LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    refTwo.child(key).child("Complete").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            checked = dataSnapshot.getValue(Boolean.class);
                            check.setChecked(checked);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    checkParams.setMargins(20, 10,0, 10);
                    check.setPadding(10, 0,0,0);
                    check.setLayoutParams(checkParams);
                    check.setText(title);
                    check.setTextSize(17);
                    check.setButtonTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.black));
                    check.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    check.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checked = !checked;

                            Log.d("======TAG======", Boolean.toString(checked));
                            refTwo.child(key).child("Complete").setValue(checked);
                        }
                    });
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

                    // notification
                    TextView notifyView = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams notifyParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    notifyView.setLayoutParams(notifyParams);
                    notifyParams.setMargins(20, -10,0, 20);
                    notifyView.setText("Complete by: " + month + "/" + day + "/" + year);
                    notifyView.setTextSize(12);
                    notifyView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    if(month != 0 && day != 0 && year != 0){
                        layout.addView(notifyView);
                    }

                    // Delete
                    Button delete = new Button(getApplicationContext());
                    LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteParams.setMargins(50, 0,-10, 0);
                    delete.setLayoutParams(deleteParams);
                    delete.setText(R.string.delete);
                    delete.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    delete.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    delete.setTransformationMethod(null);
                    delete.setStateListAnimator(null);
                    layout.addView(delete);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final Dialog dialog = new Dialog(BasketActivity.this);
                            dialog.setContentView(R.layout.delete_modal);
                            dialog.setTitle("Are you sure you want to delete this?");

                            DisplayMetrics metrics = getResources().getDisplayMetrics();
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;

                            dialog.getWindow().setLayout((7 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);

                            Button yes = (Button)dialog.findViewById(R.id.yes);
                            Button no = (Button)dialog.findViewById(R.id.no);

                            yes.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    refTwo.child(key).removeValue();
                                    refOne.child("Amount").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String amount = (String)dataSnapshot.getValue();
                                            int amountInt = Integer.parseInt(amount);
                                            amountInt = amountInt - 1;
                                            amount = Integer.toString(amountInt);
                                            refOne.child("Amount").setValue(amount);
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            });

                            no.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        }
                    });
                    itemView.addView(layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
