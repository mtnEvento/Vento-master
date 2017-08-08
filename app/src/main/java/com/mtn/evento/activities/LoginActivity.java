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
import com.mtn.evento.R;
import com.mtn.evento.data.User;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String SIGNED_UP = "SIGNED_UP";
    public static final String USERNAME ="USERNAME" ;
    public static final String EMAIL ="EMAIL" ;
    Toolbar toolbar;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mEmailField ,mPasswordField;
    private Button mSignInButton;
    private EditText mUsername;
    private ImageView signUpIcon;
    private TextView signUpText;
    private final int REQUEST_SIGNUP = 77;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        // Views
       // mUsername = (EditText) findViewById(R.id.username);
        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.loginBtn);
        signUpIcon = (ImageView) findViewById(R.id.signUpIcon);
        signUpText = (TextView) findViewById(R.id.signUpText);

        mSignInButton.setOnClickListener(this);
        signUpText.setOnClickListener(this);
        signUpIcon.setOnClickListener(this);

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

    private void signIn() {
        Log.d(LOGMESSAGE, "signIn");
        if (!validateForm()) {
            return;
        }

       // showProgressDialog();
       final  ProgressDialog processLogin =  ProgressDialog.show(this,"","Logging in ....",true,false);
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.d(LOGMESSAGE, "signIn:onComplete:" + task.isSuccessful());
                       // hideProgressDialog();
                        processLogin.hide();
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( Exception e) {
                Toast.makeText(LoginActivity.this, "Sign In Failed \n\n" +e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
                if (e.getLocalizedMessage().contains("There is no user record")){

                }
            }
        });
    }


    private void onAuthSuccess(FirebaseUser user) {
        String username = usernameFromEmail(user.getEmail());

        // Write new user
        writeNewUser(user.getUid(), username, user.getEmail());

        // Go to MainActivity
        Intent intent = new Intent();
        intent.putExtra( HomeScreenActivity.LOGINED_IN ,true);
        intent.putExtra(LoginActivity.EMAIL,user.getEmail());
        intent.putExtra(LoginActivity.USERNAME,username);
        setResult(Activity.RESULT_OK,intent);
        super.onBackPressed();
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
        if (i == R.id.loginBtn) {
            signIn();
        }
        else if(i == R.id.signUpText){
            startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class),REQUEST_SIGNUP);
        }
        else if(i == R.id.signUpIcon){
            startActivityForResult(new Intent(LoginActivity.this, SignUpActivity.class),REQUEST_SIGNUP);
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

        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }else
        {
            Intent intent = new Intent();
            intent.putExtra( HomeScreenActivity.LOGINED_IN ,false);
            setResult(Activity.RESULT_OK,intent);
        }

        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SIGNUP && resultCode== RESULT_OK){
            boolean loginedIn  = data.getBooleanExtra(SIGNED_UP,false);
            if(loginedIn){
                Intent intent = new Intent();
                intent.putExtra( HomeScreenActivity.LOGINED_IN ,true);
                intent.putExtra(LoginActivity.EMAIL,data.getStringExtra(LoginActivity.EMAIL));
                intent.putExtra(LoginActivity.USERNAME,data.getStringExtra(LoginActivity.USERNAME));
                setResult(Activity.RESULT_OK,intent);
                LoginActivity.this.onBackPressed();
            }
            else{

                Intent intent = new Intent();
                intent.putExtra( HomeScreenActivity.LOGINED_IN ,false);
                setResult(Activity.RESULT_OK,intent);
                LoginActivity.this.onBackPressed();
            }
        }
    }
}
