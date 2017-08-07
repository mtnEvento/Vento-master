package com.mtn.evento.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.R;
import com.mtn.evento.data.User;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField ,mPasswordField;
    private Button mSignUpButton;
    private EditText mUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        // Views
        mUsername = (EditText) findViewById(R.id.username);
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mSignUpButton = (Button) findViewById(R.id.signUpBtn);

        mSignUpButton.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signUp() {
        Log.d(LOGMESSAGE, "signUp");
        if (!validateForm()) {
            return;
        }

        //showProgressDialog();
        final  ProgressDialog processSignUp =  ProgressDialog.show(this,"","Signing up and logging in....",true,false);
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOGMESSAGE, "createUser:onComplete:" + task.isSuccessful());
                        //   hideProgressDialog();
                        processSignUp.hide();

                        if (task.isSuccessful()) {

                            onAuthSuccess(task.getResult().getUser());
                            Intent intent = new Intent();
                            intent.putExtra( LoginActivity.SIGNED_UP ,true);
                            intent.putExtra( LoginActivity.USERNAME,mUsername.getText().toString());
                            intent.putExtra( LoginActivity.EMAIL,mEmailField.getText().toString());
                            setResult(Activity.RESULT_OK,intent);
                            SignUpActivity.super.onBackPressed();
                        }
                    }
                })
                .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
//                         Toast.makeText(SignUpActivity.this, "Sign Up Failed :"+e.getLocalizedMessage(),
//                                 Toast.LENGTH_SHORT).show();
//                        if(e.getLocalizedMessage().contains("")){
//
//                        }
                         Intent intent = new Intent();
                         intent.putExtra( LoginActivity.SIGNED_UP ,false);
                         setResult(Activity.RESULT_OK,intent);
                         SignUpActivity.super.onBackPressed();
                     }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
       // String username = usernameFromEmail(user.getEmail());
        String username =  mUsername.getText().toString();
        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private boolean validateForm() {
        boolean result = true;

        if (TextUtils.isEmpty(mUsername.getText().toString())) {
            mUsername.setError("Required");
            result = false;
        } else {
            mUsername.setError(null);
        }
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError("Required");
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError("Required");
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        return result;
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            signUp();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }
}
