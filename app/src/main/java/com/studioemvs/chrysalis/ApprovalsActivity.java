package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studioemvs.chrysalis.models.User;

import java.util.HashMap;

public class ApprovalsActivity extends AppCompatActivity implements View.OnClickListener{
    TextView empId,activity,points,commentsFromUser,activityDate;
    Button approve,reject;
    EditText comments;
    String activityToApprove,uidToApprove;
    DatabaseReference mainRef,userRef,recentActivityRef;
    ValueEventListener approveActivityListener;
    int pointsToApprove,prevPoints,unApprovedPoints,employeeId;
    Long activityId;
    String activityKey,activityKeyInMain,userComments,userActivityDate,adminKey,adminCommentsText,approvalState;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String adminEmpId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);
        getSupportActionBar().setTitle("Approvals");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();

        empId = (TextView)findViewById(R.id.approve_rslt_empId);
        activity = (TextView)findViewById(R.id.approve_rslt_activity);
        points = (TextView)findViewById(R.id.approve_rslt_points);
        approve = (Button)findViewById(R.id.approveActivity);
        reject = (Button)findViewById(R.id.rejectActivity);
        commentsFromUser = (TextView)findViewById(R.id.approve_rslt_userComments);
        activityDate = (TextView)findViewById(R.id.approve_rslt_activityDate);
        comments = (EditText)findViewById(R.id.approve_comments);
        approve.setOnClickListener(this);
        reject.setOnClickListener(this);

        mainRef = FirebaseDatabase.getInstance().getReference();
        recentActivityRef = mainRef.child("recentActivity");
        userRef = mainRef.child("users");

        Bundle bundle = getIntent().getExtras();
        pointsToApprove = bundle.getInt("actPoints");
        activityToApprove = bundle.getString("activity");
        uidToApprove = bundle.getString("userid");
        activityId = bundle.getLong("id");
        activityKey = bundle.getString("activityKey");
        activityKeyInMain = bundle.getString("activityKeyInMain");
        userComments = bundle.getString("userComments");
        userActivityDate = bundle.getString("activityDate");
        employeeId = bundle.getInt("empid");
        approvalState = bundle.getString("approvalState");

        points.setText(String.valueOf(pointsToApprove));
        activity.setText(activityToApprove);
        empId.setText(String.valueOf(employeeId));
        commentsFromUser.setText(userComments);
        activityDate.setText(userActivityDate);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user!=null){
            adminKey = user.getUid();
            userRef.child(adminKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User adminUserInfo = dataSnapshot.getValue(User.class);
                    adminEmpId = String.valueOf(adminUserInfo.getEmpid());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        userRef.child(uidToApprove).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userData = dataSnapshot.getValue(User.class);
                prevPoints = userData.getChrysalisPoints();
                unApprovedPoints = userData.getChrysalisPointsToBeApproved();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.approveActivity:
                adminCommentsText = comments.getText().toString();
                Log.d("appoveActivity", "onClick: "+activityKeyInMain);
                userRef.child(uidToApprove).child("chrysalisPoints").setValue(pointsToApprove+prevPoints);
                userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("approval").setValue("yes");
                userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("approvedBy").setValue(adminEmpId);
                userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("adminComments").setValue(adminCommentsText);
                userRef.child(uidToApprove).child("chrysalisPointsToBeApproved").setValue(unApprovedPoints - pointsToApprove);
                Log.d("Approvals", "onClick: "+activityKey);
                HashMap<String,Object> approveActivity = new HashMap<>();
                approveActivity.put("approval",true);
                approveActivity.put("approvedBy",adminEmpId);
                approveActivity.put("adminComments",adminCommentsText);
                recentActivityRef.child(activityKey).updateChildren(approveActivity);
                finish();
                break;
            case R.id.rejectActivity:
                adminCommentsText = comments.getText().toString();
                if (adminCommentsText.length() != 0){
                    userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("adminComments").setValue(adminCommentsText);
                    userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("approval").setValue("rejected");
                    userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("approvedBy").setValue(adminEmpId);
                    userRef.child(uidToApprove).child("chrysalisPointsToBeApproved").setValue(unApprovedPoints - pointsToApprove);
                    HashMap<String,Object> rejectActivity = new HashMap<>();
                    rejectActivity.put("adminComments",adminCommentsText);
                    rejectActivity.put("approval","rejected");
                    rejectActivity.put("approvedBy",adminEmpId);
                    recentActivityRef.child(activityKey).updateChildren(rejectActivity);
                    finish();
                }else{
                    Toast.makeText(this, "Please enter comments to reject the user activity.", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }
}
