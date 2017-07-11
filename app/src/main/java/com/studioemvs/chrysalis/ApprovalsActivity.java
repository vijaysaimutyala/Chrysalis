package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ApprovalsActivity extends AppCompatActivity implements View.OnClickListener{
    TextView username,activity,points;
    Button approve,reject;
    EditText comments;
    String activityToApprove,uidToApprove;
    int pointsToApprove;
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
        comments = (EditText)findViewById(R.id.approve_comments);
        approve.setOnClickListener(this);
        reject.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        pointsToApprove = bundle.getInt("points");
        activityToApprove = bundle.getString("activity");
        uidToApprove = bundle.getString("userid");

        points.setText(String.valueOf(pointsToApprove));
        activity.setText(activityToApprove);
        username.setText(uidToApprove);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.approveActivity:
                break;
            case R.id.rejectActivity:
                break;
        }

    }
}
