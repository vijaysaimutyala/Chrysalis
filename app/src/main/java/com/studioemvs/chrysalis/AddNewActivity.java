package com.studioemvs.chrysalis;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.studioemvs.chrysalis.models.ActivitiesBean;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddNewActivity extends AppCompatActivity implements View.OnClickListener{
    String activity;
    int points;
    String chrysalisGroup,adminstructorid;
    Button submit;
    EditText activityPoints,chrysActivity,activityDate;
    Spinner chrysGroup,activityType;
    Calendar myCalendar;
    DatabaseReference mainRef,activityRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mainRef = FirebaseDatabase.getInstance().getReference();
        activityRef = mainRef.child("activites");
        Bundle data = getIntent().getExtras();
        if (data.getString("adminKey")!=null){
            adminstructorid = data.getString("adminKey");
        }else if(data.getString("instructorid")!=null){
            adminstructorid = data.getString("instructorid");
        }
        chrysActivity = (EditText)findViewById(R.id.activityName);
        chrysGroup = (Spinner)findViewById(R.id.chrysalisGroup);
        activityType = (Spinner)findViewById(R.id.activityType);
        activityPoints = (EditText)findViewById(R.id.activityPoints);
        activityDate = (EditText)findViewById(R.id.newActDate);
        submit = (Button)findViewById(R.id.submitAddAct);
        submit.setOnClickListener(this);
        activityDate.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
        activityType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getPointsByMeeting();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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

    @Override
    public void onClick(View view) {
        String key = activityRef.push().getKey();
        switch (view.getId()){
            case R.id.submitAddAct:
                String actname = chrysActivity.getText().toString();
                String group = chrysGroup.getSelectedItem().toString();
                int points = Integer.parseInt(activityPoints.getText().toString());
                String actdate = activityDate.getText().toString();
                ActivitiesBean newActivity =   new ActivitiesBean(actname,points,group,adminstructorid,actdate);
                Map<String,Object> addActivity = newActivity.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(key, addActivity);
                activityRef.updateChildren(childUpdates);
                Toast.makeText(this, "Activity Added successfully.", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.newActDate:
                new DatePickerDialog(AddNewActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
        }
    }

    private void getPointsByMeeting() {
        String activitytype = activityType.getSelectedItem().toString();
        switch (activitytype){
            case "Meeting":
                activityPoints.setText("100");
                break;
            case "Training Session":
                activityPoints.setText("500");
                break;
            case "POC":
                activityPoints.setText("1000");
                break;
            case "Real Life Deployement":
                activityPoints.setText("5000");
                break;
            default:
                activityPoints.setText("0");
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
