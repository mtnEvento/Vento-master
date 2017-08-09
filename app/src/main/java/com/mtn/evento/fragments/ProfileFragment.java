package com.mtn.evento.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.Evento;
import com.mtn.evento.R;
import com.mtn.evento.activities.EditorActivity;
import com.mtn.evento.activities.SignUpActivity;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    AppCompatActivity mContext;
    TextView email, phone, username ;
    private final int EMAIL_EDIT_REQUEST = 120;
    private final int PHONE_EDIT_REQUEST = 121;
    private final int USERNAME_EDIT_REQUEST = 122;
    private FirebaseAuth mAuth;
    public ProfileFragment() {
        // Required empty public constructor
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        CardView usernameCardView = (CardView) v.findViewById(R.id.usernameCardView);
        CardView phoneCardView = (CardView) v.findViewById(R.id.phoneCardView);
        CardView emailCardView = (CardView) v.findViewById(R.id.emailCardView);

        email = (TextView) v.findViewById(R.id.email);
        phone = (TextView) v.findViewById(R.id.phone);
        username = (TextView) v.findViewById(R.id.username);

        phoneCardView.setOnClickListener(this);
        usernameCardView.setOnClickListener(this);
        emailCardView.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        Intent editIntent = null;
        if(mAuth != null && mAuth.getCurrentUser() != null){
            switch (v.getId()){
                case R.id.usernameCardView:
                    editIntent = new Intent(mContext, EditorActivity.class);
                    editIntent.putExtra(EditorActivity.EDIT_USERNAME,getValue(username));
                    mContext.startActivityForResult(editIntent,USERNAME_EDIT_REQUEST);
                    break;
                case R.id.phoneCardView:
                    editIntent = new Intent(mContext, EditorActivity.class);
                    editIntent.putExtra(EditorActivity.EDIT_PHONE,getValue(phone));
                    mContext.startActivityForResult(editIntent,PHONE_EDIT_REQUEST);
                    break;
            }
        }
        else
        {
            Toast.makeText(mContext,"Please login start in order to edit your profile",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = (AppCompatActivity) context;


    }
        @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = (AppCompatActivity) getActivity();

    }

    private String getValue(TextView v){
        return v !=null ? v.getText().toString(): "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == USERNAME_EDIT_REQUEST && resultCode == Activity.RESULT_OK){
            if(data != null ){

                if(data.hasExtra(EditorActivity.EDIT_USERNAME)){
                   username.setText(data.getStringExtra(EditorActivity.EDIT_USERNAME));
                }

            }
        }
        else  if(requestCode == PHONE_EDIT_REQUEST && resultCode == Activity.RESULT_OK){
            if(data != null ){

                if(data.hasExtra(EditorActivity.EDIT_PHONE)){
                    phone .setText(data.getStringExtra(EditorActivity.EDIT_PHONE));
                }

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if( mAuth  != null && mAuth.getCurrentUser()!=null){
            if (((Evento) mContext.getApplication()).getSettings().contains(APP_USER_EMAIL)) {
                String str_email = ((Evento) mContext.getApplication()).getSettings().getString(APP_USER_EMAIL, "Email Address");
                email.setText(str_email);
            }

            if (((Evento) mContext.getApplication()).getSettings().contains(APP_USER_PHONE)) {
                String str_phone = ((Evento) mContext.getApplication()).getSettings().getString(APP_USER_PHONE, "Phone Number");
                phone.setText(str_phone.isEmpty() ? "No Phone number":str_phone );
            }

            if (((Evento) mContext.getApplication()).getSettings().contains(APP_USERNAME)) {
                String str_username = ((Evento) mContext.getApplication()).getSettings().getString(APP_USERNAME, "Username");
                username.setText(str_username);
            }
        }


    }
}
