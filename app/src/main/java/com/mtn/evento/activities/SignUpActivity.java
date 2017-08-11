package com.mtn.evento.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.Evento;
import com.mtn.evento.R;
import com.mtn.evento.data.User;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_ID;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField ,mPasswordField;
    private Button mSignUpButton;
    private EditText mUsername;
    private ImageView signInIcon;
    private TextView signInText;
    private boolean signUpTracker;
    ProgressDialog processSignUp;

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

        signInIcon = (ImageView) findViewById(R.id.signInIcon);
        signInText = (TextView) findViewById(R.id.loginText);

        mSignUpButton.setOnClickListener(this);

        signInText.setOnClickListener(this);
        signInIcon.setOnClickListener(this);
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
        processSignUp =  ProgressDialog.show(this,null,"Signing up and logging in....",true,false);
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.d(LOGMESSAGE, "createUser:onComplete:" + task.isSuccessful());
                        processSignUp.hide();
                        if (task.isSuccessful()) {

                            onAuthSuccess(task.getResult().getUser());
                            Intent intent = new Intent();
                            intent.putExtra( LoginActivity.SIGNED_UP ,true);
                            intent.putExtra( LoginActivity.USERNAME,mUsername.getText().toString());
                            intent.putExtra( LoginActivity.EMAIL,mEmailField.getText().toString());
                            setResult(Activity.RESULT_OK,intent);
                            signUpTracker = true ;
                            finish();
                        }
                    }
                })
                .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                     @Override
                     public void onFailure( Exception e) {

                         signUpTracker = false ;
                         Toast.makeText(SignUpActivity.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
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
        User user = new User();

        user.setUsername(name);
        user.setEmail(email);
        user.setPhone("###-###-####");
        user.setId(userId);

        mDatabase.child("users").child(userId).setValue(user);

        if( !((Evento)getApplication()).getSettings().contains(APP_USER_ID) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_ID,userId).commit();
        }
        else{
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_ID,userId).commit();
        }

        if( !((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,name).commit();
        }
        else{
            ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,name).commit();
        }


        if( !((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,email).commit();
        }
        else
        {
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,email).commit();
        }

        if( !((Evento)getApplication()).getSettings().contains(APP_USER_PHONE) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_PHONE,"###-###-####").commit();
        }

    }
    // [END basic_write]

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signUpBtn) {
            signUp();
        }
        else  if (i == R.id.signInIcon) {
            finish();
        }
        else  if (i == R.id.loginText) {
            finish();
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

    @Override
    public void onBackPressed() {

        if(!signUpTracker){
            Intent intent = new Intent();
            intent.putExtra( LoginActivity.SIGNED_UP ,false);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }


    }
}
