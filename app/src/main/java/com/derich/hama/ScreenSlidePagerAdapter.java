package com.derich.hama;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
    public List<HousePics> mFragments;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<HousePics> fragments) {
        super(fm);
        mFragments = fragments;
    }
    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new HousePicsFragment(mFragments.get(position).getPic());
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
