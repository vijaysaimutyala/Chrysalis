package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.studioemvs.chrysalis.models.ActivitiesBean;

public class AdminEditAddUpdateActivity extends AppCompatActivity {
    String adminKey,userKey;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView activityList;
    FirebaseRecyclerAdapter<ActivitiesBean,AdminEditAddUpdateActivity.ActivityListHolder> activityListAdapter;
    DatabaseReference mainRef,userRef,activityRef;
    ProgressDialog progressDialog;
    Query activityListQuery;
    String TAG = "AdminEdit activity"
            ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_add_update);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle adminbudle = getIntent().getExtras();
        adminKey = adminbudle.getString("adminkey");
        mainRef = FirebaseDatabase.getInstance().getReference();
        activityRef = mainRef.child("activites");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        activityList = (RecyclerView)findViewById(R.id.admin_activityList);
        activityList.setLayoutManager(new LinearLayoutManager(this));

        checkAuthorization();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent addActivity = new Intent(AdminEditAddUpdateActivity.this,AddNewActivity.class);
                bundle.putString("adminKey",adminKey);
                addActivity.putExtras(bundle);
                startActivity(addActivity);
                finish();
            }
        });
    }
    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    userKey = user.getUid();
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getActivities();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    public static class ActivityListHolder extends RecyclerView.ViewHolder{
        TextView activityName,activityPoints;
        CardView activityCard;
        public ActivityListHolder(View itemView) {
            super(itemView);
            activityCard = (CardView)itemView.findViewById(R.id.toApproveCardView);
            activityName = (TextView)itemView.findViewById(R.id.txt_toapprove);
            activityPoints = (TextView) itemView.findViewById(R.id.txt_toapprovepoints);
        }
    }
    private void getActivities() {

        activityListAdapter =  new FirebaseRecyclerAdapter<ActivitiesBean, ActivityListHolder>(ActivitiesBean.class,
                R.layout.dummy_tobeapproved,ActivityListHolder.class,activityRef) {
            @Override
            protected void populateViewHolder(ActivityListHolder viewHolder, ActivitiesBean model, int position) {
                DatabaseReference activityRef =getRef(position);
                final String activity = model.getActivity().toString();
                final int points = model.getActPoints();
                final String key = activityRef.getKey();
                viewHolder.activityName.setText(model.getActivity());
                viewHolder.activityPoints.setText(String.valueOf(model.getActPoints()));
                Log.d(TAG, "populateViewHolder: Activity:  "+model.getActivity()+"actPoints: "+model.getActPoints());
                viewHolder.activityCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(AdminEditAddUpdateActivity.this,AdminEditActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("activity",activity);
                        bundle.putInt("actPoints",points);
                        bundle.putString("activityKey",key);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
/*            @Override
            public User.RecentActivity getItem(int position){
                return super.getItem(getItemCount()-1-position);
            }*/
        };
        activityList.setAdapter(activityListAdapter);
        progressDialog.hide();

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
}
