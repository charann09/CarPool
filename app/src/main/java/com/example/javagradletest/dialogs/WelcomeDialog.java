package com.example.javagradletest.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.javagradletest.Account.AccountActivity;
import com.example.javagradletest.Account.HelpFragment;
import com.example.javagradletest.R;
import com.example.javagradletest.Utils.SectionsStatePageAdapter;

import android.content.Intent;

public class WelcomeDialog extends Dialog implements
        android.view.View.OnClickListener  {

    private static final String TAG = "WelcomeDialog";
    public Context c;
    public Dialog d;
    private TextView cancelDialog;
    private  Button confirmDialog;
    private SectionsStatePageAdapter pageAdapter;





    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPassowrdListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_welcome);
        cancelDialog = (TextView) findViewById(R.id.dialogCancel);
        confirmDialog = (Button) findViewById(R.id.dialogConfirm);
        cancelDialog.setOnClickListener(this);
        confirmDialog.setOnClickListener(this);

    }

    public WelcomeDialog(Context a) {
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
