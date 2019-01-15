package com.xasfemr.meiyaya.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.MyCourseSubscribeActivity;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */

/**
 * 我的课程和我的订阅原本是一个页面,两个Fragment左右滑动(和我的消息页面效果一致),后面改为两个页面,
 * 所以这个MyCourseSubscribeActivity已经不在使用.
 * MyCourseFragment是用来填充在MyCourseSubscribeActivity中的,所以也不在使用.
 */
public class MyCourseFragment extends BaseFragment {


    private MyCourseSubscribeActivity mCourSubsActivity;
    private RecyclerView              rvMyCourse;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCourSubsActivity = (MyCourseSubscribeActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(mCourSubsActivity, R.layout.fragment_my_course, null);
        rvMyCourse = (RecyclerView) view.findViewById(R.id.rv_my_course);
        return view;
    }

    @Override
    public void initData() {

        rvMyCourse.setLayoutManager(new GridLayoutManager(mCourSubsActivity, 2));
        rvMyCourse.setAdapter(new CourSubsAdapter());

    }


    private class CourSubsAdapter extends RecyclerView.Adapter<CourSubsHolder> {

        @Override
        public CourSubsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(mCourSubsActivity, R.layout.item_course_subscribe_single, null);
            CourSubsHolder courSubsHolder = new CourSubsHolder(view);
            return courSubsHolder;
        }

        @Override
        public void onBindViewHolder(CourSubsHolder holder, int position) {
            holder.tvCourseTitle.setVisibility(View.VISIBLE);
        }

        @Override
        public int getItemCount() {
            return 8;
        }
    }


    static class CourSubsHolder extends RecyclerView.ViewHolder {

        public RelativeLayout  rlCourseScreenshot;
        public ImageView       ivCourseScreenshot;
        public TextView        tvCourseTimeDuration;
        public TextView        tvCoursePlayNumber;
        public TextView        tvCourseTitle;
        public RelativeLayout  rlCourseInfo;
        public CircleImageView ivUserIcon;
        public TextView        tvUserName;
        public ImageView       ivUserCourSubs;
        public TextView        tvUserCourseDes;

        public CourSubsHolder(View itemView) {
            super(itemView);
            rlCourseScreenshot = (RelativeLayout) itemView.findViewById(R.id.rl_course_screenshot);
            ivCourseScreenshot = (ImageView) itemView.findViewById(R.id.iv_course_screenshot);
            tvCourseTimeDuration = (TextView) itemView.findViewById(R.id.tv_course_time_duration);
            tvCoursePlayNumber = (TextView) itemView.findViewById(R.id.tv_course_play_number);
            tvCourseTitle = (TextView) itemView.findViewById(R.id.tv_course_title);
            rlCourseInfo = (RelativeLayout) itemView.findViewById(R.id.rl_course_info);
            ivUserIcon = (CircleImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            ivUserCourSubs = (ImageView) itemView.findViewById(R.id.iv_user_course_subscribe);
            tvUserCourseDes = (TextView) itemView.findViewById(R.id.tv_user_course_des);
        }
    }


}
