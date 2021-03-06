package com.example.abhinav.chatapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by abhinav on 11/1/18.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {


    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return new ChatFragment();
            case 1:
                return new UserProfileActivity();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
