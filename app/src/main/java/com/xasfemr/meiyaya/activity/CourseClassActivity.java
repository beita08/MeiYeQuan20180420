package com.xasfemr.meiyaya.activity;

import android.os.Bundle;

import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

public class CourseClassActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_class);
        initTopBar();
        setTopTitleText("课程");

    }
}
