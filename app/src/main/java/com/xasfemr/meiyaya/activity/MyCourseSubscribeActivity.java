package com.xasfemr.meiyaya.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.fragment.BaseFragment;
import com.xasfemr.meiyaya.fragment.factory.CourSubsFragmentFactory;
import com.xasfemr.meiyaya.main.BaseActivity;

/**
 * 我的课程和我的订阅原本是一个页面,两个Fragment左右滑动(和我的消息页面效果一致),后面改为两个页面,
 * 所以这个MyCourseSubscribeActivity已经不在使用.
 */

public class MyCourseSubscribeActivity extends BaseActivity implements View.OnClickListener {

    private TextView  tvMyCourse;
    private TextView  tvMySubscribe;
    private ViewPager vpMyCourSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course_subscribe);
        initTopBar();
        setTopTitleText("我的课程");

        tvMyCourse = (TextView) findViewById(R.id.tv_my_course);
        tvMySubscribe = (TextView) findViewById(R.id.tv_my_subscribe);
        vpMyCourSubs = (ViewPager) findViewById(R.id.vp_my_course_and_subscribe);

        tvMyCourse.setOnClickListener(this);
        tvMySubscribe.setOnClickListener(this);

        vpMyCourSubs.setAdapter(new CourSubsAdapter(getFragmentManager()));
        vpMyCourSubs.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setTopTitleText("我的课程");
                    tvMyCourse.setSelected(true);
                    tvMySubscribe.setSelected(false);
                } else if (position == 1) {
                    setTopTitleText("我的收藏");
                    tvMyCourse.setSelected(false);
                    tvMySubscribe.setSelected(true);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        //初始化,根据传入的参数进行初始化
        Intent intent = getIntent();
        int courOrSubs = intent.getIntExtra("COUR_OR_SUBS", 0);
        if (courOrSubs == 0) {
            setTopTitleText("我的课程");
            tvMyCourse.setSelected(true);
            tvMySubscribe.setSelected(false);
            vpMyCourSubs.setCurrentItem(0);
        } else if(courOrSubs == 1){
            setTopTitleText("我的收藏");
            tvMyCourse.setSelected(false);
            tvMySubscribe.setSelected(true);
            vpMyCourSubs.setCurrentItem(1);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_my_course:
                vpMyCourSubs.setCurrentItem(0);
            break;
            case R.id.tv_my_subscribe:
                vpMyCourSubs.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    private class CourSubsAdapter extends FragmentPagerAdapter{

        public CourSubsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            BaseFragment baseFragment = CourSubsFragmentFactory.getFragment(position);
            return baseFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
