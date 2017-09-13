package com.mtn.evento.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mtn.evento.Evento;
import com.mtn.evento.Factory;
import com.mtn.evento.R;
import com.mtn.evento.activities.EditorActivity;
import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.activities.SignUpActivity;
import com.mtn.evento.data.SinglePurchaseData;

import java.util.ArrayList;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener,Factory.InternetDataListenter,  SwipeRefreshLayout.OnRefreshListener  {
    private final int EMAIL_EDIT_REQUEST = 120;
    private final int PHONE_EDIT_REQUEST = 121;
    private final int USERNAME_EDIT_REQUEST = 122;
    AppCompatActivity mContext;
    TextView email, phone, username;
    UserProfile userProfile;
    private HomeScreenActivity appContext;
    private DatabaseReference mDatabase;
    private volatile boolean hasInternet= false;
    private static SwipeRefreshLayout refreshLayout;

    public ProfileFragment() {   mDatabase = FirebaseDatabase.getInstance().getReference();}

    public void setAppContext(HomeScreenActivity appContext){

        if( this.appContext == null){
            this.appContext = appContext ;
            if( this.appContext != null)
            {
                this.userProfile = this.appContext;
                if( this.appContext.getSearchView() != null){
                    this.appContext.getSearchView().setVisibility(View.GONE);
                }

            }
            else
            {
                this.appContext = (HomeScreenActivity) getContext() ;
                if( this.appContext != null)
                {
                    this.userProfile = this.appContext;
                    if( this.appContext.getSearchView() != null){
                        this.appContext.getSearchView().setVisibility(View.GONE);
                    }
                }
            }
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile, container, false);
        refreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.refresher);
        refreshLayout.setOnRefreshListener(this);
        CardView phoneCardView = (CardView) v.findViewById(R.id.phoneCardView);
        email = (TextView) v.findViewById(R.id.email);
        phone = (TextView) v.findViewById(R.id.phone);
        username = (TextView) v.findViewById(R.id.username);
        phoneCardView.setOnClickListener(this);
        return v;
    }
    @Override
    public void onClick(View v) {
        Intent editIntent = null;

        if(FirebaseAuth.getInstance().getCurrentUser() != null){

                if(hasInternet)
                {
                    switch (v.getId())
                    {
                        case R.id.phoneCardView:
                            createUserInputResquest(getContext(),false);
                            break;
                    }
                }
                else
                {
                    Toast.makeText(getContext(),"No network connection available. Please check your network and try again!",Toast.LENGTH_LONG).show();
                }
        }
        else
        {
          //TODO: Alert no network connection
            Toast.makeText(getContext(), "Please login first in order to edit your profile", Toast.LENGTH_LONG).show();
        }
    }
    private String getValue(TextView v){
        return v ==null || v.getText()== null ||  TextUtils.isEmpty(v.getText().toString()) ? "" : v.getText().toString();
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

        if ( FirebaseAuth.getInstance().getCurrentUser()  != null) {
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
    private void displayUserdetails()
    {
            if( FirebaseAuth.getInstance().getCurrentUser() != null ){
                if ( appContext != null && appContext.getApplication() !=null &&((Evento) appContext.getApplication()).getSettings().contains(APP_USER_EMAIL)) {
                    String str_email = ((Evento) appContext.getApplication()).getSettings().getString(APP_USER_EMAIL, "Email");
                    email.setText(str_email);
                    this.userProfile.onUserProfileChange(APP_USER_EMAIL,str_email);
                }

                if (appContext != null && appContext.getApplication() !=null && ((Evento) appContext.getApplication()).getSettings().contains(APP_USER_PHONE)) {
                    String str_phone = ((Evento) appContext.getApplication()).getSettings().getString(APP_USER_PHONE, "Phone Number");
                    phone.setText(  (str_phone == null || str_phone .isEmpty() )? "No Phone number" : str_phone );
                }

                if (appContext != null && appContext.getApplication() !=null && ((Evento) appContext.getApplication()).getSettings().contains(APP_USERNAME)) {
                    String str_username = ((Evento) appContext.getApplication()).getSettings().getString(APP_USERNAME, "Username");
                    if(TextUtils.isEmpty(str_username)){
                        str_username = "Username Not Available";
                    }
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
    @Override
    public void onResume() {

        super.onResume();
        if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
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

    @Override
    public void onInternetConnected() {
        hasInternet = true ;
    }

    @Override
    public void onInternetDisconnected() {
        hasInternet = false ;
    }

    @Override
    public void onRefresh() {
        if(appContext != null ){
            appContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    displayUserdetails();
                    refreshLayout.setRefreshing(false);
                }
            });
        }
        else
        {
            //TODO:
            Toast.makeText(getContext(),"Could not refresh profile.",Toast.LENGTH_LONG).show();
        }
    }

    public interface UserProfile{
       public void onUserProfileChange(String which,String value);
    }
    private void createUserInputResquest(final Context context,boolean recreate){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(mView);


        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.setHint("e.g 0274320517");
        final TextView userInputDialogTitle = (TextView) mView.findViewById(R.id.dialogTitle);
        userInputDialogTitle.setText("ENTER MOBILE NUMBER");
        if(recreate){
            userInputDialogEditText.setError("invalid number");
        }
        userInputDialogEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here

                        if(userInputDialogEditText != null && userInputDialogEditText.getText() != null && !userInputDialogEditText.getText().toString().isEmpty() && userInputDialogEditText.getText().toString().length() >= 10){
                            userInputDialogEditText.clearFocus();

                            appContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                            updateUserInfo(""+FirebaseAuth.getInstance().getCurrentUser().getUid(),"phone",userInputDialogEditText.getText().toString());

                        }
                        else
                        {
                            userInputDialogEditText.setError("invalid number");
                            createUserInputResquest(context, true);
                            Toast.makeText(context,"invalid number",Toast.LENGTH_LONG).show();
                        }

                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        userInputDialogEditText.setError("invalid number");
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    private void updateUserInfo(final String userId,final String edit,final String editValue) {

        final ProgressDialog progressDialog =  new ProgressDialog(getContext());
        progressDialog.setMessage("updating ...");
        progressDialog.show();


            mDatabase.child("users").child(userId).child(edit).setValue(editValue, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if( !((Evento) appContext.getApplication()).getSettings().contains(APP_USER_PHONE) ){
                        ((Evento) appContext.getApplication()).getSettings().edit().putString(APP_USER_PHONE,editValue).commit();;
                    }
                    else
                    {
                        ((Evento)  appContext.getApplication()).getSettings().edit().putString(APP_USER_PHONE,editValue).commit();;
                    }
                    phone.setText(editValue);
                    progressDialog.cancel();
                }
            });


    }
}
