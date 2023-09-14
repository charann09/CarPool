package com.example.javagradletest.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.javagradletest.Account.HelpFragment;
import com.example.javagradletest.R;
import com.example.javagradletest.Rides.RidesActivity;
import com.example.javagradletest.Utils.SectionsStatePageAdapter;

public class OfferRideCreatedDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "OfferRideCreatedDialog";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;





    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPassowrdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_offer_ride_created);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.dialogConfirm);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Shows the ride has been created successfully
                dismiss();

                Intent intent = new Intent(c, RidesActivity.class);
                c.startActivity(intent);
//                ViewRideCreatedDialog dialog = new ViewRideCreatedDialog(c);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
            }
        });
    }

    public OfferRideCreatedDialog(Context a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.dialogConfirm) {
            dismiss();
            Intent intent1 = new Intent(c, HelpFragment.class);
            c.startActivity(intent1);
        } else if (viewId == R.id.dialogCancel) {
            dismiss();
        }

        dismiss();
    }

}
