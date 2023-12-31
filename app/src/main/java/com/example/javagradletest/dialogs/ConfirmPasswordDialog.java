package com.example.javagradletest.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.javagradletest.R;

public class ConfirmPasswordDialog extends DialogFragment {

    private static final String TAG = "ConfirmPasswordDialog";

    public interface onConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }

    onConfirmPasswordListener mOnConfirmPassowrdListener;

    //vars
    private TextView mPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.dialog_confirm_password, container, false);
        Log.d(TAG, "onCreateView: started");

        mPassword = (EditText) view.findViewById(R.id.confirmPassword);

        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the dialog");
                getDialog().dismiss();
            }
        });

        Button confirmDialog = (Button) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: confirm the change");
                String password = mPassword.getText().toString();
                if(!password.equals("")) {
                    mOnConfirmPassowrdListener.onConfirmPassword(password);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                }
            }
        });
       
       return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mOnConfirmPassowrdListener = (onConfirmPasswordListener) getTargetFragment();
        } catch  (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
