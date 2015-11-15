package com.example.kjpark.smartclass;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by KJPARK on 2015-11-15.
 *
 * @since 0.1
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[];
    int NumOfTabs;

    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumOfTabsum) {
        super(fm);

        this.Titles = mTitles;
        this.NumOfTabs = mNumOfTabsum;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return new NoticeTab();
        else if(position == 1)
            return new AssignmentTab();
        else if(position == 2)
            return new MemoryTab();
        else
            return new MessageTab();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    @Override
    public int getCount() {
        return NumOfTabs;
    }
}
