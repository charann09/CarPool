package com.example.javagradletest.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import com.example.javagradletest.Common.Common;
import com.example.javagradletest.R;
import com.example.javagradletest.Remote.IFCMService;
import com.example.javagradletest.models.FCMResponse;
import com.example.javagradletest.models.Notification;
import com.example.javagradletest.models.Sender;
import com.example.javagradletest.models.Token;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingReceivedDialog extends Dialog implements
        View.OnClickListener {

    private static final String TAG = "BookingReceivedDialog";
    public Context c;
    public Dialog d;

    // variables
    private FloatingActionButton mConfirmBtn, declineRideBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_booking_received);

        mConfirmBtn = (FloatingActionButton) findViewById(R.id.confirmRideBtn);
        declineRideBtn = (FloatingActionButton) findViewById(R.id.declineRideBtn);

        declineRideBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);

    }

    public BookingReceivedDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirmRideBtn) {

            dismiss();
            Toast.makeText(c, "Ride confirmed", Toast.LENGTH_SHORT).show();
        }
        if (v.getId() == R.id.declineRideBtn) {

            Toast.makeText(c, "You cancelled the ride", Toast.LENGTH_SHORT).show();
            dismiss();

        }
        dismiss();
    }


}
