package com.mtn.evento.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.fragments.BarcodeFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BarcodeActivity extends AppCompatActivity {

    ViewPager viewpager;
    MyPagerAdapter adapter;
    Toolbar toolbar;

    List<DisplayTicket> mDisplayTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        viewpager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

//       pass list of tickets into this activity and set it to mDisplayTickets

        mDisplayTickets = (List<DisplayTicket>) getIntent().getSerializableExtra(Constants.TICKET);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        adapter.setDisplayTickets(mDisplayTickets);

        viewpager.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<DisplayTicket> displayTickets;


         MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

          void setDisplayTickets(List<DisplayTicket> displayTickets) {
            this.displayTickets = displayTickets;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new BarcodeFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.TICKET, displayTickets.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return displayTickets.size();
        }
    }
}
