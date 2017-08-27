package com.studioemvs.chrysalis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity implements View.OnClickListener{
    EditText regEmail;
    Button submit;
    String email,tempPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        regEmail = (EditText)findViewById(R.id.pwdRstemail);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        email = regEmail.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        switch (view.getId()){
            case R.id.submit:
                if (email != null){
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordResetActivity.this, "Password reset mail has been sent to " + email + ". Please follow the instructions.", Toast.LENGTH_LONG).show();
                        }else if(!task.isSuccessful()){
                            Toast.makeText(PasswordResetActivity.this, "Failed to send mail. "+task.getException(), Toast.LENGTH_LONG).show();
                            Log.d("PasswordReset", "onComplete: "+task.getException());
                        }
                    }
                });

            }else{
                    Toast.makeText(this, "Please enter your registered email.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
