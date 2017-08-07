package com.mtn.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.firebase.auth.FirebaseAuth;
import com.mtn.evento.R;
import com.mtn.evento.adapters.CMPagerAdapter;
import com.mtn.evento.data.Event;
import com.mtn.evento.fragments.EventsFragment;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    SearchRequestListener searchRequestListener;
    SearchRegionRequestListener regionRequestListener;
    com.arlib.floatingsearchview.FloatingSearchView searchEdit;
    static MenuItem Loginlogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        searchEdit = (com.arlib.floatingsearchview.FloatingSearchView) findViewById(R.id.searchEdit);
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

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
        searchEdit.attachNavigationDrawerToMenuButton(mDrawerLayout);

        searchEdit.setOnHomeActionClickListener(
                new FloatingSearchView.OnHomeActionClickListener() {
                    @Override
                    public void onHomeClicked() {
                        mDrawerLayout.closeDrawer(Gravity.START);
                    }
                });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.action_spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.regions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(spinnerItemSelected());

        searchEdit.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
               // mSearchView.swapSuggestions(newSuggestions);
                searchRequestListener.onSearch(newQuery);
            }
        });
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_login_logout:
                CharSequence action_text = item.getTitle();
                if(action_text.toString().contains("Login")){

                    if ( FirebaseAuth.getInstance().getCurrentUser() == null) {
                         startActivity(new Intent(this, LoginActivity.class));

                        item.setTitle("Logout");
                    }else{
                        startActivity(new Intent(this, LoginActivity.class));

                        item.setTitle("Logout");
                    }
                }
                else
                if(action_text.toString().contains("Logout")){
                    if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
                        FirebaseAuth.getInstance().signOut();
                        item.setTitle("Login");
                    }

                }

                return true;
        }

        return super.onOptionsItemSelected(item);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_login_logout:
            CharSequence action_text = item.getTitle();
            if(action_text.toString().contains("Login")){

                if ( FirebaseAuth.getInstance().getCurrentUser() == null) {
                    startActivity(new Intent(this, LoginActivity.class));

                    item.setTitle("Logout");
                }else{
                    startActivity(new Intent(this, LoginActivity.class));

                    item.setTitle("Logout");
                }
            }
            else
            if(action_text.toString().contains("Logout")){
                if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    item.setTitle("Login");
                }

            }
            return true;

        }
        return false;
    }

//    --Region--
//



    public interface  SearchRequestListener{
        public ArrayList<Event> onSearch(String query);
    }

    public interface  SearchRegionRequestListener{
        public ArrayList<Event> onRegionSearch(String query);
    }

    public static void doAction(Context c, String action_text){

        if(action_text.toString().contains("Login")){

            if ( FirebaseAuth.getInstance().getCurrentUser() == null) {
                c.startActivity(new Intent(c, LoginActivity.class));

                Loginlogout.setTitle("Logout");
            }else{
                c.startActivity(new Intent(c, LoginActivity.class));
                Loginlogout.setTitle("Logout");
               // Loginlogout.setIcon()
            }
        }
        else
        if(action_text.toString().contains("Logout")){
            if ( FirebaseAuth.getInstance().getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Loginlogout.setTitle("Login");
               // Loginlogout.setIcon()
            }

        }

    }
}
