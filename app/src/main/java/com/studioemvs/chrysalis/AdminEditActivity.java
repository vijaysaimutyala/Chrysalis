package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AdminEditActivity extends AppCompatActivity implements View.OnClickListener{
    EditText actName, activityPoints;
    Button updateActivity,deleteActivity;
    String actKey;
    FirebaseAuth mAuth;
    DatabaseReference mainRef,activityRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);
        getSupportActionBar().setTitle("Edit Activity");
        mAuth = FirebaseAuth.getInstance();
        mainRef = FirebaseDatabase.getInstance().getReference();
        activityRef = mainRef.child("activites");
        Bundle actBundle = getIntent().getExtras();
        actKey = actBundle.getString("activityKey");
        actName = (EditText)findViewById(R.id.edit_activityName);
        activityPoints = (EditText)findViewById(R.id.edit_activityPoints);
        updateActivity = (Button)findViewById(R.id.submitUpdateAct);
        deleteActivity = (Button)findViewById(R.id.deleteUpdateAct);
        deleteActivity.setOnClickListener(this);
        updateActivity.setOnClickListener(this);
        actName.setText(actBundle.getString("activity").toString());
        activityPoints.setText(String.valueOf(actBundle.getInt("actPoints")));
    }

    @Override
    public void onClick(View view) {
        int points = Integer.parseInt(activityPoints.getText().toString());
        switch (view.getId()){
            case R.id.submitUpdateAct:
                activityRef.child(actKey).child("activity").setValue(actName.getText().toString());
                activityRef.child(actKey).child("actPoints").setValue(points);
                Toast.makeText(this, "Changes updated.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.deleteUpdateAct:
                activityRef.child(actKey).removeValue();
                finish();
                break;
        }

    }
}
