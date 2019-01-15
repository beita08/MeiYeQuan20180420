package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;

/**
 * 首页--会员
 */
public class MemberActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MemberActivity";

    private final String[]   firstClassStr  = {"美业分析", "美容机构内控", "美容机构外控", "中医养生", "皮肤护理", "职业规划", "供应商"};
    private final String[][] secondClassStr = {{"标准划分", "战略分析", "产品质量", "品牌形象", "国际美容", "其他"}, {"组织结构", "项目框架", "薪酬体系", "招聘系统", "员工培训", "其他"}, {"股权策略", "项目众筹", "拓客锁客", "营销策略", "店面扩张", "其他"}, {"按摩手法", "饮食养生", "养生秘笈", "四季养生", "五行养生", "其他"}, {"护理常识", "护理技巧", "护理要素", "饮食习惯", "美容误区", "其他"}, {"职业规划", "销售技巧", "职业素养", "服务质量", "专业知识", "业绩飙升", "其他"}, {"需求定位", "供应商管理", "竞争趋势", "其他"}};

    private TextView  tvMemberHot;
    private TextView  tvMemberPlayback;
    private ViewPager vpMemberCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        initTopBar();
        setTopTitleText("会员");

        Intent intent = getIntent();
        int course = intent.getIntExtra("COURSE", 0);
        if (course == 10) {
            int[] classArr = intent.getIntArrayExtra("CLASS");
            LogUtils.show(TAG, "CLASS---" + classArr[0] + "---" + classArr[1]);
            String title = secondClassStr[classArr[0] - 1][classArr[1] - 1];
            setTopTitleText(title);
        }

        tvMemberHot = (TextView) findViewById(R.id.tv_member_course_hot);
        tvMemberPlayback = (TextView) findViewById(R.id.tv_member_course_playback);
        vpMemberCourse = (ViewPager) findViewById(R.id.vp_member_course);

        tvMemberHot.setOnClickListener(this);
        tvMemberPlayback.setOnClickListener(this);

        //        vpMemberCourse.setAdapter(new MemberCourseAdapter(getFragmentManager()));
        vpMemberCourse.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tvMemberHot.setSelected(true);
                    tvMemberPlayback.setSelected(false);
                } else if (position == 1) {
                    tvMemberHot.setSelected(false);
                    tvMemberPlayback.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tvMemberHot.setSelected(true);
        tvMemberPlayback.setSelected(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_member_course_hot:
                vpMemberCourse.setCurrentItem(0);
                break;
            case R.id.tv_member_course_playback:
                vpMemberCourse.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    //    private class MemberCourseAdapter extends FragmentPagerAdapter {
    //
    //        public MemberCourseAdapter(FragmentManager fm) {
    //            super(fm);
    //        }
    //
    //        @Override
    //        public Fragment getItem(int position) {
    //            BaseFragment baseFragment = MemberCourseFragmentFactory.getFragment(position);
    //            return baseFragment;
    //        }
    //
    //        @Override
    //        public int getCount() {
    //            return 2;
    //        }
    //    }
}