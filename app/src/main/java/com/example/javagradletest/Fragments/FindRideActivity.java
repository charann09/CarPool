package com.example.javagradletest.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.javagradletest.Home.HomeActivity;
import com.example.javagradletest.R;
import com.example.javagradletest.Utils.FirebaseMethods;
import com.example.javagradletest.models.User;

public class FindRideActivity extends AppCompatActivity {

    private static final String TAG = "FindRideActivity";


    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;

    //Widgets
    private EditText mDestinationEditText, mFromEditText;
    private Button mChangeEmailButton;

    //vars
    private User mUserSettings;
    private String destinationId, locationId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_ride);
        Log.d(TAG, "onCreate: starting.");

        setupWidgets();
        getActivityData();
        populateActivityWidgets();


        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                Intent intent = new Intent(FindRideActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateActivityWidgets() {
        mDestinationEditText.setText(destinationId);
        mFromEditText.setText(locationId);
    }

    private void setupWidgets() {
        mDestinationEditText = (EditText) findViewById(R.id.destinationEditText);
        mFromEditText = (EditText) findViewById(R.id.fromEditText);
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationId = getIntent().getStringExtra("DESTINATION");
            locationId = getIntent().getStringExtra("LOCATION");
        }
    }
}
