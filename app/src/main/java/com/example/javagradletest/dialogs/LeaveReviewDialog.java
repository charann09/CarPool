package com.example.javagradletest.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.javagradletest.Account.ProfileActivity;
import com.example.javagradletest.R;
import com.example.javagradletest.Utils.FirebaseMethods;
import com.example.javagradletest.Utils.SectionsStatePageAdapter;

public class LeaveReviewDialog extends Dialog implements
        View.OnClickListener  {

    private static final String TAG = "LeaveReviewDialog";
    public Context c;
    public Dialog d;

    // variables
    private TextView mCancelBtn;
    private EditText mComment;
    private RatingBar mRatingBar;
    private Button mLeaveReviewBtn;
    private String comment, userID;
    private Float rating;

    //Firebase
    private FirebaseMethods mFirebaseMethods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_leave_review);

        setupWidgets();

        mFirebaseMethods = new FirebaseMethods(c);

        mLeaveReviewBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    public LeaveReviewDialog(Context a, String userID) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.userID = userID;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.leaveFeedbackBtn) {
            leaveFeedback();
        } else if (viewId == R.id.dialogCancel) {
            dismiss();
        }

        dismiss();
    }

    private void leaveFeedback(){
        mFirebaseMethods.addReview(userID, rating, comment);
    }

    private void setupWidgets(){
        //Setup widgets
        mComment = (EditText) findViewById(R.id.userCommentEditText);
        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mCancelBtn = (TextView) findViewById(R.id.dialogCancel);
        mLeaveReviewBtn = (Button) findViewById(R.id.leaveFeedbackBtn);
    }

}
