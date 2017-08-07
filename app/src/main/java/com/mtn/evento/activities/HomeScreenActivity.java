package com.mtn.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.PopupMenu;
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

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mtn.evento.R;
import com.mtn.evento.adapters.CMPagerAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.fragments.EventsFragment;

import java.util.ArrayList;
import java.util.List;

import static com.mtn.evento.data.Constants.LOGMESSAGE;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener, MaterialSearchBar.OnSearchActionListener {

    private static final int LOGIN_REQUEST = 47;
    public static final String LOGINED_IN = "LOGINED_IN";
    SearchRequestListener searchRequestListener;
    SearchRegionRequestListener regionRequestListener;
    private MaterialSearchBar searchBar;
    MenuItem Loginlogout;
    TextView nav_username, nav_email;
    DrawerLayout mDrawerLayout;
    private de.hdodenhof.circleimageview.CircleImageView  imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        initUI();

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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final CMPagerAdapter adapter = new CMPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        searchRequestListener = (SearchRequestListener) ((EventsFragment)adapter.getItem(0));
        regionRequestListener = (SearchRegionRequestListener) ((EventsFragment)adapter.getItem(0));

//        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
//        searchBar.setHint("Custom hint");
//        searchBar.setSpeechMode(true);
//        //enable searchbar callbacks
//        searchBar.setOnSearchActionListener(this);
//
//
//        //Inflate menu and setup OnMenuItemClickListener
//        searchBar.inflateMenu(R.menu.main_menu);
//        searchBar.getMenu().setOnMenuItemClickListener(this);
//
//        MenuItem item = searchBar.getMenu().getMenu().findItem(R.id.action_spinner);
//        item.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
//
//        if(spinner.getId() == R.id.action_spinner){
//            Log.d(LOGMESSAGE, "spinner found: ");
//        }
//
//        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
//                R.array.regions, android.R.layout.simple_spinner_item);
//        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(spinnerAdapter);
//        Log.d(LOGMESSAGE, "count: " + spinner.getCount());
//        spinner.setOnItemSelectedListener(spinnerItemSelected());
        // searchRequestListener.onSearch(newQuery);
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
                  regionRequestListener.onRegionSearch(regions[position].toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
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
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        Toast.makeText(HomeScreenActivity.this, "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        startSearch(text.toString(), true, null, true);
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
               break;
        }
    }

//    --Region--
//



    public interface  SearchRequestListener{
        public ArrayList<Event> onSearch(String query);
    }

    public interface  SearchRegionRequestListener{
        public ArrayList<Event> onRegionSearch(String query);
    }

}
