package com.studioemvs.chrysalis;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.studioemvs.chrysalis.models.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Login Activity";
    EditText email,pwd;
    Button login,signup;
    TextView forgotPwd;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;
    Boolean adminState,registrationState,instructorState;
    String uid;
    RelativeLayout relativeLayout;
    FirebaseUser globUser;
    private Boolean exitBool = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        email = (EditText)findViewById(R.id.edt_email);
        pwd = (EditText)findViewById(R.id.edt_password);
        login = (Button)findViewById(R.id.btn_Login);
        signup = (Button)findViewById(R.id.btn_signup);
        forgotPwd = (TextView)findViewById(R.id.forgotPassword);
        relativeLayout = (RelativeLayout)findViewById(R.id.loginRelativeLayout);
        forgotPwd.setOnClickListener(this);
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        checkAuthorzation();
    }

    private void checkAuthorzation() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                globUser = firebaseAuth.getCurrentUser();
                if (globUser != null) {
                    uid = globUser.getUid();
                    userRef.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userData = dataSnapshot.getValue(User.class);
                            adminState =userData.getAdmin();
                            instructorState =userData.getInstructor();
                            registrationState = userData.getRegistrationApproved();
                            if (registrationState){
                                Intent intent = new Intent(LoginActivity.this,UserAndStatusActivity.class);
                                Bundle userBundle = new Bundle();
                                userBundle.putBoolean("registrationState",registrationState);
                                userBundle.putString("uid",uid);
                                if (adminState){
                                    userBundle.putBoolean("adminState",adminState);
                                }else if(instructorState){
                                    userBundle.putBoolean("instructorState",instructorState);
                                }
                                intent.putExtras(userBundle);
                                startActivity(intent);
                                finish();
                            }else {
                                Snackbar snackbar = Snackbar
                                        .make(relativeLayout, "Registration not yet confirmed by admin", Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }
    private void signIn() {
        progressDialog.setMessage("Signing in");
        progressDialog.show();
        if (globUser != null) {
            uid = globUser.getUid();
            userRef.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User userData = dataSnapshot.getValue(User.class);
                    adminState = userData.getAdmin();
                    registrationState = userData.getRegistrationApproved();
                    if (registrationState) {
                        Intent intent = new Intent(LoginActivity.this, UserAndStatusActivity.class);
                        Bundle userBundle = new Bundle();
                        userBundle.putBoolean("adminState", adminState);
                        userBundle.putBoolean("registrationState", registrationState);
                        userBundle.putString("uid", uid);
                        intent.putExtras(userBundle);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, "Registration not yet confirmed by admin", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pwd.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        progressDialog.hide();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

//    @Override
//    public void onBackPressed() {
//        if(exitBool){
////            FirebaseAuth.getInstance().signOut();
//            finish();
//        }else{
//            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
//            exitBool = true;
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    exitBool = false;
//                }
//            },3*1000);
//        }
//    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forgotPassword:
                Intent forgotPwd = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(forgotPwd);
                break;
            case R.id.btn_signup:
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Login:
                signIn();
                break;
        }
    }
}
