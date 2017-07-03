package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;

public class UpdateProgressActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner activity;
    Button submit;
    TextView points;
    HashMap<String,Integer> pointsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_progress);
        activity = (Spinner)findViewById(R.id.activitySpinner);
        submit = (Button)findViewById(R.id.submitActivity);
        points = (TextView)findViewById(R.id.pointsForActivity);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.activities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activity.setAdapter(adapter);
        activity.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        pointsMap = new Gson().fromJson(getString(R.string.activityPoints), new TypeToken<HashMap<String, String>>(){}.getType());

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
