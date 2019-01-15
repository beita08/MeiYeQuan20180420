package com.xasfemr.meiyaya.module.home.activity;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.module.home.adapter.CoursePageAdapter;
import com.xasfemr.meiyaya.module.home.fragment.MemberLiveFragment;
import com.xasfemr.meiyaya.module.home.fragment.MemberPlaybackFragment;
import com.xasfemr.meiyaya.view.LoadDataView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 首页--会员课程
 * Created by sen.luo on 2017/11/29.
 */

public class MemberNewActivity extends MVPBaseActivity{

    private final String[]mTitles={"在线课程","精彩回放"};
    private List<Fragment> listragment;

    @BindView(R.id.tab_layout_course)
    TabLayout tabLayoutCourse;
    @BindView(R.id.course_view_pager)
    ViewPager courseViewPager;
    @BindView(R.id.tv_top_title)
    TextView tvTitle;
    @BindView(R.id.iv_top_back)
    ImageView ivBack;
    @Override
    protected int layoutId() {
        return R.layout.activity_course_detail;
    }

    @Override
    protected ViewGroup loadDataViewLayout() {
        return null;
    }

    @Override
    protected void initView() {
        ivBack.setOnClickListener(v -> finish());
        tvTitle.setText("会员课程");

        listragment=new ArrayList<>();
        for (int i=0;i<mTitles.length;i++) {
            switch (i) {
                case 0:
                    listragment.add(new MemberLiveFragment());
                    break;
                case 1:
                    listragment.add(new MemberPlaybackFragment());
                    break;
            }
        }

        courseViewPager.setAdapter(new CoursePageAdapter(this.getFragmentManager(),listragment));
        tabLayoutCourse.setupWithViewPager(courseViewPager);
        tabLayoutCourse.setTabMode(TabLayout.MODE_FIXED);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void getLoadView(LoadDataView loadView) {

    }

    @Override
    protected void initPresenter() {

    }
}
