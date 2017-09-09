package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.studioemvs.chrysalis.models.User;

public class NewRegistrationsListActivity extends AppCompatActivity {
    private static final String TAG = "activity Approvals List";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView reigstrationsToBeApproved;
    FirebaseRecyclerAdapter<User,NewRegistrationsListActivity.RegToApproveHolder> toApproveAdapter;
    DatabaseReference mainRef,userRef,recentActivityRef;
    String userKey,userEmail;
    ProgressDialog progressDialog;
    Query registrationsToApproveQuery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_registrations_list);
        getSupportActionBar().setTitle("New Registrants");
        mainRef = FirebaseDatabase.getInstance().getReference();
        recentActivityRef = mainRef.child("recentActivity");
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        reigstrationsToBeApproved = (RecyclerView)findViewById(R.id.registrationPending);
        reigstrationsToBeApproved.setLayoutManager(new LinearLayoutManager(this));
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
                    userEmail = user.getEmail();
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getUserData(userKey);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void getUserData(final String userKey) {
        registrationsToApproveQuery = userRef.orderByChild("registrationApproved").equalTo(false);
        toApproveAdapter =  new FirebaseRecyclerAdapter<User, NewRegistrationsListActivity.RegToApproveHolder>(User.class,
                R.layout.dummy_user_registraion_approval, NewRegistrationsListActivity.RegToApproveHolder.class, registrationsToApproveQuery) {
            @Override
            protected void populateViewHolder(NewRegistrationsListActivity.RegToApproveHolder viewHolder, User model, int position) {
                DatabaseReference userKeyRef =getRef(position);
                final String name = model.getUsername();
                final String key = userKeyRef.getKey();
                final String usermail = model.getEmailid();
                final String group = model.getChrysalisGroup();
                final String userPrevWork = model.getPersonalProjects();

                viewHolder.username.setText(model.getUsername());
                viewHolder.useremail.setText(model.getEmailid());
                viewHolder.prevWork.setText(model.getPersonalProjects());
                viewHolder.userGroup.setText(model.getChrysalisGroup());
                viewHolder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewRegistrationsListActivity.this, "approve clicked !", Toast.LENGTH_SHORT).show();
                        userRef.child(key).child("registrationApproved").setValue(true);
                    }
                });
                viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewRegistrationsListActivity.this, "reject clicked !", Toast.LENGTH_SHORT).show();
                        userRef.child(key).removeValue();
                    }
                });

            }
            @Override
            public User getItem(int position){
                return super.getItem(getItemCount()-1-position);
            }
        };
        reigstrationsToBeApproved.setAdapter(toApproveAdapter);
        progressDialog.hide();

    }

    public static class RegToApproveHolder extends RecyclerView.ViewHolder {
        TextView username,useremail,prevWork,userGroup;
        CardView toApproveCardView;
        Button approve,reject;



        public RegToApproveHolder(View itemView) {
            super(itemView);
            toApproveCardView = (CardView)itemView.findViewById(R.id.regToApproveCardView);
            username = (TextView)itemView.findViewById(R.id.txt_usertoapprove);
            useremail = (TextView)itemView.findViewById(R.id.txt_useremail);
            userGroup = (TextView)itemView.findViewById(R.id.txt_userGroup);
            prevWork = (TextView)itemView.findViewById(R.id.txt_prevWork);
            approve = (Button)itemView.findViewById(R.id.adminApproveRegistration);
            reject = (Button)itemView.findViewById(R.id.adminRejectRegistration);
        }
/*
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.adminApproveRegistration:
                    Toast.makeText(this.getClass(), "Approval", Toast.LENGTH_SHORT).show();
            }
        }*/
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
