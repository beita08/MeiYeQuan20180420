package com.xasfemr.meiyaya.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCourseActivity extends BaseActivity implements View.OnClickListener {

    private boolean boolEdit = false;

    private TextView       tvTopRight;
    private RecyclerView   rvMyCourse;
    private View           vEditBottomLine;
    private RelativeLayout rlEditBottomLine;
    private ImageView      ivEditAllSelect;
    private ImageView      ivEditDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_course);
        initTopBar();
        setTopTitleText("我的课程");

        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        rvMyCourse = (RecyclerView) findViewById(R.id.rv_my_course);
        vEditBottomLine = findViewById(R.id.view_edit_bottom_line);
        rlEditBottomLine = (RelativeLayout) findViewById(R.id.rl_edit_bottom_line);
        ivEditAllSelect = (ImageView) findViewById(R.id.iv_edit_all_select);
        ivEditDelete = (ImageView) findViewById(R.id.iv_edit_delete);

        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("编辑");
        tvTopRight.setOnClickListener(this);
        ivEditAllSelect.setOnClickListener(this);
        ivEditDelete.setOnClickListener(this);

        rvMyCourse.setLayoutManager(new GridLayoutManager(MyCourseActivity.this, 2));
        rvMyCourse.setAdapter(new CourSubsAdapter());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_top_right:
                if (boolEdit) {
                    tvTopRight.setText("编辑");
                    vEditBottomLine.setVisibility(View.GONE);
                    rlEditBottomLine.setVisibility(View.GONE);
                    boolEdit = false;
                } else {
                    tvTopRight.setText("完成");
                    vEditBottomLine.setVisibility(View.VISIBLE);
                    rlEditBottomLine.setVisibility(View.VISIBLE);
                    boolEdit = true;
                }
                rvMyCourse.setAdapter(new CourSubsAdapter());
                //TODO 控制RecyclerView中iv_course_edit_tag的显示和隐藏
                break;
            case R.id.iv_edit_all_select: //全选

                break;
            case R.id.iv_edit_delete:    //删除

                break;
            default:
                break;
        }
    }

    private class CourSubsAdapter extends RecyclerView.Adapter<CourSubsHolder> {

        @Override
        public CourSubsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(MyCourseActivity.this, R.layout.item_course_subscribe_single, null);
            CourSubsHolder courSubsHolder = new CourSubsHolder(view);
            return courSubsHolder;
        }

        @Override
        public void onBindViewHolder(CourSubsHolder holder, int position) {
            holder.tvCourseTitle.setVisibility(View.VISIBLE);

            if (boolEdit) {
                holder.ivCourseEditTag.setVisibility(View.VISIBLE);
            } else {
                holder.ivCourseEditTag.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }

    static class CourSubsHolder extends RecyclerView.ViewHolder {

        public RelativeLayout  rlCourseScreenshot;
        public ImageView       ivCourseScreenshot;
        public TextView        tvCourseTimeDuration;
        public TextView        tvCoursePlayNumber;
        public TextView        tvCourseTitle;
        public ImageView       ivCourseEditTag;
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
            ivCourseEditTag = (ImageView) itemView.findViewById(R.id.iv_course_edit_tag);
            rlCourseInfo = (RelativeLayout) itemView.findViewById(R.id.rl_course_info);
            ivUserIcon = (CircleImageView) itemView.findViewById(R.id.iv_user_icon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_user_name);
            ivUserCourSubs = (ImageView) itemView.findViewById(R.id.iv_user_course_subscribe);
            tvUserCourseDes = (TextView) itemView.findViewById(R.id.tv_user_course_des);
        }
    }
}
