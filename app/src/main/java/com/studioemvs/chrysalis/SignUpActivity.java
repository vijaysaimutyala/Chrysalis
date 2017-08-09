package com.studioemvs.chrysalis;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "Sign Up";
    EditText email,pwd,cnfpwd,username,personalProject,empId;
    Button signUp;
    Spinner chrysalisGroup;
    ProgressDialog progressDialog;
    RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        relativeLayout = (RelativeLayout)findViewById(R.id.signUpActivity);
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(SignUpActivity.this, "User" +user.getUid()+"is logged in!", Toast.LENGTH_SHORT).show();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // ...
        email = (EditText)findViewById(R.id.edt_signup_email);
        pwd = (EditText)findViewById(R.id.edt_signup_password);
        cnfpwd = (EditText)findViewById(R.id.edt_signup_confirm_password);
        signUp = (Button)findViewById(R.id.btn_signup_signup);
        username = (EditText)findViewById(R.id.signup_username);
        chrysalisGroup = (Spinner)findViewById(R.id.interestGroup);
        empId = (EditText)findViewById(R.id.signup_empId);
        //areaOfCurrentWork = (EditText)findViewById(R.id.areaOfCurrentWork);
        personalProject = (EditText)findViewById(R.id.personalProject);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpOnFirebase();
            }
/*                if (pwd.length()<8){
                    Toast.makeText(SignUpActivity.this, "Password must be of minimum 8 characters.", Toast.LENGTH_SHORT).show();
                }else {
                    if (pwd.getText().toString().equals(cnfpwd.getText().toString())){
                        signUpOnFirebase();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }*/
        });


    }

    private void signUpOnFirebase() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing Up");
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email.getText().toString(), cnfpwd.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." +task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            String emailid = email.getText().toString();
                            String uid = mAuth.getCurrentUser().getUid().toString();
                            saveUserDataToFirebase(emailid,uid);
                        }
                        progressDialog.hide();
                    }
                });
    }

    private void saveUserDataToFirebase(String emailid, String uid) {
        Log.d(TAG, "saveUserDataToFirebase: "+emailid+uid);
        // Read from the database
        String name = username.getText().toString();
        String group = chrysalisGroup.getSelectedItem().toString();
        int employeeId = Integer.parseInt(empId.getText().toString());
        String key = uid;
        int points = 0;
        int pointsToBeApproved = 0;
        String chrysalisLevel = "Beginner";//
        String personalProjects = personalProject.getText().toString();
        Boolean admin = false;
        Boolean regApproved = false;
        String chrysSubLevel = "1.1";
        User newUser = new User(emailid,name,points, group,chrysalisLevel,personalProjects,admin,key,regApproved,chrysSubLevel,pointsToBeApproved,employeeId);
        Map<String,Object> addUser = newUser.toMap();
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(key, addUser);
        userRef.updateChildren(childUpdates);

        Snackbar signUpSuccess = Snackbar.make(relativeLayout,"Account created successfully!",Snackbar.LENGTH_LONG);
        signUpSuccess.show();

        Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();

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
