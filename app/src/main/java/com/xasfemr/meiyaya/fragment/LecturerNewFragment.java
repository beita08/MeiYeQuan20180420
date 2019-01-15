package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.module.home.activity.LecturerActivity;
import com.xasfemr.meiyaya.activity.UserPagerActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class LecturerNewFragment extends BaseFragment {

    private LecturerActivity lecturerActivity;
    private RecyclerView     rvLectureNew;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lecturerActivity = (LecturerActivity) getActivity();
    }

    @Override
    public View initView() {
        View view = View.inflate(lecturerActivity, R.layout.fragment_lecturer_new, null);
        rvLectureNew = (RecyclerView) view.findViewById(R.id.rv_lecture_new);
        return view;
    }

    @Override
    public void initData() {
        rvLectureNew.setLayoutManager(new GridLayoutManager(lecturerActivity, 3));
        rvLectureNew.setAdapter(new LectureAdapter());
    }

    private class LectureAdapter extends RecyclerView.Adapter<LectureHolder>{

        @Override
        public LectureHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(lecturerActivity, R.layout.item_college_lecture, null);
            LectureHolder lectureHolder = new LectureHolder(view);
            return lectureHolder;
        }

        @Override
        public void onBindViewHolder(LectureHolder holder, int position) {

            holder.ivLecIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(lecturerActivity, UserPagerActivity.class);
                    lecturerActivity.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return 6;
        }
    }


    static class LectureHolder extends RecyclerView.ViewHolder {

        public ImageView ivLecIcon;
        public TextView  tvLecName;
        public TextView  tvLecFans;
        public ImageView ivLecFocus;

        public LectureHolder(View itemView) {
            super(itemView);
            ivLecIcon = (ImageView) itemView.findViewById(R.id.iv_lec_icon);
            tvLecName = (TextView) itemView.findViewById(R.id.tv_lec_name);
            tvLecFans = (TextView) itemView.findViewById(R.id.tv_lec_fans);
            ivLecFocus = (ImageView) itemView.findViewById(R.id.iv_lec_focus);
        }
    }



}
