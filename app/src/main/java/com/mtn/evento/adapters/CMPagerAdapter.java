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

import java.util.ArrayList;

/**
 * Created by DANNY-KAY on 8/4/2017.
 */

public class CMPagerAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    AppCompatActivity context;
    ArrayList<Fragment> mTabs;

    public CMPagerAdapter( FragmentManager fm, ArrayList<Fragment> mTabs, int NumOfTabs) {

        super(fm);
        this.mTabs = mTabs ;
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:

                return this.mTabs.get(position);
            case 1:

               return this.mTabs.get(position);
            case 2:
                return this.mTabs.get(position);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

}
