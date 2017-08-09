package com.mtn.evento.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mtn.evento.R;
import com.mtn.evento.data.Constants;
import com.mtn.evento.data.DisplayTicket;
import com.mtn.evento.fragments.BarcodeFragment;

import java.util.ArrayList;
import java.util.List;

public class BarcodeActivity extends AppCompatActivity {

    ViewPager viewpager;
    MyPagerAdapter adapter;

    List<DisplayTicket> mDisplayTickets;
    DisplayTicket displayTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        viewpager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyPagerAdapter(getSupportFragmentManager());

//       pass list of tickets into this activity and set it to mDisplayTickets
        Bundle bundle = getIntent().getBundleExtra(Constants.BUNDLE);

        if(displayTicket != null)
        mDisplayTickets = displayTicket.getDisplayTickets();

        adapter.setBundle(bundle);
        adapter.setDisplayTickets(mDisplayTickets);

        viewpager.setAdapter(adapter);



    }


     class MyPagerAdapter extends FragmentStatePagerAdapter {
        private List<DisplayTicket> displayTickets;
         Bundle bundle;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

         public void setBundle(Bundle bundle) {
             this.bundle = bundle;
         }

         public void setDisplayTickets(List<DisplayTicket> displayTickets) {
            this.displayTickets = displayTickets;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new BarcodeFragment();
//          bundle.putInt(BarcodeFragment.ARG_OBJECT, position + 1);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return displayTickets.size();
        }
    }
}
