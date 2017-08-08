package com.mtn.evento.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.R;
import com.mtn.evento.data.User;

/**
 * Created by Summy on 8/8/2017.
 */

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editableValue;
    private Button updateBtn;
    private Intent mIntent;

    public final static String EDIT_EMAIL = "EDIT_EMAIL";
    public final static String EDIT_USERNAME = "EDIT_USERNAME";
    public final static String EDIT_PHONE = "EDIT_PHONE";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editableValue = (EditText)findViewById(R.id.editableValue);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mIntent =  getIntent();
        if(mIntent != null ){

            if(mIntent.hasExtra(EDIT_EMAIL)){
                String email = mIntent.getStringExtra(EDIT_EMAIL);
                editableValue.setText(email);
            }
            else  if(mIntent.hasExtra(EDIT_USERNAME)){
                String username = mIntent.getStringExtra(EDIT_USERNAME);
                editableValue.setText(username);
            }
            else  if(mIntent.hasExtra(EDIT_PHONE)){
                String phone = mIntent.getStringExtra(EDIT_PHONE);
                editableValue.setText(phone);
            }
        }


    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.updateBtn){
            if(mIntent.hasExtra(EDIT_EMAIL)){

            }
            else  if(mIntent.hasExtra(EDIT_USERNAME)){

            }
            else  if(mIntent.hasExtra(EDIT_PHONE)){

            }
        }
    }

    // [START basic_write]
    private void writeNewUser(String userId, String name, String email) {
        User user = new User();

        user.setUsername(name);
        user.setEmail(email);
        user.setPhone("fddfdf");
        user.setId(userId);

        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]

}
