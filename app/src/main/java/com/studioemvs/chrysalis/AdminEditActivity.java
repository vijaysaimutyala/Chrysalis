package com.studioemvs.chrysalis;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.HashMap;

public class AdminEditActivity extends AppCompatActivity implements View.OnClickListener{
    EditText actName, activityPoints;
    Button updateActivity,deleteActivity;
    String actKey;
    FirebaseAuth mAuth;
    DatabaseReference mainRef,activityRef;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit);
        getSupportActionBar().setTitle("Edit Activity");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mainRef = FirebaseDatabase.getInstance().getReference();
        activityRef = mainRef.child("activites");
        Bundle actBundle = getIntent().getExtras();
        actKey = actBundle.getString("activityKey");
        actName = (EditText) findViewById(R.id.edit_activityName);
        activityPoints = (EditText) findViewById(R.id.edit_activityPoints);
        updateActivity = (Button)findViewById(R.id.submitUpdateAct);
        deleteActivity = (Button)findViewById(R.id.deleteUpdateAct);
        deleteActivity.setOnClickListener(this);
        updateActivity.setOnClickListener(this);
        actName.setText(actBundle.getString("activity").toString());
        activityPoints.setText(String.valueOf(actBundle.getInt("actPoints")));
        alertDialog = new AlertDialog.Builder(this);


    }

    @Override
    public void onClick(View view) {
        int points = Integer.parseInt(activityPoints.getText().toString());
        switch (view.getId()){
            case R.id.submitUpdateAct:
                //activityRef.child(actKey).child("activity").setValue(actName.getText().toString());
                //activityRef.child(actKey).child("actPoints").setValue(points);
                Toast.makeText(this, "Sorry. Updating the activity is disabled for now. Check back after the next version is released.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.deleteUpdateAct:
                alertDialog.setMessage("Do you really want to delete the activity?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activityRef.child(actKey).removeValue();
                                Toast.makeText(getApplicationContext(), "Activity deleted succesfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                AlertDialog alert = alertDialog.create();
                alert.show();
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
