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
import com.mtn.evento.Factory;
import com.mtn.evento.R;
import com.mtn.evento.data.User;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_ID;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;
import static com.mtn.evento.data.Constants.LOGINED_IN;
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
    ProgressDialog processLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void signIn() {
        Log.d(LOGMESSAGE, "signIn");
        if (!validateForm()) {
            return;
        }

        if(processLogin == null){
            processLogin =  ProgressDialog.show(LoginActivity.this,"","Logging In ....",true,false);
        }

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        Log.d(LOGMESSAGE, "signIn:onComplete:" + task.isSuccessful());
                        if(processLogin != null){
                            processLogin.hide();
                        }
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

        if(user  != null){

            String username = usernameFromEmail(user.getEmail());
            String str_username = null ;
            if( ((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
                 str_username = ((Evento)getApplication()).getSettings().getString(APP_USERNAME,username);
            }
            else
            {
                str_username = username ;
            }


            if( !((Evento)getApplication()).getSettings().contains(APP_USER_ID) ){
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_ID,user.getUid()).commit();
            }
            else{
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_ID,user.getUid()).commit();
            }

            if( !((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
                ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,str_username).commit();
            }
            else
            {
                if(str_username != null && !str_username.equalsIgnoreCase(username)){
                    ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,str_username).commit();
                }
                else
                {
                    ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,str_username).commit();
                }

            }

            if( !((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,user.getEmail()).commit();
            }
            else
            {
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,user.getEmail()).commit();
            }

            Intent intent = new Intent();
            intent.putExtra( LOGINED_IN ,true);
            intent.putExtra(LoginActivity.EMAIL,user.getEmail());
            intent.putExtra(LoginActivity.USERNAME,str_username);
            setResult(Activity.RESULT_OK,intent);
            finish();
        }

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

        if(! mEmailField.getText().toString().contains("@")){
            mEmailField.setError("Must contain '@' sign");
            result = false;
        }
        else
        {
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
            intent.putExtra( LOGINED_IN ,false);
            setResult(Activity.RESULT_OK,intent);
            finish();
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
                intent.putExtra( LOGINED_IN ,true);
                intent.putExtra(LoginActivity.EMAIL,data.getStringExtra(LoginActivity.EMAIL));
                intent.putExtra(LoginActivity.USERNAME,data.getStringExtra(LoginActivity.USERNAME));
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
            else{

                Intent intent = new Intent();
                intent.putExtra( LOGINED_IN ,false);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        }
    }
}
