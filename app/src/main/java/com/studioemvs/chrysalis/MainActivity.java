package com.studioemvs.chrysalis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.icu.util.BuddhistCalendar;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;
    String TAG ="Main Activity";
    String userKey,username,chrysLevel,chrysGroup,chrysPoints;
    Query userDataQuery,activityQuery;
    TextView name,level,points,group;
    ProgressDialog progressDialog;
    Button updateActivity,redeemPoints;
    RecyclerView recentActivity;
    FirebaseRecyclerAdapter<User.RecentActivity,ActivityHolder> activityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("My Profile");

        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");

        mAuth = FirebaseAuth.getInstance();
        name = (TextView)findViewById(R.id.profileName);
        level = (TextView)findViewById(R.id.chrysLevel);
        group = (TextView)findViewById(R.id.chrysGroup);
        points = (TextView)findViewById(R.id.chrysPoints);
        updateActivity = (Button)findViewById(R.id.updateActivity);
        redeemPoints = (Button)findViewById(R.id.redeemPoints);

        recentActivity = (RecyclerView)findViewById(R.id.rv_recentActivity);
        recentActivity.setLayoutManager(new LinearLayoutManager(this));


        progressDialog = new ProgressDialog(this);
        updateActivity.setOnClickListener(this);
        redeemPoints.setOnClickListener(this);

        checkAuthorization();
    }

    private void checkAuthorization() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Toast.makeText(MainActivity.this, "User" +user.getEmail()+"is logged in!", Toast.LENGTH_SHORT).show();
                    userKey = user.getUid();
                    Log.d(TAG, "onAuthStateChanged: "+userKey+"uid: "+user.getUid());
                    progressDialog.setMessage("Fetching user data");
                    progressDialog.show();
                    getUserData(userKey);//settingtextView
                    getRecentActivity(userKey);//settingRecentActivities
                } else {
                    // User is signed out
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void getRecentActivity(final String userkey) {
        activityQuery = userRef.child(userkey).child("recentActivity").orderByChild("id");
        activityAdapter =new FirebaseRecyclerAdapter<User.RecentActivity, ActivityHolder>(User.RecentActivity.class,R.layout.activity_recent_dummy,ActivityHolder.class,activityQuery) {
            @Override
            protected void populateViewHolder(ActivityHolder viewHolder, User.RecentActivity model, int position) {
                viewHolder.activity.setText(model.getActivity());
            }
        };
        recentActivity.setAdapter(activityAdapter);
        progressDialog.hide();
    }


    public static class ActivityHolder extends RecyclerView.ViewHolder{
        TextView activity;

        public ActivityHolder(View itemView) {
            super(itemView);
            activity = (TextView)itemView.findViewById(R.id.txt_activity);
        }
    }

    private void getUserData(final String userkey) {
        Log.d(TAG, "getUserData: "+userkey);
        userRef.child(userkey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User userProfile = dataSnapshot.getValue(User.class);
                username = userProfile.getUsername();
                chrysLevel = userProfile.getChrysalisLevel();
                chrysGroup = userProfile.getChrysalisGroup();
                chrysPoints = String.valueOf(userProfile.getChrysalisPoints());
                name.setText(username);
                level.setText(chrysLevel);
                points.setText(chrysPoints);
                group.setText(chrysGroup);
                }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_activity, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.test:
                Toast.makeText(MainActivity.this, "Test Successful!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.signout:
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.updateActivity:
                Bundle bundle = new Bundle();
                bundle.putInt("points", Integer.parseInt(chrysPoints));
                Intent updateAct = new Intent(this,ProgressUpdateActivity.class);
                updateAct.putExtras(bundle);
                startActivity(updateAct);
                finish();
                break;
            case R.id.redeemPoints:
                Bundle bunndle = new Bundle();
                bunndle.putInt("points", Integer.parseInt(chrysPoints));
                Intent redeemAct = new Intent(this, RedeemPointsActivity.class);
                redeemAct.putExtras(bunndle);
                startActivity(redeemAct);
                finish();
                break;
        }
    }
}

