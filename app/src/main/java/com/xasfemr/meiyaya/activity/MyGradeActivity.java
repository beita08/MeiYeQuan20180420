package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.main.BaseActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyGradeActivity extends BaseActivity {

    private TextView        tvTopRight;
    private CircleImageView civUserIcon;
    private TextView        tvUserGrade;
    private ImageView       ivGradeFloor;
    private ProgressBar     progressBar;
    private ImageView       ivGradeCeiling;
    private TextView        tvGradeFloor;
    private TextView        tvGradeCeiling;
    private TextView        tvGradeGrowthLeft;
    private TextView        tvGradeGrowth;
    private TextView        tvGradeGrowthRight;
    private LinearLayout    llGradeGrowth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_user_grade);
        initTopBar();
        setTopTitleText("我的等级");

        tvTopRight = (TextView) findViewById(R.id.tv_top_right);
        civUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        tvUserGrade = (TextView) findViewById(R.id.tv_user_grade);
        ivGradeFloor = (ImageView) findViewById(R.id.iv_grade_floor);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ivGradeCeiling = (ImageView) findViewById(R.id.iv_grade_ceiling);
        tvGradeFloor = (TextView) findViewById(R.id.tv_grade_floor);
        tvGradeCeiling = (TextView) findViewById(R.id.tv_grade_ceiling);
        tvGradeGrowthLeft = (TextView) findViewById(R.id.tv_grade_growth_left);
        tvGradeGrowth = (TextView) findViewById(R.id.tv_grade_growth);
        tvGradeGrowthRight = (TextView) findViewById(R.id.tv_grade_growth_right);
        llGradeGrowth = (LinearLayout) findViewById(R.id.ll_grade_growth);

        setTopRight();

        Intent intent = getIntent();
        int userGrowth = intent.getIntExtra("GROWTH", 0);
        String userName = intent.getStringExtra("USER_NAME");
        String userIcon = intent.getStringExtra("USER_ICON");
        Glide.with(MyGradeActivity.this).load(userIcon).into(civUserIcon);

        setUserGrowthProgress(userGrowth);
    }

    private void setTopRight() {
        tvTopRight.setVisibility(View.VISIBLE);
        tvTopRight.setText("等级规则");
        tvTopRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyGradeActivity.this, MyGradePrivilegeActivity.class);
                startActivity(intent);
            }
        });

    }


    private void setUserGrowthProgress(int userGrowth) {

        tvGradeGrowth.setText("" + userGrowth);

        LinearLayout.LayoutParams paramsLeft = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams paramsRight = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (userGrowth >= 0 && userGrowth <= 100) {
            System.out.println("LV.1");
            tvUserGrade.setText("LV.1");

            ivGradeFloor.setImageResource(R.drawable.grade_v1);
            ivGradeCeiling.setImageResource(R.drawable.grade_v2);
            progressBar.setMax(100);
            progressBar.setProgress(userGrowth - 0);
            tvGradeFloor.setText("0");
            tvGradeCeiling.setText("100");

            paramsLeft.weight = Math.abs(userGrowth - 0);
            paramsRight.weight = Math.abs(100 - userGrowth);

        } else if (userGrowth >= 101 && userGrowth <= 300) {
            System.out.println("LV.2");
            tvUserGrade.setText("LV.2");

            ivGradeFloor.setImageResource(R.drawable.grade_v2);
            ivGradeCeiling.setImageResource(R.drawable.grade_v3);
            progressBar.setMax(200);
            progressBar.setProgress((userGrowth - 100));
            tvGradeFloor.setText("101");
            tvGradeCeiling.setText("300");

            paramsLeft.weight = Math.abs(userGrowth - 100);
            paramsRight.weight = Math.abs(300 - userGrowth);

        } else if (userGrowth >= 301 && userGrowth <= 500) {
            System.out.println("LV.3");
            tvUserGrade.setText("LV.3");

            ivGradeFloor.setImageResource(R.drawable.grade_v3);
            ivGradeCeiling.setImageResource(R.drawable.grade_v4);
            progressBar.setMax(200);
            progressBar.setProgress((userGrowth - 300));
            tvGradeFloor.setText("301");
            tvGradeCeiling.setText("500");

            paramsLeft.weight = Math.abs(userGrowth - 300);
            paramsRight.weight = Math.abs(500 - userGrowth);

        } else if (userGrowth >= 501 && userGrowth <= 1000) {
            System.out.println("LV.4");
            tvUserGrade.setText("LV.4");

            ivGradeFloor.setImageResource(R.drawable.grade_v4);
            ivGradeCeiling.setImageResource(R.drawable.grade_v5);
            progressBar.setMax(500);
            progressBar.setProgress((userGrowth - 500));
            tvGradeFloor.setText("501");
            tvGradeCeiling.setText("1000");

            paramsLeft.weight = Math.abs(userGrowth - 500);
            paramsRight.weight = Math.abs(1000 - userGrowth);

        } else if (userGrowth >= 1001 && userGrowth <= 2000) {
            System.out.println("LV.5");
            tvUserGrade.setText("LV.5");

            ivGradeFloor.setImageResource(R.drawable.grade_v5);
            ivGradeCeiling.setImageResource(R.drawable.grade_v6);
            progressBar.setMax(1000);
            progressBar.setProgress((userGrowth - 1000));
            tvGradeFloor.setText("1001");
            tvGradeCeiling.setText("2000");

            paramsLeft.weight = Math.abs(userGrowth - 1000);
            paramsRight.weight = Math.abs(2000 - userGrowth);

        } else if (userGrowth >= 2001 && userGrowth <= 3500) {
            System.out.println("LV.6");
            tvUserGrade.setText("LV.6");

            ivGradeFloor.setImageResource(R.drawable.grade_v6);
            ivGradeCeiling.setImageResource(R.drawable.grade_v7);
            progressBar.setMax(1500);
            progressBar.setProgress((userGrowth - 2000));
            tvGradeFloor.setText("2001");
            tvGradeCeiling.setText("3500");

            paramsLeft.weight = Math.abs(userGrowth - 2000);
            paramsRight.weight = Math.abs(3500 - userGrowth);

        } else if (userGrowth >= 3501 && userGrowth <= 5500) {
            System.out.println("LV.7");
            tvUserGrade.setText("LV.7");

            ivGradeFloor.setImageResource(R.drawable.grade_v7);
            ivGradeCeiling.setImageResource(R.drawable.grade_v8);
            progressBar.setMax(2000);
            progressBar.setProgress((userGrowth - 3500));
            tvGradeFloor.setText("3501");
            tvGradeCeiling.setText("5500");

            paramsLeft.weight = Math.abs(userGrowth - 3500);
            paramsRight.weight = Math.abs(5500 - userGrowth);

        } else if (userGrowth >= 5501 && userGrowth <= 8000) {
            System.out.println("LV.8");
            tvUserGrade.setText("LV.8");

            ivGradeFloor.setImageResource(R.drawable.grade_v8);
            ivGradeCeiling.setImageResource(R.drawable.grade_v9);
            progressBar.setMax(2500);
            progressBar.setProgress((userGrowth - 5500));
            tvGradeFloor.setText("5501");
            tvGradeCeiling.setText("8000");

            paramsLeft.weight = Math.abs(userGrowth - 5500);
            paramsRight.weight = Math.abs(8000 - userGrowth);

        } else if (userGrowth >= 8001 && userGrowth <= 11000) {
            System.out.println("LV.9");
            tvUserGrade.setText("LV.9");

            ivGradeFloor.setImageResource(R.drawable.grade_v9);
            ivGradeCeiling.setImageResource(R.drawable.grade_v10);
            progressBar.setMax(3000);
            progressBar.setProgress((userGrowth - 8000));
            tvGradeFloor.setText("8001");
            tvGradeCeiling.setText("11000");

            paramsLeft.weight = Math.abs(userGrowth - 8000);
            paramsRight.weight = Math.abs(11000 - userGrowth);

        } else if (userGrowth > 11000) {
            System.out.println("LV.10");
            tvUserGrade.setText("LV.10");
            //初期等级到这里已经够用了.后续再加
        } else {
            System.out.println("数据有误");
        }

        tvGradeGrowthLeft.setLayoutParams(paramsLeft);
        tvGradeGrowthRight.setLayoutParams(paramsRight);
    }

}
