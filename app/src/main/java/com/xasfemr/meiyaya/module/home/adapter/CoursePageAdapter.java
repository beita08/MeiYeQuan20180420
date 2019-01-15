package com.xasfemr.meiyaya.module.home.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/11/29.
 */

public class CoursePageAdapter extends FragmentPagerAdapter{


    private final String[]mTitles={"在线课程","精彩回放"};
    private List<Fragment> listragment;


    public CoursePageAdapter(FragmentManager fm,List<Fragment> list_fragment) {
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
