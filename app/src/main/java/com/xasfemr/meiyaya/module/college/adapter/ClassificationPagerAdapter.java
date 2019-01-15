package com.xasfemr.meiyaya.module.college.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;



public class ClassificationPagerAdapter extends FragmentStatePagerAdapter {

    private final String[]mTitles={"课程","讲师","资料","活动"};
    private List<Fragment> listragment;
    private String status;

    public ClassificationPagerAdapter(FragmentManager fm, List<Fragment> list_fragment) {
        super(fm);
        this.listragment= list_fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return listragment.get(position);

    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
