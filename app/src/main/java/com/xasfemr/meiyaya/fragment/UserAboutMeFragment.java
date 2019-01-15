package com.xasfemr.meiyaya.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.activity.UserPagerActivity;
import com.xasfemr.meiyaya.bean.LookUserData;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAboutMeFragment extends BaseFragment {
    private static final String TAG = "UserAboutMeFragment";

    private UserPagerActivity userPagerActivity;
    private String            lookUserId;

    private TextView tvGrade;
    private TextView tvYearsOld;
    private TextView tvGender;
    private TextView tvDistrict;
    private TextView tvProfession;
    private TextView tvIntroduce;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userPagerActivity = (UserPagerActivity) getActivity();
        lookUserId = userPagerActivity.getLookUserId();
    }

    @Override
    public View initView() {
        View view = View.inflate(userPagerActivity, R.layout.fragment_user_about_me, null);
        tvGrade = (TextView) view.findViewById(R.id.tv_user_grade);
        tvYearsOld = (TextView) view.findViewById(R.id.tv_user_years_old);
        tvGender = (TextView) view.findViewById(R.id.tv_user_gender);
        tvDistrict = (TextView) view.findViewById(R.id.tv_user_district);
        tvProfession = (TextView) view.findViewById(R.id.tv_user_profession);
        tvIntroduce = (TextView) view.findViewById(R.id.tv_user_introduce);
        return view;
    }

    @Override
    public void initData() {
        System.out.println("2222222222222++++++++++++++++++++======================");
        //从服务器获取用户信息
        gotoGetUserInfo();
    }

    /*@Override
    public void onStart() {
        super.onStart();
        gotoGetUserInfo();
    }*/

    private void gotoGetUserInfo() {
        String mUserId = SPUtils.getString(userPagerActivity, GlobalConstants.userID, "");

        OkHttpUtils.get().url(GlobalConstants.URL_LOOK_USER_PAGER)
                .addParams("id", mUserId)       //客户端id用字段id提交
                .addParams("uid", lookUserId)   //被点击的头像的id用字段uid提交
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.i(TAG, "onError: ---网络异常,获取用户数据失败---");
                        Toast.makeText(userPagerActivity, "网络异常,获取用户数据失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse: 用户数据:response = ---" + response + "---");
                        try {
                            parserUserInfoJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: .....解析用户数据出现异常.....");
                        }
                    }
                });
    }

    private void parserUserInfoJson(String response) {
        Gson gson = new Gson();
        LookUserData lookUserData = gson.fromJson(response, LookUserData.class);

        tvYearsOld.setText(lookUserData.data.age + "");
        //用户性别 sex：性别（0未知，1男，2女）
        if (lookUserData.data.sex == 1) {
            tvGender.setText("男");
        } else if (lookUserData.data.sex == 2) {
            tvGender.setText("女");
        } else {
            tvGender.setText("");
        }
        tvDistrict.setText(lookUserData.data.region);
        tvProfession.setText(lookUserData.data.profession);
        tvIntroduce.setText(lookUserData.data.signture);

        //成长值,等级划分
        int userGrowth = lookUserData.data.growth;
        if (userGrowth >= 0 && userGrowth <= 100) {
            System.out.println("LV.1");
            tvGrade.setText("LV.1");
        } else if (userGrowth >= 101 && userGrowth <= 300) {
            System.out.println("LV.2");
            tvGrade.setText("LV.2");
        } else if (userGrowth >= 301 && userGrowth <= 500) {
            System.out.println("LV.3");
            tvGrade.setText("LV.3");
        } else if (userGrowth >= 501 && userGrowth <= 1000) {
            System.out.println("LV.4");
            tvGrade.setText("LV.4");
        } else if (userGrowth >= 1001 && userGrowth <= 2000) {
            System.out.println("LV.5");
            tvGrade.setText("LV.5");
        } else if (userGrowth >= 2001 && userGrowth <= 3500) {
            System.out.println("LV.6");
            tvGrade.setText("LV.6");
        } else if (userGrowth >= 3501 && userGrowth <= 5500) {
            System.out.println("LV.7");
            tvGrade.setText("LV.7");
        } else if (userGrowth >= 5501 && userGrowth <= 8000) {
            System.out.println("LV.8");
            tvGrade.setText("LV.8");
        } else if (userGrowth >= 8001 && userGrowth <= 11000) {
            System.out.println("LV.9");
            tvGrade.setText("LV.9");
        } else if (userGrowth > 11000) {
            System.out.println("LV.10");
            tvGrade.setText("LV.10");
        } else {
            System.out.println("数据有误");
        }
    }
}
