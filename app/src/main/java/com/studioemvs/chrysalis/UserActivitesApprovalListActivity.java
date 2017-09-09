package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.studioemvs.chrysalis.models.User;

public class UserActivitesApprovalListActivity extends AppCompatActivity {
    private static final String TAG = "activity Approvals List";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView toBeApproved;
    FirebaseRecyclerAdapter<User.RecentActivity,UserActivitesApprovalListActivity.ToApproveHolder> toApproveAdapter;
    DatabaseReference mainRef,userRef,recentActivityRef;
    String userKey;
    ProgressDialog progressDialog;
    Query toApproveQuery;
    String approvalState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_activites_approval_list);
        getSupportActionBar().setTitle("Approvals");
        mainRef = FirebaseDatabase.getInstance().getReference();
        recentActivityRef = mainRef.child("recentActivity");
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        toBeApproved = (RecyclerView)findViewById(R.id.approvalPending);
        toBeApproved.setLayoutManager(new LinearLayoutManager(this));

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
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getAppovalData(userKey);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }
    private void getAppovalData(String userKey) {
        toApproveQuery = recentActivityRef.orderByChild("approval").equalTo("no");

        toApproveAdapter =  new FirebaseRecyclerAdapter<User.RecentActivity, ToApproveHolder>(User.RecentActivity.class,
                R.layout.dummy_tobeapproved, ToApproveHolder.class,toApproveQuery) {
            @Override
            protected void populateViewHolder(ToApproveHolder viewHolder, User.RecentActivity model, int position) {
                DatabaseReference activityRef =getRef(position);
                final String activity = model.getActivity().toString();
                final String userid = model.getUserid();
                final int points = model.getPoints();
                final Long id = model.getId();
                final int empid = model.getEmpid();
                final String key = activityRef.getKey();
                final String activityKeyInMain = model.getActivityKey();
                final String userComments = model.getUserComments();
                final String activityDate = model.getActivityDate();

                viewHolder.toapprove.setText(model.getActivity());
                viewHolder.toapprovepoints.setText(String.valueOf(model.getPoints()));
                approvalState = model.getApproval();
                Log.d(TAG, "populateViewHolder: Activity:  "+model.getActivity()+"actPoints: "+model.getPoints());
                viewHolder.toApproveCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserActivitesApprovalListActivity.this,ApprovalsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("activity",activity);
                        bundle.putInt("actPoints",points);
                        bundle.putString("userid",userid);
                        bundle.putString("approvalState",approvalState);
                        bundle.putLong("id",id);
                        bundle.putString("activityKey",key);
                        bundle.putString("userComments",userComments);
                        bundle.putString("activityKeyInMain",activityKeyInMain);
                        bundle.putString("activityDate",activityDate);
                        bundle.putInt("empid",empid);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                });


            }
/*            @Override
            public User.RecentActivity getItem(int position){
                return super.getItem(getItemCount()-1-position);
            }*/
        };
        toBeApproved.setAdapter(toApproveAdapter);
        progressDialog.hide();

    }

    public static class ToApproveHolder extends RecyclerView.ViewHolder{
        TextView toapprove,toapprovepoints;
        CardView toApproveCardView;


        public ToApproveHolder(View itemView) {
            super(itemView);
            toApproveCardView = (CardView)itemView.findViewById(R.id.toApproveCardView);
            toapprove = (TextView)itemView.findViewById(R.id.txt_toapprove);
            toapprovepoints = (TextView) itemView.findViewById(R.id.txt_toapprovepoints);

        }
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
