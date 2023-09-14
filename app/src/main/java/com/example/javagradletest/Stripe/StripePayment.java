package com.example.javagradletest.Stripe;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.javagradletest.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;


public class StripePayment extends AppCompatActivity {
    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    private String paymentClientSecret;

    PaymentSheet.CustomerConfiguration customerConfig;

    private String paymentURL = "http://192.168.0.100:3000/payment-sheet";
    private RequestQueue requestQueue;

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;

    private String title, body, username, profile_photo, to, from, userID, rideID;



    private static final String TAG = "CheckoutActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        myRef = mFirebaseDatabase.getReference();

        //To get Ride Data
        getActivityData();


        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        fetchAPI();
    }

    private void onPaymentSheetResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.d(TAG, "Payment Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e(TAG, "Payment Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.d(TAG, "Payment Completed");


        }
    }

    private void fetchAPI() {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                paymentURL,
                null,
                response -> {
                    try {
                        String customer = response.getString("customer");
                        String ephemeralKey = response.getString("ephemeralKey");
                        paymentIntentClientSecret = response.getString("paymentIntent");
                        String publishableKey = response.getString("publishableKey");

                        Log.i(TAG, "Data from API : " + customer + ephemeralKey + paymentClientSecret + publishableKey);

                        PaymentConfiguration.init(getApplicationContext(), publishableKey);
                        customerConfig = new PaymentSheet.CustomerConfiguration(customer, ephemeralKey);

                        presentPaymentSheet();

                    } catch (JSONException e) {
                        // Handle JSON parsing error
                    }
                },
                error -> {
                    Log.e("ERROR", "Error while fetching stripe api : " + error);
                }
        );

// Add the request to the queue
        requestQueue.add(jsonObjectRequest);
    }

    private void presentPaymentSheet() {
        final PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("CarPool Payments")
                .customer(customerConfig)
                .allowsDelayedPaymentMethods(true)
                .build();
        if (paymentIntentClientSecret != null) {

            Log.i(TAG, "PaymentSheet" + paymentSheet);

            paymentSheet.presentWithPaymentIntent(
                    paymentIntentClientSecret,
                    configuration
            );
        } else {
            Log.e(TAG, "PaymentSheet is NULL");
            Log.i(TAG, "PaymentSheet : " + paymentIntentClientSecret);
            Log.i(TAG, "configuration : " + configuration);


        }
    }

    private void acceptRide() {
        myRef.child("requestRide")
                .child(rideID)
                .child(userID)
                .child("accepted")
                .setValue(true);

        //Will close the intent when the ride is accepted
        finish();
    }

    private void getActivityData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = getIntent().getStringExtra("title");
            body = getIntent().getStringExtra("userID");
            username = getIntent().getStringExtra("username");
            profile_photo = getIntent().getStringExtra("profile_photo");
            to = getIntent().getStringExtra("to").replaceAll("\n", ", ");
            from = getIntent().getStringExtra("from").replaceAll("\n", ", ");
            rideID = getIntent().getStringExtra("rideID");
        }
    }


}