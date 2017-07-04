package com.studioemvs.chrysalis;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProgressUpdateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    Spinner activity;
    TextView points;
    Button submitPoints;
    int[] activityPoints = {50,100,100,200,200,500,500,500,500,1000,1000,1000,2500,100,150,250,500};
    DatabaseReference userRef;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String userKey,activityCompleted;
    int pointsForActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_update);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth = FirebaseAuth.getInstance();
        activity = (Spinner)findViewById(R.id.activitySpinner);
        points = (TextView)findViewById(R.id.pointsForActivity);
        submitPoints = (Button)findViewById(R.id.submitActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.setAdapter(adapter);
        activity.setOnItemSelectedListener(this);
        submitPoints.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String actPoints = String.valueOf(activityPoints[i]);
        pointsForActivity = activityPoints[i];
        activityCompleted = activity.getSelectedItem().toString();
        points.setText(actPoints);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submitActivity:
                FirebaseUser user =  mAuth.getCurrentUser();
                if (user!=null){
                    userKey = user.getUid();
                    submitActivityToAdmin(pointsForActivity,activityCompleted);
                    try {
                        Thread.sleep(2000);
                        updatePoints(pointsForActivity);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(ProgressUpdateActivity.this, "user key "+userKey, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ProgressUpdateActivity.this, "No user is logged in", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void updatePoints(final int pointsForActivity) {
        //add points to already existing ones
        userRef.child(userKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                User user = mutableData.getValue(User.class);
                if (user ==null){
                    return Transaction.success(mutableData);
                }
                user.chrysalisPoints = user.chrysalisPoints+pointsForActivity;
                mutableData.setValue(user);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("point update", "postTransaction:onComplete:" + databaseError);
            }
        });
    }


    private void submitActivityToAdmin(final int points, String activity) {
        DatabaseReference recRef = userRef.child(userKey+"/recentActivity");
        String key = recRef.push().getKey();
        User.RecentActivity recentActivity = new User.RecentActivity("recentss",activity,points);
        Map<String,Object> recActivity = recentActivity.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userKey+"/"+"recentActivity/"+key, recActivity);
        userRef.updateChildren(childUpdates);
    }
}
