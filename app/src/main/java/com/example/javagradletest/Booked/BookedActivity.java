package com.example.javagradletest.Booked;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.example.javagradletest.Adapter.BookingAdapter;
import com.example.javagradletest.Home.SearchResultsActivity;
import com.example.javagradletest.R;
import com.example.javagradletest.Utils.BottomNavigationViewHelper;
import com.example.javagradletest.Utils.FirebaseMethods;
import com.example.javagradletest.Adapter.MyAdapter;
import com.example.javagradletest.dialogs.LeaveReviewDialog;
import com.example.javagradletest.dialogs.WelcomeDialog;
import com.example.javagradletest.models.BookingResults;
import com.example.javagradletest.models.Request;
import com.example.javagradletest.models.Ride;

public class BookedActivity extends AppCompatActivity {

    private static final String TAG = "BookedActivity";
    private static final int ACTIVITY_NUMBER = 2;

    //View variables
    private RelativeLayout mNoResultsFoundLayout;
    private BottomNavigationView bottomNavigationView;

    //Recycle View variables
    private Context mContext = BookedActivity.this;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mRecycleAdapter;
    private BookingAdapter myAdapter;
    private ArrayList<BookingResults> rides;

    //Firebase variables
    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabse;
    private DatabaseReference mRef;
    private FirebaseMethods mFirebaseMethods;

    private String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: started.");
        setContentView(R.layout.activity_booked);
        setupBottomNavigationView();

        //setupDialog();

        //Setup firebase object
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabse = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabse.getReference();
        if (mAuth.getCurrentUser() != null){
            user_id = mAuth.getCurrentUser().getUid();
            Log.i(TAG, "onCreate: "+ user_id);
        }

        checkNotifications();



        //Setup recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mRecycleAdapter);
        rides = new ArrayList<BookingResults>();

        //View setup
        mNoResultsFoundLayout = (RelativeLayout) findViewById(R.id.noResultsFoundLayout);

        mRef.child("requestRide").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {
                                BookingResults r = dataSnapshot2.getValue(BookingResults.class);
                                if (dataSnapshot2.getKey().contains(user_id)){
                                    rides.add(r);
                                    mNoResultsFoundLayout.setVisibility(View.INVISIBLE);
                                }
                        }
                    }
                }

                myAdapter = new BookingAdapter(BookedActivity.this, rides);
                mRecyclerView.setAdapter(myAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /***
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        //BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView);

        //Change current highlighted icon
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUMBER);
        menuItem.setChecked(true);
    }

    private void setupBadge(int reminderLength){
        if (reminderLength > 0){
            //Adds badge and notification number to the BottomViewNavigation
            BottomNavigationViewHelper.addBadge(mContext, bottomNavigationView, reminderLength);
        }
    }

    /**
     * Checks if there are notifications available for the current logged in user.
     */
    private void checkNotifications(){
        mRef.child("Reminder").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int reminderLength = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        reminderLength++;
                    }
                }
                //Passes the number of notifications onto the setup badge method
                setupBadge(reminderLength);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    /***
     *  Setup the firebase object
     */
    @Override
    public void onStart() {
        super.onStart();
    }


}
