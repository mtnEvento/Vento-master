package com.mtn.evento.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mtn.evento.activities.HomeScreenActivity;
import com.mtn.evento.fragments.EventsFragment;
import com.mtn.evento.fragments.ProfileFragment;
import com.mtn.evento.fragments.ReservedFragment;

/**
 * Created by DANNY-KAY on 8/4/2017.
 */

public class CMPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;


    public CMPagerAdapter( FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                EventsFragment tab1 = new EventsFragment();
                return tab1;
            case 1:
                ReservedFragment tab2 = new ReservedFragment();
                return tab2;
            case 2:
                ProfileFragment tab3 = new ProfileFragment();
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
