package com.xasfemr.meiyaya.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.xasfemr.meiyaya.R;
import com.xasfemr.meiyaya.global.GlobalConstants;
import com.xasfemr.meiyaya.main.BaseActivity;
import com.xasfemr.meiyaya.main.MainActivity;
import com.xasfemr.meiyaya.utils.SPUtils;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveEndActivity extends BaseActivity {

    private CircleImageView civUserIcon;
    private TextView        tvUserName;
    private TextView        tvFansNum;
    private TextView        tvGoldNum;
    private TextView        tvLookerNum;
    private TextView        tvEndConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_end);
        civUserIcon = (CircleImageView) findViewById(R.id.civ_user_icon);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvFansNum = (TextView) findViewById(R.id.tv_live_fans_num);
        tvGoldNum = (TextView) findViewById(R.id.tv_live_gold_num);
        tvLookerNum = (TextView) findViewById(R.id.tv_live_looker_num);
        tvEndConfirm = (TextView) findViewById(R.id.tv_live_end_confirm);

        if (!TextUtils.isEmpty(getIntent().getStringExtra("nums"))){
            tvLookerNum.setText(getIntent().getStringExtra("nums"));
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("goldmoney"))){
            tvFansNum.setText(getIntent().getStringExtra("goldmoney"));
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("goldmoney_in"))){
            tvGoldNum.setText(getIntent().getStringExtra("goldmoney_in"));
        }


        tvUserName.setText(SPUtils.getString(this, GlobalConstants.USER_NAME,""));

        Glide.with(this).load(SPUtils.getString(this,GlobalConstants.USER_HEAD_IMAGE,"")).apply(new RequestOptions().placeholder(R.drawable.meiyaya_logo_round_gray)).into(civUserIcon);






        tvEndConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LiveEndActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
