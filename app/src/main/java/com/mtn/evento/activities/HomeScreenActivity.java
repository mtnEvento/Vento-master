package com.mtn.evento.activities;

import android.app.SearchManager;
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
import com.mtn.evento.AboutUsActivity;
import com.mtn.evento.Evento;
import com.mtn.evento.Factory;
import com.mtn.evento.MobileMoneyActivity;
import com.mtn.evento.R;
import com.mtn.evento.adapters.CMPagerAdapter;
import com.mtn.evento.data.Constants;
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

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, Factory.UserLogInOrOutListenter, ProfileFragment.UserProfile, Factory.InternetDataListenter {
    private static final int LOGIN_REQUEST = 47;
    private static final int SIGNUP_REQUEST = 49;
    public static ArrayList<Event> events, cacheEvent;
    private Factory mFactory;
    private ArrayList<SearchRequestListener> searchRequestListeners;
    private ArrayList<SearchRegionRequestListener> regionRequestListeners;
    private MenuItem Login, logout;
    private TextView nav_username, nav_email;
    private DrawerLayout mDrawerLayout;
    private de.hdodenhof.circleimageview.CircleImageView  imageView;
    private ActionBarDrawerToggle mDrawerToggle;
    private SearchView searchView;
    private ViewPager viewPager;
    private CMPagerAdapter tabAdapter;
    private Spinner spinner;
    private TabLayout tabLayout;
    private  ArrayList<Fragment> mTabs ;
    private EventsFragment tab1;
    private ReservedFragment tab2;
    private ProfileFragment tab3 ;
    private LoginLogoutListener reservedLoginLogoutListener;
    private Factory.InternetDataListenter eventInternetDataListenter;
    private Factory.InternetDataListenter  profileInternetDataListenter;
    private  MenuItem itemSpinner;

    public static boolean isSearching = false;
    private int selectedPage= 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        initToolbar();
        initTabs();
        initUI(mTabs);
        initSetting();
        initFactory();

    }
    private void initFactory(){
        mFactory = new Factory(HomeScreenActivity.this);
    }

    public ViewPager getViewPager() {
        return viewPager;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public SearchView getSearchView() {
        return searchView;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        itemSpinner = menu.findItem(R.id.action_spinner);
        itemSpinner.setVisible(false);
        spinner = (Spinner) MenuItemCompat.getActionView(itemSpinner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spinner.setPopupBackgroundResource(R.color.colorAccent);
        }
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.regions, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(spinnerItemSelected());

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        if(viewPager.getCurrentItem() == 0){
            Fragment fragment = mTabs.get(viewPager.getCurrentItem());
            if(fragment instanceof  EventsFragment){
                if(itemSpinner != null ){
                    itemSpinner.setVisible(true);
                }
            }
            else
            {
                if(itemSpinner != null ){
                    itemSpinner.setVisible(false);
                }
            }
        }
        ImageView mCloseButton = (ImageView) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchSrcTextView.setText("");

        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearFocus();
                v.setSelected(false);
                searchView.setIconified(false);
                SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text);
                mSearchSrcTextView.setText("");
                searchView.onActionViewCollapsed();

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(searchRequestListeners!= null){
                    if(searchRequestListeners.size() > 0 )
                    {
                        for ( SearchRequestListener searchRequestListener  :searchRequestListeners )
                        {
                            searchRequestListener.onSearch(newText.toLowerCase(),selectedPage);
                        }

                    }
                    else
                    {
                        for ( SearchRequestListener searchRequestListener  :searchRequestListeners )
                        {
                            searchRequestListener.onSearchQueryEmpty(selectedPage);
                        }
                    }
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGNUP_REQUEST && resultCode == RESULT_OK) {
            boolean signupAndLogined = data.getBooleanExtra(LoginActivity.SIGNED_UP, false);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                if (Login != null && logout != null && signupAndLogined) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Login.setVisible(false);
                    logout.setVisible(true);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }
                reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                if (Login != null && logout != null) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Login.setVisible(true);
                    logout.setVisible(false);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                }
            }
        } else if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK) {
            boolean loginedIn = data.getBooleanExtra(LOGINED_IN, false);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                if (Login != null && logout != null && loginedIn) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Login.setVisible(false);
                    logout.setVisible(true);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                }
                reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
            } else if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                if (Login != null && logout != null) {
                    nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                    nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                    Login.setVisible(true);
                    logout.setVisible(false);
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                }
                reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
            }

        }
    }
    public AdapterView.OnItemSelectedListener spinnerItemSelected() {
        final String[] regions = getResources().getStringArray(R.array.regions);
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(regionRequestListeners != null){
                    if(regionRequestListeners.size() > 0 )
                    {
                        for ( SearchRegionRequestListener searchRegionRequestListener :regionRequestListeners )
                        {
                            searchRegionRequestListener.onRegionSearch(regions[position].toLowerCase());
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (mFactory != null)
        {
            mFactory.runNetworkTask();
            mFactory.runLoginLogoutTask();
        }
        else
        {
            mFactory = new Factory(HomeScreenActivity.this);
            if (mFactory != null)
            {
                mFactory.runNetworkTask();
                mFactory.runLoginLogoutTask();
            }
        }

        if(  getSearchView() != null )
        {
            getSearchView().clearFocus();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mFactory != null) {
            mFactory.stopNetworkTask();
            mFactory.stopLoginLogoutTask();
            mFactory.cancelReservedSeatsTasksOnInternetUnAvailable();
        }
        if(  getSearchView() != null )
        {
            getSearchView().setIconified(false);
            getSearchView().setSelected(false);
            ImageView mCloseButton = (ImageView) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text);
            mSearchSrcTextView.setText("");
            getSearchView().onActionViewCollapsed();
           // mCloseButton.performClick();
        }

    }
    @Override
    protected void onStop() {
        if (mFactory != null) {
            mFactory = null;
        }
        super.onStop();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.register:
                HomeScreenActivity.this.startActivityForResult(new Intent(HomeScreenActivity.this, SignUpActivity.class), SIGNUP_REQUEST);
                return true;
            case R.id.action_logout:
                logout = item;
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                } else {
                    if (isNetworkAndInternetAvailable()) {
                        FirebaseAuth.getInstance().signOut();
                    } else {
                        FirebaseAuth.getInstance().signOut();
                    }
                    nav_username.setText("Username");
                    nav_email.setText("Email");
                    reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                }

                return true;
            case R.id.action_login:
                Login = item;
                nav_username.setText("Username");
                nav_email.setText("Email");
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {

                    if (isNetworkAndInternetAvailable()) {

                    } else {
                        Toast.makeText(HomeScreenActivity.this, "No internet available to perform this task", Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            case R.id.action_account_settings:
                handleAccountSettings();
                return true;
            case R.id.nav_about_us:
                handleAboutUs();
                return true;
            case R.id.nav_share_app_link:
                handleShareAppLink();
                return true;


        }
        return false;
    }

    private void handleShareAppLink() {

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT,"Hello friends, check out 'MTN Evento' which happens to be an Events App that allows you to purchase/reserve tickets of your favorite events, with just some few click from anywhere and at anytime of your convenience.  MTN Event coming soon on MTN Apps store.");
            startActivity(intent);
    }

    private void handleAboutUs() {
        Intent aboutUsIntent = new Intent(this, AboutUsActivity.class);
        startActivity(aboutUsIntent);

    }

    private void handleAccountSettings() {

        Intent mobileMoneyIntent = new Intent(this, MobileMoneyActivity.class);
        startActivity(mobileMoneyIntent);

    }

    @Override
    public void onUserProfileChange(final String which, final String value) {
        Log.d(LOGMESSAGE, "HomeScreenActivity onUserProfileChange called");
        nav_email.post(new Runnable() {
            @Override
            public void run() {
                if (APP_USER_EMAIL.contentEquals(which)) {
                    nav_email.setText(value);
                }
            }
        });
        nav_username.post(new Runnable() {
            @Override
            public void run() {
                if (APP_USERNAME.contentEquals(which)) {
                    nav_username.setText(value);
                }
            }
        });
    }
    @Override
    public void onInternetConnected() {
        if (mFactory != null) {
            if (HomeScreenActivity.this.eventInternetDataListenter != null) {
                HomeScreenActivity.this.eventInternetDataListenter.onInternetConnected();
            }
            if (HomeScreenActivity.this.profileInternetDataListenter != null) {
                HomeScreenActivity.this.profileInternetDataListenter.onInternetConnected();
            }
        }
    }
    @Override
    public void onInternetDisconnected() {
        if (mFactory != null) {
            if (HomeScreenActivity.this.eventInternetDataListenter != null) {
                HomeScreenActivity.this.eventInternetDataListenter.onInternetDisconnected();
            }
            if (HomeScreenActivity.this.profileInternetDataListenter != null) {
                HomeScreenActivity.this.profileInternetDataListenter.onInternetDisconnected();
            }
        }
    }
    @Override
    public void onUserSignedIn() {
        Log.d(LOGMESSAGE, "HomeScreenActivity onUserSignedIn called");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (FirebaseAuth.getInstance().getCurrentUser() != null && logout != null && Login != null && nav_username != null && nav_email != null) {

                    if (logout != null && Login != null) {

                        String email = "Email";
                        String username = "Username";
                        if (((Evento) getApplication()).getSettings().contains(APP_USER_EMAIL)) {
                            email = ((Evento) getApplication()).getSettings().getString(APP_USER_EMAIL, email);
                        }

                        if (((Evento) getApplication()).getSettings().contains(APP_USERNAME)) {
                            username = ((Evento) getApplication()).getSettings().getString(APP_USERNAME, username);
                        }
                        nav_username.setText(username);
                        nav_email.setText(email);
                        logout.setVisible(true);
                        Login.setVisible(false);
                        reservedLoginLogoutListener.onLoginLogout(APP_LOGIN);
                    }
                }
            }
        });

    }
    @Override
    public void onUserSignedOut() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null && logout != null && Login != null && nav_username != null && nav_email != null) {

                    if (logout != null && Login != null) {

                        nav_username.setText("Username");
                        nav_email.setText("Email");
                        logout.setVisible(false);
                        Login.setVisible(true);
                        reservedLoginLogoutListener.onLoginLogout(APP_LOGOUT);
                    }
                }
            }
        });

    }
    public void initUI(final ArrayList<Fragment> mTabs){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.mNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.post(new Runnable() {
            @Override
            public void run() {
                navigationView.setBackgroundResource(R.color.colorBackground);
            }
        });
        final View headerView = navigationView.getHeaderView(0);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerLayout.setBackgroundResource(R.color.colorBackground);
            }
        });
        imageView =  (de.hdodenhof.circleimageview.CircleImageView) headerView. findViewById(R.id.nav_user);
        nav_username = (TextView) headerView. findViewById(R.id.nav_username);
        nav_email = (TextView) headerView. findViewById(R.id.nav_email);

        final Menu menu = navigationView.getMenu();
        Login = menu.findItem(R.id.action_login);
        logout = menu.findItem(R.id.action_logout);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Reserved"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));


        viewPager = (ViewPager) findViewById(R.id.pager);

        tabAdapter  = new CMPagerAdapter(getSupportFragmentManager(),mTabs,3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                selectedPage = position ;
                Fragment fragment = mTabs.get(position);

                if(fragment instanceof EventsFragment){
                    ((EventsFragment) fragment).onRefresh();

                    if(itemSpinner != null ){
                        itemSpinner.setVisible(true);
                    }
                    if(spinner!= null ){ spinner.setVisibility(View.VISIBLE);}
                    ((EventsFragment) fragment).onRegionSearch("--region--");
                }
                else
                {
                    if(spinner!= null ){spinner.setVisibility(View.GONE);}
                    if(itemSpinner != null ){
                        itemSpinner.setVisible(false);
                    }
                }

                if(fragment instanceof ReservedFragment)
                {
                    ((ReservedFragment) fragment).onRefresh();
                }
                else
                {
                    //TODO: nothing here
                }

                if(fragment instanceof ProfileFragment)
                {
                    ((ProfileFragment) fragment).onRefresh();
                    if(getSearchView()!= null ){
                        getSearchView().setVisibility(View.GONE);
                        SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text);
                        mSearchSrcTextView.setText("");
                        getSearchView().onActionViewCollapsed();

                    }
                }
                else
               {
                   if(getSearchView()!= null ){
                       getSearchView().setVisibility(View.VISIBLE);
                       SearchView.SearchAutoComplete mSearchSrcTextView = (SearchView.SearchAutoComplete) getSearchView().findViewById(android.support.v7.appcompat.R.id.search_src_text);
                       mSearchSrcTextView.setText("");
                       getSearchView().onActionViewCollapsed();
                   }
               }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                selectedPage = tab.getPosition();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        reservedLoginLogoutListener = (tab2);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,android.R.string.ok,android.R.string.cancel);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        viewPager.setAdapter(tabAdapter);
        searchRequestListeners.add(tab1);
        searchRequestListeners.add(tab2);

        regionRequestListeners.add(tab1) ;
       // regionRequestListeners.add(tab2)) ;
    }
    public void initTabs(){
        if (events == null) {
            events = new ArrayList<>();
        }
        if (cacheEvent == null) {
            cacheEvent = new ArrayList<>();
        }

        searchRequestListeners = new ArrayList<>();
        regionRequestListeners = new ArrayList<>();
        mTabs = new ArrayList<>();
        tab1 = new EventsFragment();
        tab1.setAppContext(HomeScreenActivity.this);
        this.eventInternetDataListenter = tab1;
        mTabs.add(tab1);

        tab2 = new ReservedFragment();
        tab2.setAppContext(HomeScreenActivity.this);
        mTabs.add(tab2);

        tab3 = new ProfileFragment();
        tab3.setAppContext(HomeScreenActivity.this);
        this.profileInternetDataListenter = tab3 ;
        mTabs.add(tab3);
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
    public boolean isInternetOn() {

        ConnectivityManager connec =(ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
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
    public interface SearchRequestListener {
        public void onSearch(String query,int adapterPosition);
        public void onSearchQueryEmpty(int adapterPosition);

    }
    public interface SearchRegionRequestListener {
        public void onRegionSearch(String query);
    }
    public interface LoginLogoutListener {
        public boolean onLoginLogout(String which);
    }

}
