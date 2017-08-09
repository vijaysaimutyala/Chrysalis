package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class ApprovalsActivity extends AppCompatActivity implements View.OnClickListener{
    TextView username,activity,points,commentsFromUser,activityDate;
    Button approve,reject;
    EditText comments;
    String activityToApprove,uidToApprove;
    DatabaseReference mainRef,userRef,recentActivityRef;
    ValueEventListener approveActivityListener;
    int pointsToApprove,prevPoints,unApprovedPoints;
    Long activityId;
    String activityKey,activityKeyInMain,userComments,userActivityDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvals);
        getSupportActionBar().setTitle("Approvals");

        username = (TextView)findViewById(R.id.approve_rslt_username);
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
        pointsToApprove = bundle.getInt("points");
        activityToApprove = bundle.getString("activity");
        uidToApprove = bundle.getString("userid");
        activityId = bundle.getLong("id");
        activityKey = bundle.getString("activityKey");
        activityKeyInMain = bundle.getString("activityKeyInMain");
        userComments = bundle.getString("userComments");
        userActivityDate = bundle.getString("activityDate");

        points.setText(String.valueOf(pointsToApprove));
        activity.setText(activityToApprove);
        username.setText(uidToApprove);
        commentsFromUser.setText(userComments);
        activityDate.setText(userActivityDate);
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
                Log.d("appoveActivity", "onClick: "+activityKeyInMain);
                userRef.child(uidToApprove).child("chrysalisPoints").setValue(pointsToApprove+prevPoints);
                userRef.child(uidToApprove).child("recentActivity").child(activityKeyInMain).child("approval").setValue(true);
                userRef.child(uidToApprove).child("chrysalisPointsToBeApproved").setValue(unApprovedPoints - pointsToApprove);
                Log.d("Approvals", "onClick: "+activityKey);
                HashMap<String,Object> approveActivity = new HashMap<>();
                approveActivity.put("approval",true);
                recentActivityRef.child(activityKey).updateChildren(approveActivity);
                finish();
                break;
            case R.id.rejectActivity:
                break;
        }

    }
}
