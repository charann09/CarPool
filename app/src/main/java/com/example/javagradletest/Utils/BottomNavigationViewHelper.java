package com.example.javagradletest.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.javagradletest.Account.AccountActivity;
import com.example.javagradletest.Booked.BookedActivity;
import com.example.javagradletest.Home.HomeActivity;
import com.example.javagradletest.R;
import com.example.javagradletest.Rides.RidesActivity;

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";
    
    public static void setupBottomNavigationView(BottomNavigationView bottomNavigationView){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
    }

    public static void enableNavigation(final Context context, final BottomNavigationView view){

        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menu_location) {
                    if (view.getSelectedItemId() != R.id.menu_location) {
                        Intent intentLocation = new Intent(context, HomeActivity.class); // ACTIVITY_NUMBER = 0
                        context.startActivity(intentLocation);
                    }
                } else if (itemId == R.id.menu_rides) {
                    if (view.getSelectedItemId() != R.id.menu_rides) {
                        Intent intentRides = new Intent(context, RidesActivity.class); // ACTIVITY_NUMBER = 1
                        context.startActivity(intentRides);
                    }
                } else if (itemId == R.id.menu_booked) {
                    if (view.getSelectedItemId() != R.id.menu_booked) {
                        Intent intentBooked = new Intent(context, BookedActivity.class); // ACTIVITY_NUMBER = 2
                        context.startActivity(intentBooked);
                    }
                } else if (itemId == R.id.menu_account) {
                    if (view.getSelectedItemId() != R.id.menu_account) {
                        Intent intentAccount = new Intent(context, AccountActivity.class); // ACTIVITY_NUMBER = 3
                        context.startActivity(intentAccount);
                    }
                }

                return false;
            }
        });
    }

    public static void addBadge(final Context context, BottomNavigationView bottomNavigationView, int reminderLength){
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(1);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        View badge = LayoutInflater.from(context).inflate(R.layout.util_navigation_notification, itemView, true);
        TextView textView = (TextView) badge.findViewById(R.id.notificationsCount);
        textView.setText(String.valueOf(reminderLength));
    }

    public static void removeBadge(BottomNavigationView navigationView, int index) {
        BottomNavigationMenuView bottomNavigationMenuView = (BottomNavigationMenuView) navigationView.getChildAt(0);
        View v = bottomNavigationMenuView.getChildAt(index);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        itemView.removeViewAt(itemView.getChildCount()-1);
    }
}
