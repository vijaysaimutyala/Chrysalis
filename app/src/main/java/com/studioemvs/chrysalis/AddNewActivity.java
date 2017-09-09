package com.studioemvs.chrysalis;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
    String chrysalisGroup,adminid;
    Button submit;
    EditText activityPoints,chrysActivity,activityDate;
    Spinner chrysGroup;
    Calendar myCalendar;
    DatabaseReference mainRef,activityRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        mainRef = FirebaseDatabase.getInstance().getReference();
        activityRef = mainRef.child("activites");
        Bundle data = getIntent().getExtras();
        adminid = data.getString("adminKey");
        chrysActivity = (EditText)findViewById(R.id.activityName);
        chrysGroup = (Spinner)findViewById(R.id.chrysalisGroup);
        activityPoints = (EditText)findViewById(R.id.activityPoints);
        activityDate = (EditText)findViewById(R.id.newActDate);
        submit = (Button)findViewById(R.id.submitAddAct);
        submit.setOnClickListener(this);
        activityDate.setOnClickListener(this);
        myCalendar = Calendar.getInstance();
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
                ActivitiesBean newActivity =   new ActivitiesBean(actname,points,group,adminid,actdate);
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
}
