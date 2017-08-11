package com.mtn.evento.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.mtn.evento.activities.HomeScreenActivity;
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
    private HomeScreenActivity appContext;
    UserProfile userProfile;

    public ProfileFragment() {
        mAuth = FirebaseAuth.getInstance();
    }

    public void setAppContext(AppCompatActivity appContext){
        this.appContext = (HomeScreenActivity)appContext ;
        if( this.appContext != null){
            this.userProfile = (UserProfile) this.appContext;
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        if(isNetworkAndInternetAvailable()){
            if(mAuth != null && mAuth.getCurrentUser() != null){
                switch (v.getId()){
                    case R.id.usernameCardView:
                        editIntent = new Intent(appContext, EditorActivity.class);
                        editIntent.putExtra(EditorActivity.EDIT_USERNAME,getValue(username));
                        appContext.startActivityForResult(editIntent,USERNAME_EDIT_REQUEST);
                        break;
                    case R.id.phoneCardView:
                        editIntent = new Intent(appContext, EditorActivity.class);
                        editIntent.putExtra(EditorActivity.EDIT_PHONE,getValue(phone));
                        appContext.startActivityForResult(editIntent,PHONE_EDIT_REQUEST);
                        break;
                }
            }
            else
            {
                Toast.makeText(appContext,"Please login start in order to edit your profile",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
          //TODO: Alert no network connection
            Toast.makeText(appContext,"No network connection available. Please check your network and try again!",Toast.LENGTH_LONG).show();
        }
    }
    private String getValue(TextView v){
        return v ==null || v.getText().toString().isEmpty() ? "" : v.getText().toString();
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
    public void onStart() {
        super.onStart();

        if ( mAuth  != null  && mAuth.getCurrentUser() != null) {
            displayUserdetails();
        }
        else
        {
            if( this.userProfile != null){
                this.userProfile.onUserProfileChange(APP_USER_EMAIL,"Email");
                this.userProfile.onUserProfileChange(APP_USERNAME,"Username");
            }

        }
    }
    private void displayUserdetails(){
        if(isNetworkAndInternetAvailable()){
            if( mAuth  != null && mAuth.getCurrentUser()!=null){
                if (((Evento) appContext.getApplication()).getSettings().contains(APP_USER_EMAIL)) {
                    String str_email = ((Evento) appContext.getApplication()).getSettings().getString(APP_USER_EMAIL, "Email");
                    email.setText(str_email);
                    this.userProfile.onUserProfileChange(APP_USER_EMAIL,str_email);
                }

                if (((Evento) appContext.getApplication()).getSettings().contains(APP_USER_PHONE)) {
                    String str_phone = ((Evento) appContext.getApplication()).getSettings().getString(APP_USER_PHONE, "Phone Number");
                    phone.setText(  (str_phone == null || str_phone .isEmpty() )? "No Phone number":str_phone );
                }

                if (((Evento) appContext.getApplication()).getSettings().contains(APP_USERNAME)) {
                    String str_username = ((Evento) appContext.getApplication()).getSettings().getString(APP_USERNAME, "Username");
                    username.setText(str_username);
                    this.userProfile.onUserProfileChange(APP_USERNAME,str_username);
                }
            }
            else
            {
                if( this.userProfile != null){
                    this.userProfile.onUserProfileChange(APP_USER_EMAIL,"Email");
                    this.userProfile.onUserProfileChange(APP_USERNAME,"Username");
                }
            }
        }
        else
        {
            //TODO: Alert no network connection
            Toast.makeText(appContext,"No network connection available. Please check your network and try again!",Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();

        if ( mAuth  != null  && mAuth.getCurrentUser() != null) {
            displayUserdetails();
        }
        else
        {
            if( this.userProfile != null){
                this.userProfile.onUserProfileChange(APP_USER_EMAIL,"Email");
                this.userProfile.onUserProfileChange(APP_USERNAME,"Username");
            }
        }
    }
    public boolean isInternetOn() {
        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getActivity(). getSystemService(getActivity().getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
        {

            return false;
        }
        return false;
    }
    private boolean isNetworkOn(){
        ConnectivityManager ConnectionManager=(ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=ConnectionManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()==true )
        {
            return true;
        }
        else
        {
            return  false;
        }
    }
    private boolean isNetworkAndInternetAvailable(){
        return  isNetworkOn()&& isInternetOn() ;
    }

    public interface UserProfile{
       public void onUserProfileChange(String which,String value);
    }
}
