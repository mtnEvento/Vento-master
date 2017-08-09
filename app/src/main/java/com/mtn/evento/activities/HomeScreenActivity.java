package com.mtn.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.mtn.evento.R;
import com.mtn.evento.adapters.CMPagerAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.fragments.EventsFragment;

import java.util.ArrayList;
import java.util.List;

import static com.mtn.evento.data.Constants.APP_USERNAME;
import static com.mtn.evento.data.Constants.APP_USER_EMAIL;
import static com.mtn.evento.data.Constants.APP_USER_ID;
import static com.mtn.evento.data.Constants.APP_USER_PHONE;
import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnCloseListener,SearchView.OnClickListener {

    private static final int LOGIN_REQUEST = 47;
    public static final String LOGINED_IN = "LOGINED_IN";
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
    CMPagerAdapter tabAdapter;
    Spinner spinner;

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
        initUI();
        initSetting();
        initSearch();
    }

    public void initUI(){

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        NavigationView navigationView = (NavigationView) findViewById(R.id.mNavigationView);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        imageView =  (de.hdodenhof.circleimageview.CircleImageView) headerView. findViewById(R.id.nav_user);
        nav_username = (TextView) headerView. findViewById(R.id.nav_username);
        nav_email = (TextView) headerView. findViewById(R.id.nav_email);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Events"));
        tabLayout.addTab(tabLayout.newTab().setText("Reserved"));
        tabLayout.addTab(tabLayout.newTab().setText("Profile"));

        viewPager = (ViewPager) findViewById(R.id.pager);
        tabAdapter  = new CMPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(tabAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if(tab.getPosition() > 0 ){
                    if(spinner!= null ){
                        spinner.setVisibility(View.GONE);
                    }

                }
                else
                {
                    if(spinner!= null ){
                        spinner.setVisibility(View.VISIBLE);
                    }

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        searchRequestListener = (SearchRequestListener) ((EventsFragment)tabAdapter.getItem(0));
        regionRequestListener = (SearchRegionRequestListener) ((EventsFragment)tabAdapter.getItem(0));
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,android.R.string.ok,android.R.string.cancel);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

    }

    private void initSearch(){
        searchView = (SearchView) findViewById(R.id.eventSearchView);
        searchEditText = (EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView closeButtonImage = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        closeButtonImage.setVisibility(View.GONE);
        closeButtonImage.removeOnLayoutChangeListener(null);
        closeButtonImage.setOnFocusChangeListener(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            closeButtonImage.setRevealOnFocusHint(false);
        }
        searchEditText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        searchEditText.setHintTextColor(Color.LTGRAY);
        searchEditText.setHint("           Search Events by Titles...");
        ImageView mSearchHintIcon = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
                customSearchIcon.setVisibility(View.GONE);
                searchEditText.setHint("       Search Events by Titles...");
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
                    customSearchIcon.setVisibility(View.GONE);
                    searchEditText.setHint("       Search Events by Titles...");
                }
                else{
                    customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
                    customSearchIcon.setVisibility(View.VISIBLE);
                    searchEditText.setHint("           Search Events by Titles...");
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeScreenActivity.this,""+ v.getId(),Toast.LENGTH_LONG).show();
            }
        });


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
        Log.d(LOGMESSAGE, "count: " + spinner.getCount());
        spinner.setOnItemSelectedListener(spinnerItemSelected());
        // searchRequestListener.onSearch(newQuery);
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
        if(requestCode == LOGIN_REQUEST && resultCode == RESULT_OK){

            boolean loginedIn  = data.getBooleanExtra(LOGINED_IN,false);
            if(loginedIn){

                nav_username.setText(data.getStringExtra(LoginActivity.USERNAME));
                nav_email.setText(data.getStringExtra(LoginActivity.EMAIL));
                Loginlogout.setIcon(R.drawable.ic_settings_power_black_24dp);
                Loginlogout.setTitle("Logout");
            }
            else{
                nav_username.setText("Username");
                nav_email.setText("Email");
                Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
                Loginlogout.setTitle("Login");

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

        if(searchEditText.getText().toString().isEmpty()){
            customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
            customSearchIcon.setVisibility(View.VISIBLE);
        }
        else {
            customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
            customSearchIcon.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();

        }
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_login_logout:
                Loginlogout = item;
                CharSequence action_text = item.getTitle();
                if(action_text.toString().contains("Login")){

                    if ( FirebaseAuth.getInstance().getCurrentUser() == null) {
                         startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST);
                    }
                    else{
                        startActivityForResult(new Intent(this, LoginActivity.class),LOGIN_REQUEST);
                    }
                }
                else
                if(action_text.toString().contains("Logout")){
                    if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
                          FirebaseAuth.getInstance().signOut();
                          Loginlogout.setTitle("Login");
                          Loginlogout.setIcon(R.drawable.ic_lock_open_black_24dp);
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
                }
            return true;

        }
        return false;
    }

    @Override
    public boolean onClose() {

        if(searchEditText.getText().toString().isEmpty()){
            customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
            customSearchIcon.setVisibility(View.VISIBLE);
        }
        else {
            customSearchIcon = (ImageView) findViewById(R.id.customSearchIcon);
            customSearchIcon.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== android.support.v7.appcompat.R.id.search_close_btn){
            searchEditText.setText("         ");
        }
    }

    public interface  SearchRequestListener{
        public ArrayList<Event> onSearch(String query);
    }

    public interface  SearchRegionRequestListener{
        public ArrayList<Event> onRegionSearch(String query, ViewPager  vp);
    }



}
