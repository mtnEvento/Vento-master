package com.mtn.evento.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.Evento;
import com.mtn.evento.Factory;
import com.mtn.evento.R;
import com.mtn.evento.adapters.CMPagerAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.data.ResultSet;
import com.mtn.evento.fragments.EventsFragment;
import com.mtn.evento.fragments.ProfileFragment;
import com.mtn.evento.fragments.ReservedFragment;

import java.util.ArrayList;
import java.util.List;

import static com.mtn.evento.data.Constants.APP_LOGIN;
import static com.mtn.evento.data.Constants.APP_LOGOUT;
import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_ID;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;
import static com.mtn.evento.data.Constants.LOGINED_IN;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,Factory.UserLogInOrOutListenter,ProfileFragment.UserProfile, Factory.InternetDataListenter, Factory.EventsDataAvailableListener, Factory.ReservedSeatsDataAvailableListener {
    private static final int LOGIN_REQUEST = 47;
    private static final int SIGNUP_REQUEST = 49;
    private Factory mFactory;
    private SearchRequestListener searchRequestListener;
    private SearchRegionRequestListener regionRequestListener;
    private MenuItem Loginlogout;
    private TextView nav_username, nav_email;
    private DrawerLayout mDrawerLayout;
    private de.hdodenhof.circleimageview.CircleImageView  imageView;
    private ActionBarDrawerToggle mDrawerToggle;
    private ImageView customSearchIcon;
    private EditText searchEditText;
    private SearchView searchView;
    private ViewPager viewPager;
    private CMPagerAdapter tabAdapter;
    private Spinner spinner;
    private TabLayout tabLayout;
    private ArrayList<Fragment> mTabs ;
    private EventsFragment tab1;
    private ReservedFragment tab2;
    private ProfileFragment tab3 ;
    private LoginLogoutListener reservedLoginLogoutListener;
    private Factory.InternetDataListenter eventInternetDataListenter;
    private Factory.EventsDataAvailableListener eventsDataAvailableListener;
    private Factory.InternetDataListenter reservedSeatInternetDataListenter;
    private Factory.ReservedSeatsDataAvailableListener reservedSeatsDataAvailableListener;
    private Factory.InternetDataListenter  profileInternetDataListenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){

            // getSupportActionBar().setIcon(R.mipmap.ic_launcher_round);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        }
        mFactory = new Factory(HomeScreenActivity.this);
        initTabs();
        initUI(mTabs);
        initSetting();
    }
    public void initUI(ArrayList<Fragment> mTabs){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.mNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        imageView =  (de.hdodenhof.circleimageview.CircleImageView) headerView. findViewById(R.id.nav_user);
        nav_username = (TextView) headerView. findViewById(R.id.nav_username);
        nav_email = (TextView) headerView. findViewById(R.id.nav_email);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Reserved"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));

        viewPager = (ViewPager) findViewById(R.id.pager);

        tabAdapter  = new CMPagerAdapter(getSupportFragmentManager(),mTabs, mTabs.size());
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() > 0 ){
                    if(spinner!= null ){spinner.setVisibility(View.GONE);}
                }
                else
                {
                    if(spinner!= null ){ spinner.setVisibility(View.VISIBLE);}
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        reservedLoginLogoutListener = (LoginLogoutListener) (tab2);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,android.R.string.ok,android.R.string.cancel);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        viewPager.setAdapter(tabAdapter);
        searchRequestListener = (SearchRequestListener) ((EventsFragment) tab1);
        regionRequestListener = (SearchRegionRequestListener) ((EventsFragment)tab1);

    }
    public void initTabs(){
        mTabs = new ArrayList<>();
        tab1 = new EventsFragment();
        tab1.setAppContext(HomeScreenActivity.this);
        this.eventInternetDataListenter = tab1;
        this.eventsDataAvailableListener = tab1;

        mTabs.add(tab1);

        tab2 = new ReservedFragment();
        tab2.setAppContext(HomeScreenActivity.this);
        this.reservedSeatInternetDataListenter = tab2 ;
        this.reservedSeatsDataAvailableListener = tab2;
        mTabs.add(tab2);

        tab3 = new ProfileFragment();
        tab3.setAppContext(HomeScreenActivity.this);
        this.profileInternetDataListenter = tab3 ;
        mTabs.add(tab3);

    }
    @Override
    protected void onStart() {
        super.onStart();


    }
    private void initSearch(){

        searchView = (SearchView) findViewById(R.id.reserve_seat);
        searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        View mSearchEditFrame = searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);
        ImageView mCollapsedIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView mCloseButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        Drawable mSearchHintIcon = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.ic_search_svg));
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.OPAQUE;
            }
        };

        View mSearchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);;
        final View mSubmitArea  = searchView.findViewById(android.support.v7.appcompat.R.id.submit_area);
        ImageView mSearchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);;
        ImageView mGoButton =(ImageView)searchView.findViewById(android.support.v7.appcompat.R.id.search_go_btn); ;

        mCloseButton.setVisibility(View.GONE);
        mCloseButton.removeOnLayoutChangeListener(null);
        mCloseButton.setOnFocusChangeListener(null);
        mCollapsedIcon.setImageDrawable(mSearchHintIcon);

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreenActivity.this,"close Btn clicked! and mSubmitArea with text : "+(searchEditText).getText().toString(),Toast.LENGTH_LONG).show();
            }
        });
        mGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreenActivity.this,"Go Btn clicked!",Toast.LENGTH_LONG).show();
            }
        });
//        mSearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(HomeScreenActivity.this,"Search Btn clicked! and mSubmitArea with text : "+(searchEditText).getText().toString(),Toast.LENGTH_LONG).show();
//            }
//        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            mCloseButton.setRevealOnFocusHint(false);
        }
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setHintTextColor(Color.LTGRAY);
        searchEditText.setHint("           Search Events by Titles...");

//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
//                customSearchIcon.setVisibility(View.GONE);
//                searchEditText.setHint("       Search Events by Titles...");
//            }
//        });
//
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
//                    customSearchIcon.setVisibility(View.GONE);
//                    searchEditText.setHint("       Search Events by Titles...");
//                }
//                else{
//                    customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
//                    customSearchIcon.setVisibility(View.VISIBLE);
//                    searchEditText.setHint("           Search Events by Titles...");
//                }
//            }
//        });
//
//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(HomeScreenActivity.this,""+ v.getId(),Toast.LENGTH_LONG).show();
//            }
//        });


        // searchView.
    }
    private void initSetting(){

        if( !((Evento)getApplication()).getSettings().contains(APP_USER_ID) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_ID,"").commit();;
        }

        if( !((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USERNAME,"").commit();;
        }

        if( !((Evento)getApplication()).getSettings().contains(APP_USER_PHONE) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_PHONE,"").commit();;
        }
        if( !((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
            ((Evento)getApplication()).getSettings().edit().putString(APP_USER_EMAIL,"").commit();;
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        MenuItem item = menu.findItem(R.id.action_spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.regions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerItemSelected());
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == SIGNUP_REQUEST && resultCode == RESULT_OK )
        {
            boolean signupAndLogined  = data.getBooleanExtra(LoginActivity.SIGNED_UP,false);
            if( FirebaseAuth.getInstance().getCurrentUser() != null){

                if(Loginlogout.getTitle().toString().equals("Logout") && Loginlogout.getIcon().equals(R.drawable.ic_settings_power_black_24dp) ) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                    Loginlogout.setTitle("Logout");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }
                else
                {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                    Loginlogout.setTitle("Logout");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }

            }
            else  if( FirebaseAuth.getInstance().getCurrentUser() == null){


                if(Loginlogout.getTitle().toString().equals("Login") && Loginlogout.getIcon().equals(R.drawable.ic_lock_open_black_24dp) ){
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                }
                else
                {

                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    Loginlogout.setTitle("Login");
                    Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);

                }
            }
        }
        else
        if(requestCode == LOGIN_REQUEST && resultCode == RESULT_OK){
            boolean loginedIn  = data.getBooleanExtra(LOGINED_IN,false);
            if( FirebaseAuth.getInstance().getCurrentUser() != null){

                if(Loginlogout.getTitle().toString().equals("Logout") && Loginlogout.getIcon().equals(R.drawable.ic_settings_power_black_24dp) ) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                    Loginlogout.setTitle("Logout");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }
                else
                {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                    Loginlogout.setTitle("Logout");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }
                
            }
            else  if( FirebaseAuth.getInstance().getCurrentUser() == null){


                if(Loginlogout.getTitle().toString().equals("Login") && Loginlogout.getIcon().equals(R.drawable.ic_lock_open_black_24dp) ){
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);

                }
                else
                {
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    Loginlogout.setTitle("Login");
                    Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                }
            }

        }
    }
    public AdapterView.OnItemSelectedListener spinnerItemSelected(){
        final String [] regions = getResources().getStringArray(R.array.regions);
        return  new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                 regionRequestListener.onRegionSearch(regions[position].toLowerCase(),viewPager);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else
        {
            moveTaskToBack(true);
        }
    }
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        int id = item.getItemId();
        switch (id){

            case R.id.register:
                HomeScreenActivity.this.startActivityForResult(new Intent(HomeScreenActivity.this, SignUpActivity.class),SIGNUP_REQUEST);
                return  true;
            case R.id.action_login_logout:
                Loginlogout = item;

                if ( FirebaseAuth.getInstance().getCurrentUser() == null && Loginlogout != null ) {

                    if(Loginlogout != null && Loginlogout.getTitle().toString().equals("Login") && Loginlogout.getIcon().equals(R.drawable.ic_lock_open_black_24dp) ){
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                    }
                    else
                    {
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        Loginlogout.setTitle("Login");
                        Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                    }
                    if(isNetworkAndInternetAvailable()){
                        HomeScreenActivity.this.startActivityForResult(new Intent(HomeScreenActivity.this, LoginActivity.class),LOGIN_REQUEST);
                    }
                    else
                    {
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        Loginlogout.setTitle("Login");
                        Toast.makeText(HomeScreenActivity.this,"No internet available to perform this task", Toast.LENGTH_LONG).show();

                    }

                }
                else
                if ( FirebaseAuth.getInstance().getCurrentUser() != null) {

                    if(Loginlogout != null && Loginlogout.getTitle().toString().equals("Logout") && Loginlogout.getIcon().equals(R.drawable.ic_settings_power_black_24dp) ) {

                        if(isNetworkAndInternetAvailable()){
                            FirebaseAuth.getInstance().signOut();
                        }
                        else
                        {
                            FirebaseAuth.getInstance().signOut();
                        }
                        Loginlogout.setTitle("login");
                        Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                    }
                    else
                    {
                        if(isNetworkAndInternetAvailable()){
                            FirebaseAuth.getInstance().signOut();
                        }
                        else
                        {
                            FirebaseAuth.getInstance().signOut();
                        }
                        Loginlogout.setTitle("login");
                        Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                    }
                }
               return true;
        }
        return false;
    }
    @Override
    public void onUserProfileChange(String which, String value) {
        if(APP_USER_EMAIL.contentEquals(which)){
            nav_email.setText(value);
        }

        if(APP_USERNAME.contentEquals(which)){
            nav_username.setText(value);
        }

    }

    @Override
    public void onInternetConnected() {

        Log.d(LOGMESSAGE,"HomeScreenActivity onInternetConnected called") ;
        if(mFactory != null){
            mFactory.initFirebase();
            mFactory.startFirebaseEventTask(eventsDataAvailableListener);
            mFactory.runReservedSeatsTasksOnInternetAvailable();
            mFactory.runEventValueTaskOnInternetAvailable(this.eventsDataAvailableListener);
            this.eventInternetDataListenter.onInternetConnected();
            this.reservedSeatInternetDataListenter.onInternetConnected();
            this.profileInternetDataListenter.onInternetConnected();
        }
        else{
            mFactory = new Factory(HomeScreenActivity.this) ;
            mFactory.initFirebase();
            mFactory.startFirebaseEventTask(eventsDataAvailableListener);
            mFactory.runReservedSeatsTasksOnInternetAvailable();
            mFactory.runEventValueTaskOnInternetAvailable(this.eventsDataAvailableListener);
            this.eventInternetDataListenter.onInternetConnected();
            this.reservedSeatInternetDataListenter.onInternetConnected();
            this.profileInternetDataListenter.onInternetConnected();
        }

    }

    @Override
    public void onInternetDisconnected() {
        Log.d(LOGMESSAGE,"HomeScreenActivity onInternetDisconnected called") ;
        if(mFactory != null)
        {
            mFactory.runReservedSeatsTasksOnInternetAvailable();
            this. eventInternetDataListenter.onInternetDisconnected();
            this.reservedSeatInternetDataListenter.onInternetDisconnected();
            this.profileInternetDataListenter.onInternetDisconnected();
        }else
        {
            mFactory = new Factory(HomeScreenActivity.this) ;
            mFactory.runReservedSeatsTasksOnInternetAvailable();
            this. eventInternetDataListenter.onInternetDisconnected();
            this.reservedSeatInternetDataListenter.onInternetDisconnected();
            this.profileInternetDataListenter.onInternetDisconnected();
        }

    }

    @Override
    public void onEventsDataAvailable(int count, ArrayList<Event> reservedResultSets) {
        Log.d(LOGMESSAGE,"HomeScreenActivity onEventsDataAvailable called") ;
       this. eventsDataAvailableListener.onEventsDataAvailable(count,reservedResultSets);
        Toast.makeText(HomeScreenActivity.this,"eventsDataAvailableListener called", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReservedSeatsDataAvailable(int count, ArrayList<ResultSet> reservedResultSets) {

        Log.d(LOGMESSAGE,"HomeScreenActivity onReservedSeatsDataAvailable called") ;
        this.reservedSeatsDataAvailableListener.onReservedSeatsDataAvailable(count,reservedResultSets);
    }

    @Override
    public void onUserSignedIn() {

       runOnUiThread(new Runnable() {
           @Override
           public void run() {

               if( FirebaseAuth.getInstance().getCurrentUser() != null && Loginlogout != null && nav_username != null &&  nav_email != null){

                   if( Loginlogout != null && Loginlogout.getTitle().toString().equals("Logout") && Loginlogout.getIcon().equals(R.drawable.ic_settings_power_black_24dp) ) {

                       String email = "Email";String username= "Username";
                       if( ((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
                           email = ((Evento)getApplication()).getSettings().getString(APP_USER_EMAIL,email);
                       }

                       if( ((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
                           username   = ((Evento)getApplication()).getSettings().getString(APP_USERNAME,username);
                       }
                       nav_username.setText(username);
                       nav_email.setText(email);
                       Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                       Loginlogout.setTitle("Logout");
                   }
                   else
                   {
                       String email = "Email";String username= "Username";
                       if( ((Evento)getApplication()).getSettings().contains(APP_USER_EMAIL) ){
                           email = ((Evento)getApplication()).getSettings().getString(APP_USER_EMAIL,email);
                       }

                       if( ((Evento)getApplication()).getSettings().contains(APP_USERNAME) ){
                           username   = ((Evento)getApplication()).getSettings().getString(APP_USERNAME,username);
                       }
                       nav_username.setText(username);
                       nav_email.setText(email);
                       Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                       Loginlogout.setTitle("Logout");
                   }
                   reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
               }
           }
       });

    }

    @Override
    public void onUserSignedOut() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ( FirebaseAuth.getInstance().getCurrentUser() == null && Loginlogout != null && nav_username != null &&  nav_email != null) {

                    if(Loginlogout.getTitle().toString().equals("Login") && Loginlogout.getIcon().equals(R.drawable.ic_lock_open_black_24dp) ){
                        if(Loginlogout.getActionView() != null){}
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);;
                    }
                    else
                    {
                        if(Loginlogout.getActionView() != null){}
                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        Loginlogout.setTitle("Login");
                        Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                    }
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                }
            }
        });

    }

    public interface  SearchRequestListener{
        public ArrayList<Event> onSearch(String query);
    }
    public interface  SearchRegionRequestListener{
        public ArrayList<Event> onRegionSearch(String query, ViewPager  vp);
    }
    public interface  LoginLogoutListener{
        public boolean onLoginLogout(String which);
    }
    public boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED)
        {

            return false;
        }
        return false;
    }
    private boolean isNetworkOn(){
        ConnectivityManager ConnectionManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
