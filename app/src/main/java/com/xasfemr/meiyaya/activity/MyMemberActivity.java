package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.MyMemberData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class MyMemberActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MyMemberActivity";

    private Intent         mIntent;
    private String         userName;
    private String         userIcon;
    private boolean        isMember;
    private String         memberOvertime;
    private LinearLayout   llMyMemberYes;
    private ImageView      ivMyUserIcon;
    private TextView       tvMyUserName;
    private TextView       tvMyMemberTime;
    private ImageView      ivMyMemberRenew;
    private RelativeLayout rlMyMemberNo;
    private TextView       tvMyMemberOpen;
    private TextView       tvMyMemberPriviCourse;
    private TextView       tvMyMemberProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_member);
        initTopBar();
        setTopTitleText("我的会员");

        Intent intent = getIntent();
        userName = intent.getStringExtra("USER_NAME");
        userIcon = intent.getStringExtra("USER_ICON");
        isMember = intent.getBooleanExtra("IS_MEMBER", false);
        memberOvertime = intent.getStringExtra("MEMBER_OVERTIME");

        llMyMemberYes = (LinearLayout) findViewById(R.id.ll_my_member_yes);
        ivMyUserIcon = (ImageView) findViewById(R.id.iv_my_user_icon);
        tvMyUserName = (TextView) findViewById(R.id.tv_my_user_name);
        tvMyMemberTime = (TextView) findViewById(R.id.tv_my_member_time);
        ivMyMemberRenew = (ImageView) findViewById(R.id.iv_my_member_immediately_renew);
        rlMyMemberNo = (RelativeLayout) findViewById(R.id.rl_my_member_no);
        tvMyMemberOpen = (TextView) findViewById(R.id.tv_my_member_open);
        tvMyMemberPriviCourse = (TextView) findViewById(R.id.tv_my_member_privileges_course);
        tvMyMemberProtocol = (TextView) findViewById(R.id.tv_my_member_protocol);

        ivMyUserIcon.setOnClickListener(this);
        ivMyMemberRenew.setOnClickListener(this);
        tvMyMemberOpen.setOnClickListener(this);
        tvMyMemberPriviCourse.setOnClickListener(this);
        tvMyMemberProtocol.setOnClickListener(this);

        if (isMember) {
            llMyMemberYes.setVisibility(View.VISIBLE);
            rlMyMemberNo.setVisibility(View.GONE);
            Glide.with(MyMemberActivity.this).load(userIcon).into(ivMyUserIcon);
            tvMyUserName.setText(userName);
            tvMyMemberTime.setText("您的会员将于" + memberOvertime + "到期");
        } else {
            llMyMemberYes.setVisibility(View.GONE);
            rlMyMemberNo.setVisibility(View.VISIBLE);
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        String mUserId = SPUtils.getString(this, GlobalConstants.userID, "");
        OkHttpUtils.get().url(GlobalConstants.URL_MY_MEMBER)
                .addParams("id", mUserId)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyMemberActivity.this, "网络异常,请检查你的网络", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        LogUtils.show(TAG, "----response = " + response + " ----");
                        try {
                            parserMyMemberJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void parserMyMemberJson(String response) {
        Gson gson = new Gson();
        MyMemberData myMemberData = gson.fromJson(response, MyMemberData.class);

        if (myMemberData != null && myMemberData.data != null) {

            if (TextUtils.equals(myMemberData.data.ustatus, "1")) {//是会员
                llMyMemberYes.setVisibility(View.VISIBLE);
                rlMyMemberNo.setVisibility(View.GONE);
                Glide.with(MyMemberActivity.this).load(myMemberData.data.images).into(ivMyUserIcon);
                tvMyUserName.setText(myMemberData.data.username);
                tvMyMemberTime.setText("您的会员将于" + myMemberData.data.ustatus_overtime + "到期");

            } else {  //不是会员
                llMyMemberYes.setVisibility(View.GONE);
                rlMyMemberNo.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (mIntent == null) {
            mIntent = new Intent();
        }

        switch (v.getId()) {
            case R.id.iv_my_user_icon://用户头像

                break;
            case R.id.iv_my_member_immediately_renew://是会员 , 立即续费
                mIntent.setClass(MyMemberActivity.this, MyMemberRechargeActivity.class);
                mIntent.putExtra("USER_NAME", userName);
                mIntent.putExtra("USER_ICON", userIcon);
                startActivity(mIntent);
                break;
            case R.id.tv_my_member_open://不是会员, 开通会员
                mIntent.setClass(MyMemberActivity.this, MyMemberRechargeActivity.class);
                mIntent.putExtra("USER_NAME", userName);
                mIntent.putExtra("USER_ICON", userIcon);
                startActivity(mIntent);
                break;
            case R.id.tv_my_member_privileges_course://会员特权专享课程

                break;
            case R.id.tv_my_member_protocol://付费会员服务协议

                break;
            default:
                break;
        }
    }
}
