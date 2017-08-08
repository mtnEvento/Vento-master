package com.mtn.evento.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.Evento;
import com.mtn.evento.R;
import com.mtn.evento.data.User;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_ID;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;

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
        String   userId = "";
        Intent intent =null ;
        if( ((Evento)getApplication()).getSettings().contains(APP_USER_ID) ){
            userId =  ((Evento)getApplication()).getSettings().getString(APP_USER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        if(v.getId()== R.id.updateBtn){
            if(mIntent.hasExtra(EDIT_EMAIL)){
                updateUserInfo(userId, "email",editableValue.getText().toString());
                intent = new Intent();
                intent.putExtra(EDIT_EMAIL,editableValue.getText().toString());
                setResult(Activity.RESULT_OK,intent);
            }
            else  if(mIntent.hasExtra(EDIT_USERNAME)){
                updateUserInfo(userId, "username",editableValue.getText().toString());
                intent = new Intent();
                intent.putExtra(EDIT_USERNAME,editableValue.getText().toString());
                setResult(Activity.RESULT_OK,intent);
            }
            else  if(mIntent.hasExtra(EDIT_PHONE)){
                updateUserInfo(userId, "phone",editableValue.getText().toString());
                intent = new Intent();
                intent.putExtra(EDIT_PHONE,editableValue.getText().toString());
                setResult(Activity.RESULT_OK,intent);
            }
        }
    }

    // [START basic_write]
    private void updateUserInfo(String userId,String edit, String editValue) {

        if(mIntent.hasExtra(EDIT_EMAIL)){
            mDatabase.child("users").child(userId).child(edit).setValue(editValue);
            if( !((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,editValue).commit();
            }
            else
            {
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,editValue).commit();
            }
        }
        else  if(mIntent.hasExtra(EDIT_USERNAME)){
            mDatabase.child("users").child(userId).child(edit).setValue(editValue);
            if( !((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
                ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,editValue).commit();
            }
            else{
                ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,editValue).commit();
            }
        }
        else  if(mIntent.hasExtra(EDIT_PHONE)){
            mDatabase.child("users").child(userId).child(edit).setValue(editValue);
           if( !((Evento)getApplication()).getSettings().contains(APP_USER_PHONE) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_PHONE,editValue).commit();;
            }
            else
            {
                ((Evento)getApplication()).getSettings().edit().putString(APP_USER_PHONE,editValue).commit();;
            }
        }
    }
    // [END basic_write]

}