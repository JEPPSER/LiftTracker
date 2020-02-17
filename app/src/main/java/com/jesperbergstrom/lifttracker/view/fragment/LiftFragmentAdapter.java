package com.jesperbergstrom.lifttracker.view.fragment;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class LiftFragmentAdapter extends FragmentPagerAdapter {

    private Context myContext;
    private int totalTabs;

    public LiftFragmentAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                WorkoutFragment wf = new WorkoutFragment();
                return wf;
            case 1:
                StatsFragment sf = new StatsFragment();
                return sf;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
