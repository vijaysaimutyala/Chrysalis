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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

public class AdminActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "Admin Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef,recentActivityRef;
    String userKey;
    ProgressDialog progressDialog;
    Button registrationApproval,activityApproval,addActivity;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        getSupportActionBar().setTitle("Admin Portal");

        mainRef = FirebaseDatabase.getInstance().getReference();
        recentActivityRef = mainRef.child("recentActivity");
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        registrationApproval = (Button)findViewById(R.id.approveRegistration);
        activityApproval = (Button)findViewById(R.id.approveActivities);
        addActivity = (Button)findViewById(R.id.editUpdateActivities);
        registrationApproval.setOnClickListener(this);
        activityApproval.setOnClickListener(this);
        addActivity.setOnClickListener(this);
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
                    //progressDialog.setMessage("Fetching user data");
                    //progressDialog.show();
                } else {
                    // User is signed out
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_admin_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.adminProfileasUser:
                Intent adminAsUser = new Intent(AdminActivity.this,UserAndStatusActivity.class);
                startActivity(adminAsUser);
                finish();
                return true;
            case R.id.adminsignout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.approveRegistration:
                Intent intentReg = new Intent(AdminActivity.this,NewRegistrationsListActivity.class);
                startActivity(intentReg);
                finish();
                break;
            case R.id.approveActivities:
                Intent intent = new Intent(AdminActivity.this,UserActivitesApprovalListActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.editUpdateActivities:
                Intent addActIntent = new Intent(AdminActivity.this,AdminEditAddUpdateActivity.class);
                Bundle adminbundle = new Bundle();
                adminbundle.putString("adminkey",userKey);
                addActIntent.putExtras(adminbundle);
                startActivity(addActIntent);
                finish();
                break;

        }
    }
}
