package com.example.matthewwilliamsii.toodo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    // Set most of the values needed to run program
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference ref;
    DatabaseReference refTwo;

    Button logout;
    Button createBasket;

    String uid;

    LinearLayout basketView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize values
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        uid = intent.getStringExtra("UID");

        basketView = (LinearLayout)findViewById(R.id.baskets);

        // Create a new basket when the create button is clicked
        createBasket = (Button)findViewById(R.id.create);
        createBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog  = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.create_basket_modal);
                dialog.setTitle("Create a Basket");

                Button create = (Button) dialog.findViewById(R.id.create);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText titleView = (EditText) dialog.findViewById(R.id.basketTitle);
                        final String title = titleView.getText().toString();
                        ref = database.getReference("Users/" + uid + "/Baskets");
                        String newKey = ref.push().getKey();
                        ref.child(newKey).child("Title").setValue(title);
                        int preset = 0;
                        ref.child(newKey).child("Amount").setValue("0");
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        // Logout user when logout is pressed
        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        getBaskets();
    }

    // Logout
    public void logout(){
        auth.signOut();
        startActivity(new Intent(MainActivity.this, OpeningActivity.class));
    }

    // Display all baskets when app loads
    public void getBaskets(){
        refTwo = database.getReference("Users/" + uid + "/Baskets");
        refTwo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                basketView.removeAllViews();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    final String key = snapshot.getKey();
                    String itemsNumber = snapshot.child("Amount").getValue(String.class);
                    String title = snapshot.child("Title").getValue(String.class);

                    // Basket Layout
                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            350
                    );
                    params.setMargins(8, 0, 8, 100);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setBackgroundResource(R.drawable.white_rounded);
                    layout.setLayoutParams(params);

//                     Basket Button Button
                    Button basket = new Button(getApplicationContext());
                    LinearLayout.LayoutParams basketParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    basketParams.setMargins(30, 20, 50, 0);
                    basket.setPadding(0, 10, 0 , 0);
                    basket.setText(title);
                    basket.setBackgroundResource(R.drawable.white_rounded);
                    basket.setTextSize(20);
                    basket.setTextColor(Color.parseColor("#000000"));
                    basket.setGravity(Gravity.LEFT);
                    basket.setTransformationMethod(null);
                    basket.setLayoutParams(basketParams);
                    basket.setStateListAnimator(null);
                    basket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle extras = new Bundle();
                            extras.putString("UID", uid);
                            extras.putString("KEY", key);
                            startActivity(new Intent(MainActivity.this, BasketActivity.class).putExtras(extras));
                        }
                    });
                    layout.addView(basket);

//                    Items number textview
                    TextView items = new TextView(getApplicationContext());
                    LinearLayout.LayoutParams itemsParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    itemsParams.setMargins(30, -25, 0, 0 );
                    items.setText("Filled with " + itemsNumber + " items.");
                    items.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.gray));
                    items.setTextSize(14);
                    items.setLayoutParams(itemsParams);
                    layout.addView(items);

                    // Delete Basket Button
                    Button delete = new Button(getApplicationContext());
                    LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    deleteParams.setMargins(0,0,20,0);
                    deleteParams.gravity = Gravity.RIGHT;
                    delete.setLayoutParams(deleteParams);
                    delete.setText(R.string.delete);
                    delete.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    delete.setBackgroundResource(R.color.white);
                    delete.setStateListAnimator(null);
                    delete.setTransformationMethod(null);
//                    delete.setGravity(Gravity.RIGHT);
                    delete.setTextSize(16);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            refTwo.child(key).removeValue();
                        }
                    });
                    layout.addView(delete);

                    basketView.addView(layout);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
