package com.studioemvs.chrysalis;

import android.net.Uri;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.security.KeyStore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Login Activity";
    EditText email,pwd;
    Button login,signup;
    TextView forgotPwd;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mainRef,userRef;
    Boolean adminState;
    String uid;
    VideoView videoBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mainRef = FirebaseDatabase.getInstance().getReference();
        userRef = mainRef.child("users");
        progressDialog = new ProgressDialog(this);
        //videoBackground = (VideoView)findViewById(R.id.videoView);
        //Uri uri = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.metamorphosis);
        //videoBackground.setVideoURI(uri);
        //videoBackground.start();
        email = (EditText)findViewById(R.id.edt_email);
        pwd = (EditText)findViewById(R.id.edt_password);
        login = (Button)findViewById(R.id.btn_Login);
        signup = (Button)findViewById(R.id.btn_signup);
        forgotPwd = (TextView)findViewById(R.id.forgotPassword);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    userRef.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User useData = dataSnapshot.getValue(User.class);
                            adminState = useData.getAdmin();
                            Intent intent = new Intent(LoginActivity.this,UserAndStatusActivity.class);
                            Bundle userBundle = new Bundle();
                            userBundle.putString("uid",uid);
                            intent.putExtras(userBundle);
                            startActivity(intent);
                           // roleBasedCheck(adminState,uid);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
    }

    private void roleBasedCheck(Boolean admin,String uid) {
        if (admin){
            Intent intent = new Intent(LoginActivity.this,AdminActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(LoginActivity.this,UserAndStatusActivity.class);
            Bundle userBundle = new Bundle();
            userBundle.putString("uid",uid);
            intent.putExtras(userBundle);
            startActivity(intent);
        }
    }

    private void signIn() {
        progressDialog.setMessage("Signing in");
        progressDialog.show();
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
                            Toast.makeText(LoginActivity.this, "Authentication failed."+task.getException(),
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
}
