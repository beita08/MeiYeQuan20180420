package com.xasfemr.meiyaya.module.home.activity;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.base.activity.MVPBaseActivity;
import com.xasfemr.meiyaya.fragment.MemberCourseHotFragment;
import com.xasfemr.meiyaya.fragment.MemberCoursePlaybackFragment;
import com.xasfemr.meiyaya.module.home.adapter.CoursePageAdapter;
import com.xasfemr.meiyaya.view.LoadDataView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 课程详情
 * 此页面需传 直播与回放两接口
 * Created by sen.luo on 2017/11/29.
 */

public class CourseDetailActivity extends MVPBaseActivity{

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

    private String net_typeid="";


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

        if (TextUtils.isEmpty(getIntent().getStringExtra("net_typeid"))||
        TextUtils.isEmpty(getIntent().getStringExtra("cname"))){
            return;
        }

        tvTitle.setText(getIntent().getStringExtra("cname"));
        net_typeid=getIntent().getStringExtra("net_typeid");


        listragment=new ArrayList<>();
        for (int i=0;i<mTitles.length;i++) {
            switch (i) {
                case 0:
                    listragment.add(new MemberCourseHotFragment(net_typeid));
                    break;
                case 1:
                    listragment.add(new MemberCoursePlaybackFragment(net_typeid));
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
