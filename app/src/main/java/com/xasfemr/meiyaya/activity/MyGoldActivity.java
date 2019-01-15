package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.bean.CurrentMeUser;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.module.college.activity.WebViewActivity;
import com.xasfemr.meiyaya.module.mine.activity.WithdrawActivity;
import com.xasfemr.meiyaya.utils.LogUtils;
import com.xasfemr.meiyaya.utils.SPUtils;
import com.xasfemr.meiyaya.utils.ToastUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.simple.eventbus.EventBus;

import okhttp3.Call;

public class MyGoldActivity extends BaseActivity implements View.OnClickListener {

    private Intent   mIntent;
    private boolean  isLecturer;
//    private int      goldNumber;
    private TextView tvMyGoldDetail;
    private TextView tvMyGoldNumber;
    private TextView tvMyGoldRecharge;
    private TextView tvMyGoldGetCash;
    private TextView tvMyGoldNoCash;
    private TextView tvMyGoldAbout;

    private int rate; //兑换金币比例
    private String uname="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gold);
        initTopBar();
        setTopTitleText("我的金币");
        showTopRight();
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        isLecturer = intent.getBooleanExtra("IS_LECTURER", false);
        //goldNumber = intent.getIntExtra("GOLD_NUMBER", 0);
        rate=intent.getIntExtra("RATE",0);
        uname=intent.getStringExtra("UNAME");

        tvMyGoldDetail = (TextView) findViewById(R.id.tv_top_right);
        tvMyGoldNumber = (TextView) findViewById(R.id.tv_my_gold_number);
        tvMyGoldRecharge = (TextView) findViewById(R.id.tv_my_gold_recharge);
        tvMyGoldGetCash = (TextView) findViewById(R.id.tv_my_gold_get_cash);
        tvMyGoldNoCash = (TextView) findViewById(R.id.tv_my_gold_no_cash);
        tvMyGoldAbout = (TextView) findViewById(R.id.tv_my_gold_about_gold);

        tvMyGoldDetail.setOnClickListener(this);
        tvMyGoldRecharge.setOnClickListener(this);
        tvMyGoldGetCash.setOnClickListener(this);
        tvMyGoldAbout.setOnClickListener(this);


        if (isLecturer) {//是讲师
            tvMyGoldGetCash.setEnabled(true);
            tvMyGoldGetCash.setBackgroundResource(R.drawable.selector_button_def_white);
            tvMyGoldGetCash.setTextColor(this.getResources().getColorStateList(R.color.selector_button_text_def_red));
            tvMyGoldNoCash.setVisibility(View.GONE);
        } else { //不是讲师
            tvMyGoldGetCash.setEnabled(false);
            tvMyGoldGetCash.setBackgroundResource(R.drawable.shape_gray_oval_frame);
            tvMyGoldGetCash.setTextColor(0xFFB9B9B9);
            tvMyGoldNoCash.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvMyGoldNumber.setText("" + SPUtils.getInt(this,GlobalConstants.USER_GOLD_NUMBER,0));
    }

    @Override
    public void onClick(View v) {
        if (mIntent == null) {
            mIntent = new Intent();
        }
        switch (v.getId()) {
            case R.id.tv_top_right: //金币明细
                mIntent.setClass(MyGoldActivity.this, MyGoldDetailListActivity.class);
                startActivity(mIntent);
                break;

            case R.id.tv_my_gold_recharge: //充值
                mIntent.setClass(MyGoldActivity.this, MyGoldRechargeActivity.class);
                startActivity(mIntent);
                break;

            case R.id.tv_my_gold_get_cash: //提现

                mIntent.setClass(MyGoldActivity.this, WithdrawActivity.class);
                mIntent.putExtra("RATE",rate);
                mIntent.putExtra("UNAME",uname);
                finish();
                startActivity(mIntent);
//                if (goldNumber >= 100) {
//
//                } else {
//                    ToastUtil.showShort(this, "至少'100'个金币才能提现哦~");
//                }
                break;
            case R.id.tv_my_gold_about_gold: //关于金币
                mIntent.setClass(MyGoldActivity.this, WebViewActivity.class);
                mIntent.putExtra("url", GlobalConstants.H5_ABOUT_GOLD);
                mIntent.putExtra("title", "关于金币");
                mIntent.putExtra("news", false);

                startActivity(mIntent);
                break;

            default:
                break;
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        getDataFromServer(SPUtils.getString(this, GlobalConstants.phoneNumber, ""));
//    }

//    @Subscriber(mode = ThreadMode.MAIN,tag = GlobalConstants.EventBus.UPDATE_USER_DATE)
//    public void updateData(String number){
//        LogUtils.show("更新信息","---");
//        getDataFromServer(SPUtils.getString(this, GlobalConstants.phoneNumber, ""));
//    }

    private void getDataFromServer(String phoneNumber) {

        OkHttpUtils.get().url(GlobalConstants.URL_USER_ME)
                .addParams("phoneNumber", phoneNumber)  //13720549469
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort(MyGoldActivity.this,"网络访问失败!");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        //保存'我的'
                        try {
                            LogUtils.show("金额。。。",response);
                            parseJson(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void parseJson(String json) {
        Gson gson = new Gson();
        CurrentMeUser currentMeUser = gson.fromJson(json, CurrentMeUser.class);
        tvMyGoldNumber.setText(currentMeUser.data.goldmoney+"");
        //将用户金额存至本地
        SPUtils.putInt(this, GlobalConstants.USER_GOLD_NUMBER, currentMeUser.data.goldmoney);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
