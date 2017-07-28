package com.studioemvs.chrysalis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class ProgressUpdateActivity extends AppCompatActivity implements View.OnClickListener {

    Button submitPoints;
    int[] activityPoints = {50,100,100,200,200,500,500,500,500,1000,1000,1000,2500,100,150,250,500};
    DatabaseReference mainRef,userRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String userKey,activityCompleted,activity;
    int pointsForActivity,prevPoints;
    Boolean approval = false;
    Calendar myCalendar;
    EditText activityDate,comments;
    TextView activityTxt,pointsTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_update);
        Bundle bundle = getIntent().getExtras();
        pointsForActivity = bundle.getInt("points");
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
/*        activity.setAdapter(adapter);
        activity.setOnItemSelectedListener(this);*/
        activityTxt.setText(activityCompleted);
        pointsTxt.setText(String.valueOf(pointsForActivity));
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
            updateLabel();
        }

    };
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        activityDate.setText(sdf.format(myCalendar.getTime()));
    }
/*
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String actPoints = String.valueOf(activityPoints[i]);
        points.setText(actPoints);
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
                FirebaseUser user =  mAuth.getCurrentUser();
                if (user!=null){
                    userKey = user.getUid();
                    //updatePoints(pointsForActivity);
                    submitActivityToAdmin(pointsForActivity,activityCompleted,approval);
                    updateActInMain(pointsForActivity, activityCompleted,approval);
                    Toast.makeText(ProgressUpdateActivity.this, "user key "+userKey, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProgressUpdateActivity.this, "No user is logged in", Toast.LENGTH_SHORT).show();
                }
            case R.id.date_rslt_points:
                new DatePickerDialog(ProgressUpdateActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    private void updatePoints(final int pointsForActivity) {
        //add points to already existing ones
        userRef.child(userKey).child("chrysalisPoints").setValue(pointsForActivity+prevPoints);
        prevPoints = pointsForActivity+prevPoints;
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


    private void submitActivityToAdmin(final int points, String activity,Boolean approval) {
        DatabaseReference recRef = userRef.child(userKey+"/recentActivity");
        String key = recRef.push().getKey();
        long id = System.currentTimeMillis();
        String userid = userKey;
        Log.d("progress update ", "submitActivityToAdmin: "+points+"activity: "+activity);
        User.RecentActivity recentActivityInUser = new User.RecentActivity(activity,points,approval,id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userKey+"/"+"recentActivity/"+key, recentActivityInUser);
        userRef.updateChildren(childUpdates);
    }

    private void updateActInMain(final int points, String activity,Boolean approval) {
        DatabaseReference mainRecRef = mainRef.child("recentActivity");
        String mainRecKey = mainRecRef.push().getKey();
        long id = System.currentTimeMillis();
        User.RecentActivity recentActivityInMain = new User.RecentActivity(userKey,activity,points,approval,id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(mainRecKey, recentActivityInMain);
        mainRecRef.updateChildren(childUpdates);
    }

}
