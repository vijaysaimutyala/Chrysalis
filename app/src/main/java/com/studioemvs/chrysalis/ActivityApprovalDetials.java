package com.studioemvs.chrysalis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ActivityApprovalDetials extends AppCompatActivity {
    TextView activity,points,date,adminId,adminComments,rejectionText,status;
    String approval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_detials);
        activity = (TextView)findViewById(R.id.user_update_rslt_activity);
        points= (TextView)findViewById(R.id.user_update_rslt_points);
        date= (TextView)findViewById(R.id.user_date_rslt_points);
        adminId= (TextView)findViewById(R.id.user_rslt_adminId);
        adminComments= (TextView)findViewById(R.id.user_rslt_adminCommetns);
        rejectionText= (TextView)findViewById(R.id.rejectionText);
        status = (TextView)findViewById(R.id.user_rslt_approvalStatus);
        Bundle actBundle = getIntent().getExtras();
        activity.setText(actBundle.getString("activity"));
        points.setText(actBundle.getString("actPoints"));
        date.setText(actBundle.getString("date"));
        adminComments.setText(actBundle.getString("adminComm"));
        adminId.setText(actBundle.getString("adminid"));
        approval = actBundle.getString("approval");
        if (approval.equals("rejected")){
            status.setText("Rejected");
        }else if(approval.equals("yes")){
            status.setText("Approved");
            rejectionText.setVisibility(View.GONE);
        }else if(approval.equals("no")){
            status.setText("Approval In Progress");
            rejectionText.setVisibility(View.GONE);
        }
    }

}
