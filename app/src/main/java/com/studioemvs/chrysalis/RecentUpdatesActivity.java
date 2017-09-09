package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.studioemvs.chrysalis.models.User;

public class RecentUpdatesActivity extends AppCompatActivity {
    RecyclerView recentActivity;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef,recentActivityRef;
    String TAG ="Recent Activity";
    String userKey,username,chrysLevel,chrysGroup,chrysPoints;
    Query userDataQuery,activityQuery;
    TextView name,level,points,group;
    ProgressDialog progressDialog;
    FirebaseRecyclerAdapter<User.RecentActivity,RecentUpdatesActivity.ActivityHolder> activityAdapter;
    String act,actpoints,actdate,actadminId,actadminComments,actkey,actapproval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_updates);
        getSupportActionBar().setTitle("Recent Activity");
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        recentActivityRef = mainRef.child("recentActivity");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        recentActivity = (RecyclerView)findViewById(R.id.recUpdate_recentActivity);
        recentActivity.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(this);
        checkAuthorization();
    }
    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userKey = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: "+userKey+"uid: "+user.getUid());
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    //getUserData(userKey);//settingtextView
                    getRecentActivity(userKey);//settingRecentActivities
                } else {
                    // User is signed out
                    Intent intent = new Intent(RecentUpdatesActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void getRecentActivity(final String userkey) {
        Toast.makeText(RecentUpdatesActivity.this, "Called recent activity", Toast.LENGTH_SHORT).show();
        activityQuery = userRef.child(userkey).child("recentActivity").orderByChild("id");
        activityAdapter =new FirebaseRecyclerAdapter<User.RecentActivity, RecentUpdatesActivity.ActivityHolder>
                (User.RecentActivity.class, R.layout.activity_recent_dummy,RecentUpdatesActivity.ActivityHolder.class,activityQuery) {
            @Override
            protected void populateViewHolder(RecentUpdatesActivity.ActivityHolder viewHolder,
                                              final User.RecentActivity model, int position) {
                actapproval = model.getApproval().toString();
                act = model.getActivity().toString();
                actpoints = String.valueOf(model.getPoints());
                actadminComments = model.getAdminComments().toString();
                actadminId = model.getApprovedBy().toString();
                actdate = model.getActivityDate().toString();
                //actkey = model.getActivityKey().toString();
                Log.d(TAG, "populateViewHolder: "+act+actapproval+actadminId+actadminComments);
                if (actapproval.equals("no")){
                    viewHolder.cv.setCardBackgroundColor(Color.parseColor("#ff6f00"));
                }else if(actapproval.equals("yes")){
                    viewHolder.cv.setCardBackgroundColor(Color.parseColor("#00c853"));
                }else if (actapproval.equals("rejected")){
                    viewHolder.cv.setCardBackgroundColor(Color.parseColor("#ff0000"));
                }
                viewHolder.activity.setText(model.getActivity());
                viewHolder.cv.setRadius(2);
                viewHolder.points.setText(String.valueOf(model.getPoints()));
                Log.d(TAG, "populateViewHolder: "+model.getApproval()+model.getActivity());


                viewHolder.cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle actBundle = new Bundle();
                        actBundle.putString("activity",act);
                        actBundle.putString("actPoints",actpoints);
                        actBundle.putString("date",actdate);
                        actBundle.putString("adminComm",actadminComments);
                        actBundle.putString("adminid",actadminId);
                        actBundle.putString("approval",actapproval);
                        Intent intent = new Intent(RecentUpdatesActivity.this,ActivityApprovalDetials.class);
                        intent.putExtras(actBundle);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        };
        recentActivity.setAdapter(activityAdapter);
        progressDialog.hide();
    }

    public static class ActivityHolder extends RecyclerView.ViewHolder{
        TextView activity;
        TextView points;
        public CardView cv;


        public ActivityHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.card_recentActivity);
            activity = (TextView)itemView.findViewById(R.id.txt_activity);
            points = (TextView)itemView.findViewById(R.id.txt_points);

        }
    }
}

