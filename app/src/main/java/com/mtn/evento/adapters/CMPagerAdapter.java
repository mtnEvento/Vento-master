package com.mtn.evento.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;

import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.fragments.EventsFragment;
import com.mtn.evento.fragments.ProfileFragment;
import com.mtn.evento.fragments.ReservedFragment;

/**
 * Created by DANNY-KAY on 8/4/2017.
 */

public class CMPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    AppCompatActivity context;


    public CMPagerAdapter(AppCompatActivity context, FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EventsFragment tab1 = new EventsFragment();
                tab1.setAppContext(this.context);
                return tab1;
            case 1:
                ReservedFragment tab2 = new ReservedFragment();
                tab2.setAppContext(this.context);
                return tab2;
            case 2:
                ProfileFragment tab3 = new ProfileFragment();
                tab3.setAppContext(this.context);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
