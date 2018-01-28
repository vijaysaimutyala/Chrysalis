package com.studioemvs.chrysalis;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProgressUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    Button submitPoints;
    int[] activityPoints = {50,100,100,200,200,500,500,500,500,1000,1000,1000,2500,100,150,250,500};
    DatabaseReference mainRef,userRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String userKey,activityCompleted,dateCompleted,userComments,keyUnderUserRecentNode;
    String approvedBy = "",adminComments = "";
    int pointsForActivity,prevPointsToApprove,globPrevPoints,empid,globEmpId;
    String approval = "no";
    Calendar myCalendar;
    EditText activityDate,comments;
    TextView activityTxt,pointsTxt;
    RelativeLayout relLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_update);
        getSupportActionBar().setTitle("Submit Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle bundle = getIntent().getExtras();
        pointsForActivity = bundle.getInt("actPoints");
        activityCompleted = bundle.getString("activity");
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();
        myCalendar = Calendar.getInstance();
        submitPoints = (Button)findViewById(R.id.submitActivity);
        activityTxt = (TextView)findViewById(R.id.update_rslt_activity);
        pointsTxt = (TextView)findViewById(R.id.update_rslt_points);
        activityDate = (EditText)findViewById(R.id.date_rslt_points);
        comments = (EditText)findViewById(R.id.update_comments);
        relLayout = (RelativeLayout)findViewById(R.id.updateRelActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
/*        activity.setAdapter(adapter);
        activity.setOnItemSelectedListener(this);*/
        activityTxt.setText(activityCompleted);
        pointsTxt.setText(String.valueOf(pointsForActivity));
        getPrevPoints();
        submitPoints.setOnClickListener(this);
        activityDate.setOnClickListener(this);

    }
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }

    };
    private void updateDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        activityDate.setText(sdf.format(myCalendar.getTime()));
    }
/*
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String actPoints = String.valueOf(activityPoints[i]);
        actPoints.setText(actPoints);
        pointsForActivity = Integer.parseInt(actPoints);
        activityCompleted = activity.getSelectedItem().toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitActivity:
                dateCompleted = activityDate.getText().toString();
                userComments = comments.getText().toString();
                //updatePoints(pointsForActivity);
                if (dateCompleted.length() !=0){
                    submitActivityToAdmin(pointsForActivity,activityCompleted,approval,dateCompleted,userComments);
                    updateActInMain(pointsForActivity, activityCompleted,approval,dateCompleted,userComments,keyUnderUserRecentNode);
                    updateToBeApprovedPoints(pointsForActivity,prevPointsToApprove);
                   //Toast.makeText(ProgressUpdateActivity.this, "user key "+userKey, Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Snackbar dateBar = Snackbar.make(relLayout,"Please enter the activity date",Snackbar.LENGTH_SHORT);
                    dateBar.setActionTextColor(Color.RED);
                    dateBar.show();
                }

                break;
            case R.id.date_rslt_points:
                new DatePickerDialog(ProgressUpdateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void getPrevPoints() {
        FirebaseUser user =  mAuth.getCurrentUser();
        if (user!=null){
            userKey = user.getUid();
            userRef.child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                   // Toast.makeText(ProgressUpdateActivity.this, "in onDatachange", Toast.LENGTH_SHORT).show();
                    User userdata = dataSnapshot.getValue(User.class);
                    empid = userdata.getEmpid();
                    prevPointsToApprove = userdata.getChrysalisPointsToBeApproved();
                    savePrevPointsForUpdate(prevPointsToApprove,empid);
                    Log.d("SubmitActivity", "onDataChange: "+prevPointsToApprove);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        Log.d("prevpoints", "savePrevPointsForUpdate: "+prevPointsToApprove);
        globPrevPoints = prevPointsToApprove;
    }else{
            Toast.makeText(this, "User is not logged.Please logout and login to post activity", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePrevPointsForUpdate(int previousPoints,int empid) {
        globPrevPoints = previousPoints;
        globEmpId =empid;
    }


    private void updateToBeApprovedPoints(final int pointsForActivity, int prevPointsToApprove) {
        //add actPoints to already existing ones
        //userRef.child(userKey).child("chrysalisPoints").setValue(pointsForActivity+prevPoints);
        int totalPointsToApprove = pointsForActivity+prevPointsToApprove;
        Log.d("Prevpoints", "updateToBeApprovedPoints: prevpoints"+prevPointsToApprove);
        Log.d("pointsForActivity", "updateToBeApprovedPoints: "+pointsForActivity);
        Log.d("UpdateToBeApproved", "updateToBeApprovedPoints: "+totalPointsToApprove);
        userRef.child(userKey).child("chrysalisPointsToBeApproved").setValue(totalPointsToApprove);

/*        userRef.child(userKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                User.RecentActivity recentActivity = new User.RecentActivity("recentss",activityCompleted,pointsForActivity);
                if (user ==null){
                    return Transaction.success(mutableData);
                }
                user.chrysalisPoints = user.chrysalisPoints+pointsForActivity;
                user.recentActivity = recentActivity;

                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("point update", "postTransaction:onComplete:" + databaseError);

            }
        });*/
    }

//updating in recent activity node under User main node
    private void submitActivityToAdmin(final int points, String activity,String approval,String activityDate,
                                       String userComments) {
        DatabaseReference recRef = userRef.child(userKey+"/recentActivity");
        keyUnderUserRecentNode = recRef.push().getKey();
        long id = System.currentTimeMillis();
        String userid = userKey;
        Log.d("progress update ", "submitActivityToAdmin: "+points+"activity: "+activity);
        User.RecentActivity recentActivityInUser = new User.RecentActivity(activity,points,approval,id,activityDate,userComments,globEmpId,approvedBy,adminComments);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userKey+"/"+"recentActivity/"+keyUnderUserRecentNode, recentActivityInUser);
        userRef.updateChildren(childUpdates);
    }

    //updating in the recent activity main node
    private void updateActInMain(final int points, String activity,String approval,String activityDate, String userComments,String activityKey) {
        DatabaseReference mainRecRef = mainRef.child("recentActivity");
        String mainRecKey = mainRecRef.push().getKey();
        long id = System.currentTimeMillis();
        User.RecentActivity recentActivityInMain = new User.RecentActivity(userKey,activity,points,approval,id,activityDate,userComments,keyUnderUserRecentNode,globEmpId,approvedBy,adminComments);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(mainRecKey, recentActivityInMain);
        mainRecRef.updateChildren(childUpdates);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
